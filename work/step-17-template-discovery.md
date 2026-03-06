# Step 17 — Convention-Based Template Discovery

## Summary

Replace the single `md-serve.template` configuration property with convention-based template discovery. The server reads templates from a `.md-serve/templates/` directory inside the source directory. Three named roles are recognised: `default.hbs` (for Markdown pages), `directory.hbs` (for directory listings), and `error.hbs` (for error pages). A built-in template is bundled for each role and used as a fallback when no user-provided template exists. Per-file template overrides are supported via a `template` key in YAML front matter.

## Background / Current Behavior

- `MdServeConfig` exposes `Optional<String> template()` — a single filesystem path to a custom template.
- `TemplateLoader` loads either that path or the built-in `default.hbs` from the classpath. All pages (file, directory, error) use the same template.
- `TemplateRenderer` holds one compiled `Template` and applies it unconditionally.
- There is no way to have different layouts for directory listings vs. content pages without editing the single template.
- The bundled `default.hbs` serves as the one built-in template.

## Desired Behavior

- `md-serve.template` is **removed** from `MdServeConfig`.
- On startup, the server looks for `<source-dir>/.md-serve/templates/` and loads any `.hbs` files found there by name (without extension).
- Three template roles are defined:

  | Role name     | Used by            | Bundled fallback              |
  |---------------|--------------------|-------------------------------|
  | `default`     | Markdown file pages | `templates/default.hbs`      |
  | `directory`   | Directory listings  | `templates/directory.hbs`    |
  | `error`       | Error pages         | `templates/error.hbs`        |

- A Markdown page can override its template by setting `template: <name>` in YAML front matter. The value must match a known template name (user-provided or bundled); if it does not, fall back to `default`.
- The `.md-serve/` directory is hidden from the request router by step 15's dot-prefix rule; its contents are never served directly.

## Scope

**In scope**
- Remove `md-serve.template` from `MdServeConfig` and `application.properties`.
- New `TemplateRegistry` bean replacing `TemplateLoader`: loads all templates by role name, with per-role classpath fallback.
- Bundled `directory.hbs` and `error.hbs` classpath templates.
- `TemplateRenderer.render()` accepts a template name; renderers pass the correct name.
- `FileRenderer` reads `frontmatter.get("template")` and forwards it to `TemplateRenderer`.
- `DirectoryRenderer` requests the `directory` template.
- `ErrorRenderer` requests the `error` template.
- Unit tests for `TemplateRegistry` (discovery, fallback, missing template).
- Integration tests verifying that a user-provided template in `<tempDir>/.md-serve/templates/` is picked up.

**Out of scope**
- Hot-reloading templates after startup.
- Template inheritance or partials.
- Configurable template directory path (always `.md-serve/templates/`).
- Role names beyond the three defined above.

## Implementation Plan

1. **Remove `md-serve.template`**: Delete the `template()` method from `MdServeConfig`. Remove it from `application.properties` docs. Update any test `@QuarkusTestProfile` overrides that set this property.

2. **Bundled templates**: Add `src/main/resources/templates/directory.hbs` and `src/main/resources/templates/error.hbs`. These are the built-in fallbacks. The existing `default.hbs` is unchanged in content but may gain minor structural adjustments for layout consistency with the two new templates.

3. **`TemplateRegistry`**: New `@ApplicationScoped` bean in `de.nikolassv.mdserve.template`. Inject `MdServeConfig` for `sourceDir`. On construction:
   - Compute `templateDir = sourceDir.resolve(".md-serve/templates")`.
   - For each of the three role names (`default`, `directory`, `error`):
     - Check whether `templateDir.resolve(name + ".hbs")` exists. If so, load it with `Files.readString()` + `new Handlebars().compileInline(source)`.
     - Otherwise, load the bundled classpath resource `/templates/<name>.hbs`.
   - Store results in `Map<String, Template>`.
   - Also load any *additional* `.hbs` files found in `templateDir` (for front-matter overrides), warn and skip any that fail to compile.

4. **`TemplateRenderer`**: Change the method signature from `render(TemplateContext ctx)` to `render(TemplateContext ctx, String templateName)`. Look up the template in `TemplateRegistry`; fall back to `default` if the name is not found.

5. **`FileRenderer`**: Extract `String templateName` from `doc.frontmatter().getOrDefault("template", "default").toString()`; pass it to `templateRenderer.render(ctx, templateName)`.

6. **`DirectoryRenderer`**: Call `templateRenderer.render(ctx, "directory")`.

7. **`ErrorRenderer`**: Call `templateRenderer.render(ctx, "error")`.

8. **Delete `TemplateLoader`**: It is fully superseded by `TemplateRegistry`.

9. **Tests**: See Testing Plan below.

## Relevant Code Pointers

- `src/main/java/de/nikolassv/mdserve/MdServeConfig.java` — remove `template()`
- `src/main/java/de/nikolassv/mdserve/template/TemplateLoader.java` — **delete**
- `src/main/java/de/nikolassv/mdserve/template/TemplateRenderer.java` — update method signature
- `src/main/java/de/nikolassv/mdserve/render/FileRenderer.java:28` — read `frontmatter.template`, pass to renderer
- `src/main/java/de/nikolassv/mdserve/render/DirectoryRenderer.java:22` — pass `"directory"` to renderer
- `src/main/java/de/nikolassv/mdserve/render/ErrorRenderer.java:33` — pass `"error"` to renderer
- `src/main/resources/templates/` — add `directory.hbs`, `error.hbs`

## Bundled Template Design

**`error.hbs`**: Shows the page title (e.g. "Not Found") as an `<h1>`, renders `{{{content}}}` below it, and includes the breadcrumb block from `default.hbs`. No file listing.

**`directory.hbs`**: Shows the directory name as an `<h1>`, renders the `{{#each files}}` file listing block from `default.hbs`. May omit the `{{{content}}}` block. Shares the same CSS base as `default.hbs`.

Both bundled templates should be recognisably similar in style to `default.hbs` but focused on their specific purpose.

## API / Configuration Changes

| Before | After |
|--------|-------|
| `md-serve.template=/path/to/custom.hbs` | Removed; place `default.hbs` in `<source-dir>/.md-serve/templates/` instead |
| Single template for all pages | Three role-based templates with per-file front matter override |

This is a **breaking change** for anyone using `md-serve.template` in `application.properties`.

## Acceptance Criteria

- [ ] `md-serve.template` property is removed; setting it has no effect.
- [ ] Markdown pages are rendered with `default.hbs`.
- [ ] Directory pages are rendered with `directory.hbs`.
- [ ] Error pages are rendered with `error.hbs`.
- [ ] A `.hbs` file in `<source-dir>/.md-serve/templates/` overrides the corresponding bundled template.
- [ ] A Markdown file with `template: custom` in its front matter is rendered with `custom.hbs` if it exists; falls back to `default` if not.
- [ ] All three bundled templates produce valid HTML.
- [ ] `mvn verify` passes with no new failures.

## Testing Plan

**Unit — `TemplateRegistryTest`**
- No `templateDir` present: all three roles load their bundled templates without error.
- `templateDir` present with `default.hbs`: loaded template is used for the `default` role; other roles still use bundled templates.
- `templateDir` present with an extra `custom.hbs`: it is accessible by name.
- A template name not in the registry falls back to `default`.

**Unit — `FileRendererTest`**
- Front matter contains `template: custom`; assert the renderer requests `custom` from `TemplateRenderer` (mock the renderer).
- No `template` in front matter; assert `default` is requested.

**Integration — `MarkdownResourceTest`**
- Write a minimal `default.hbs` (e.g. just `CUSTOM:{{title}}`) to `tempDir/.md-serve/templates/`; assert the rendered response body starts with `CUSTOM:`.
- Assert that `GET /` (directory) and `GET /nonexistent` (404) each return 200/404 with HTML bodies (verifying the respective bundled templates render without error).

## Definition of Done

- [ ] `md-serve.template` removed from `MdServeConfig` and docs.
- [ ] `TemplateLoader` deleted.
- [ ] `TemplateRegistry` implemented with discovery + per-role fallback.
- [ ] `directory.hbs` and `error.hbs` bundled in classpath.
- [ ] `TemplateRenderer.render()` accepts a template name.
- [ ] All three renderers pass the correct template name.
- [ ] `FileRenderer` reads `frontmatter.template` for per-file overrides.
- [ ] Unit and integration tests added and passing.
- [ ] `mvn verify` passes.
- [ ] Docs updated to describe the new `.md-serve/templates/` convention.
- [ ] Step marked DONE and moved to `work/done/`.

## Assumptions

- A1: Templates are compiled once at startup; no reload on file change.
- A2: The `template` front matter value is a plain string matching a file name without extension.
- A3: Template compilation errors for user-provided templates at startup are fatal (the server fails to start), giving users immediate feedback.
- A4: The `.md-serve/` directory itself is hidden by step 15's dot-prefix rule and will never be served as a directory listing or file.
