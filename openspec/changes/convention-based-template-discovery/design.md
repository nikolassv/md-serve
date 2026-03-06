## Context

md-serve currently manages templates through a single `TemplateLoader` bean that loads one compiled Handlebars template — either from a user-supplied filesystem path (`md-serve.template`) or the bundled `default.hbs` classpath resource. Every renderer (`FileRenderer`, `DirectoryRenderer`, `ErrorRenderer`) delegates to `TemplateRenderer`, which holds that single compiled `Template` and applies it unconditionally.

This makes it impossible to give directory listings or error pages a distinct layout without embedding complex conditionals inside a monolithic template.

## Goals / Non-Goals

**Goals:**
- Replace single-template configuration with convention-based discovery from `<source-dir>/.md-serve/templates/`.
- Define three template roles (`default`, `directory`, `error`), each with a bundled classpath fallback.
- Allow per-file template selection via `template:` YAML front matter.
- Remove the `md-serve.template` config property (breaking change).

**Non-Goals:**
- Hot-reloading templates after startup.
- Template inheritance, partials, or includes.
- Configurable template directory path.
- Template roles beyond the three defined.

## Decisions

### D1 — `TemplateRegistry` replaces `TemplateLoader`

A new `@ApplicationScoped` `TemplateRegistry` bean loads all templates at startup into a `Map<String, Template>`. It replaces `TemplateLoader`, which is deleted.

**Rationale**: A registry keyed by name is the natural structure for role-based lookup and front-matter override. A single loader bean cannot serve multiple named templates.

**Alternatives considered**: Keeping `TemplateLoader` and extending it — rejected because the single-template assumption is baked throughout its interface.

### D2 — Templates compiled once at startup; errors are fatal

User-provided templates that fail to compile cause the server to fail to start (CDI startup exception). Bundled templates are assumed correct.

**Rationale**: Failing fast gives immediate feedback. Silent fallback to the bundled template would hide configuration errors.

**Alternatives considered**: Log a warning and fall back — rejected; this masks mistakes.

### D3 — `TemplateRenderer.render()` accepts a template name

Signature changes from `render(TemplateContext ctx)` to `render(TemplateContext ctx, String templateName)`. Unknown names fall back to `default`.

**Rationale**: Callers know which role they need; the renderer should not need to infer it.

### D4 — Per-file override via front matter `template:` key

`FileRenderer` reads `frontmatter.get("template")` (defaults to `"default"`) and passes it to `TemplateRenderer`. `TemplateRegistry` resolves it; if the name is not found, `default` is used silently.

**Rationale**: Silently falling back for unknown front-matter values is safe — a typo degrades gracefully rather than crashing a page render.

## Risks / Trade-offs

- **Breaking change for `md-serve.template` users** → Documented migration: place `default.hbs` in `<source-dir>/.md-serve/templates/`. Covered in user docs.
- **Startup cost** → All templates compiled at startup. Acceptable; Handlebars compilation is fast and the number of templates is small.
- **No hot-reload** → Template changes require a server restart. Matches existing behaviour and is acceptable for a dev-time tool.

## Migration Plan

1. Remove `md-serve.template` from `MdServeConfig` and `application.properties`.
2. Instruct users in `docs/user/configuration.md` to place their custom template at `<source-dir>/.md-serve/templates/default.hbs`.
3. No data migration needed; all state is on the filesystem.
4. Rollback: revert to previous release; the `.md-serve/templates/` directory is ignored by older versions.

## Open Questions

None — the spec from `work/step-17-template-discovery.md` fully resolves all design choices.
