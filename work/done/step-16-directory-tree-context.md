# Step 16 — Full Directory Tree in Template Context

## Summary

Walk the entire source directory tree at request time and pass it to every Handlebars template as a `tree` variable. Each node in the tree knows whether it is on the active path (the current page or one of its ancestor directories), allowing template authors to render a highlighted site-wide navigation sidebar without any per-page configuration.

## Background / Current Behavior

- `TemplateContext` (`TemplateContext.java`) holds `title`, `content`, `files`, `breadcrumbs`, and `frontmatter`. The `files` field is a flat list of the *current* directory's immediate children, populated only for directory pages.
- File pages have `files = null`; there is no way for a template to render a link to an arbitrary page elsewhere in the tree.
- `DirectoryIndexer.list()` does a single-level `Files.list()` call and is not designed for recursive walking.

## Desired Behavior

- Every rendered page (file, directory, and error) receives a `tree` field in its template context containing a recursive representation of the source directory.
- The tree structure exposes each node's display name, URL path, whether it is a directory, and its children (for directories).
- Each node is marked `active` if it matches the current request URL path **or** is an ancestor directory of the current request URL path. This enables templates to highlight the current location and expand parent nodes.
- Dot-prefixed entries (hidden files, per step 15) are excluded from the tree.
- Only `.md` files and directories are included (consistent with `DirectoryIndexer`'s existing filter).
- Template authors access the tree via `{{#each tree}}` / `{{tree}}` in Handlebars.

## Scope

**In scope**
- A new `TreeNode` value type (record) in `de.nikolassv.mdserve.render`.
- A new `@ApplicationScoped` `DirectoryTreeBuilder` bean that walks the source directory recursively and returns `List<TreeNode>`.
- Active-node marking based on the current request URL path.
- Adding a `tree` field to `TemplateContext`.
- Populating `tree` in `FileRenderer`, `DirectoryRenderer`, and `ErrorRenderer`.
- A navigation sidebar in the default template that uses `tree` and applies active styling.
- Unit tests for `DirectoryTreeBuilder`, including active-marking behaviour.

**Out of scope**
- Caching the tree between requests (can be added later).
- Exposing asset files (non-`.md`) in the tree.
- Sorting strategies beyond alphabetical-by-name.
- Frontmatter-derived display names or ordering overrides.

## Data Model

```java
// de.nikolassv.mdserve.render.TreeNode
public record TreeNode(
        String name,            // display name (filename without .md extension for files)
        String path,            // absolute URL path, e.g. "/docs/intro"
        boolean directory,
        boolean active,         // true if this node IS the current page or an ancestor of it
        List<TreeNode> children // empty list for leaf files
) {}
```

`TemplateContext` gains a new field:

```java
public record TemplateContext(
        String title,
        String content,
        List<FileEntry> files,
        List<Breadcrumb> breadcrumbs,
        Map<String, Object> frontmatter,
        List<TreeNode> tree            // NEW
) {}
```

## Active-Node Marking

A node is `active` when its `path` equals the current URL path **or** the current URL path starts with `node.path + "/"`. This means:

- For a request to `/docs/intro`, the node for `/docs/intro` is active, and the node for `/docs` is active. The root node (`/`) is always active for any request.
- The `build(String currentUrlPath)` method applies marking during the recursive walk: after building a subtree, a directory node is marked active if its own path satisfies the condition, or if any of its children are active (the `active` flag is propagated upward).

## Implementation Plan

1. **`TreeNode` record**: Add `src/main/java/de/nikolassv/mdserve/render/TreeNode.java`.

2. **`DirectoryTreeBuilder`**: New `@ApplicationScoped` bean. Inject `MdServeConfig` for `sourceDir`. Expose `build(String currentUrlPath)`:
   - Recursively lists `sourceDir`.
   - Skips entries whose filename starts with `.`.
   - For `.md` files: strips `.md` from the display name; computes URL path relative to `sourceDir`; marks `active` if the URL path condition holds.
   - For directories: recurses first, then marks the directory node `active` if its own URL path condition holds **or** any child is active.
   - Sorts children alphabetically by name.

3. **`TemplateContext`**: Add `List<TreeNode> tree`.

4. **Renderers**: Inject `DirectoryTreeBuilder`. Call `treeBuilder.build(urlPath)` and pass the result when constructing `TemplateContext`.

5. **Default template**: Add a collapsible `<nav class="site-tree">` sidebar. Display each top-level node; nodes with `active = true` get a CSS class that bolds or highlights them. Use a Handlebars partial or nested `{{#each}}` blocks for one level of children (two levels total is sufficient for the default template).

6. **Tests**: See Testing Plan below.

## Relevant Code Pointers

- `src/main/java/de/nikolassv/mdserve/template/TemplateContext.java` — add `tree` field
- `src/main/java/de/nikolassv/mdserve/render/FileRenderer.java:27` — pass `tree` when building `TemplateContext`
- `src/main/java/de/nikolassv/mdserve/render/DirectoryRenderer.java` — same
- `src/main/java/de/nikolassv/mdserve/render/ErrorRenderer.java` — same
- `src/main/java/de/nikolassv/mdserve/render/DirectoryIndexer.java` — reference for the existing flat-list approach
- `src/main/java/de/nikolassv/mdserve/MdServeConfig.java` — inject for `sourceDir()`
- `src/main/resources/templates/default.hbs` — add sidebar using `tree`

## Acceptance Criteria

- [ ] `TemplateContext.tree` is non-null on all rendered pages.
- [ ] The tree contains nodes for all non-hidden `.md` files and directories in `source-dir`.
- [ ] Dot-prefixed entries are absent from the tree.
- [ ] `.md` file nodes have their extension stripped from `name`.
- [ ] Each node's `path` is a valid absolute URL path (starts with `/`).
- [ ] The node matching the current URL path has `active = true`.
- [ ] All ancestor directory nodes of the current URL path have `active = true`.
- [ ] Nodes unrelated to the current URL path have `active = false`.
- [ ] The default template renders a sidebar that visually distinguishes active nodes.
- [ ] `mvn verify` passes with no new failures.

## Testing Plan

**Unit — `DirectoryTreeBuilderTest`**

Set up a `@TempDir` with:
```
root/
  .hidden/
  docs/
    intro.md
    .draft.md
  index.md
```

Structural assertions (with `currentUrlPath = "/"`):
- Top-level children: `docs/` (directory) and `index` (file).
- `.hidden/` is absent.
- `docs/` children: `intro` only (`.draft.md` is absent).
- `intro` node: `path = "/docs/intro"`, `directory = false`, `children` is empty.

Active-marking assertions:
- With `currentUrlPath = "/docs/intro"`: `intro` node is active; `docs/` node is active; `index` node is not active.
- With `currentUrlPath = "/index"`: `index` node is active; `docs/` node is not active.
- With `currentUrlPath = "/docs"`: `docs/` node is active; `intro` node is not active.

**Integration**

Existing `MarkdownResourceTest` renders pages through the full stack; adding `tree` to `TemplateContext` is covered implicitly as long as `mvn verify` passes. One additional assertion: the rendered HTML for a page contains `<nav` (verifying the sidebar is present in the default template output).

## Definition of Done

- [ ] `TreeNode` record added with `active` field.
- [ ] `DirectoryTreeBuilder` implemented with active-marking logic.
- [ ] `DirectoryTreeBuilderTest` passes, including active-marking cases.
- [ ] `TemplateContext` updated with `tree` field.
- [ ] All three renderers populate `tree` by calling `treeBuilder.build(urlPath)`.
- [ ] Default template updated with a sidebar that uses `tree` and highlights active nodes.
- [ ] `mvn verify` passes.
- [ ] Step marked DONE and moved to `work/done/`.

## Assumptions

- A1: Re-building the tree on every request is acceptable for now; caching is a later concern.
- A2: Symlinks are not followed during the walk (consistent with `PathResolver` behaviour).
- A3: Very large source directories (thousands of files) are not a target use case for this step.
- A4: Error pages receive the tree with no node marked active (passing an empty or root-only `currentUrlPath`).
