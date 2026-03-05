# Step 8: File Request Pipeline

## Goal

Wire together all previously built components to serve a fully rendered HTML page for a Markdown file request.

## Implementation Plan

- In `MarkdownResource`, replace the file stub with the full pipeline:
  1. Read file content from the resolved path
  2. Pass content through `FrontmatterParser` → get `frontmatter` map + `body`
  3. Pass `body` through `MarkdownRenderer` → get HTML fragment
  4. Extract title using resolution order:
     - `frontmatter.get("title")` if present
     - First `<h1>` tag text from the rendered HTML fragment
     - Filename without `.md` extension as final fallback
  5. Build breadcrumbs: split URL path by `/`, build `[{label, path}]` list (each entry links to its cumulative path)
  6. Construct `TemplateContext` with `title`, `content`, `breadcrumbs`, `frontmatter`; `files` is null
  7. Pass to `TemplateRenderer` → return as 200 HTML response
- Extract title-from-H1 logic into a small private helper or utility method

## Definition of Done

- `GET /some/file` returns a fully rendered HTML page with correct title and content
- Title resolution order is respected (verified with test fixtures)
- Breadcrumbs reflect the URL path correctly
- Front matter values are accessible in the rendered template output
- Integration test using `@QuarkusTest` with a temporary directory of fixture `.md` files
