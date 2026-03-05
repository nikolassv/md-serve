# Step 9: Directory Listing

## Goal

When a request resolves to a directory, return an HTML page listing the Markdown files it contains, with linked titles.

## Implementation Plan

- Create `DirectoryIndexer` as an `@ApplicationScoped` bean:
  - Single public method:
    ```java
    List<FileEntry> list(Path directory)
    record FileEntry(String name, String path, String title) {}
    ```
  - List all `.md` files directly within the directory (non-recursive)
  - For each file: read only as much as needed to extract the title (front matter `title` → first H1 → filename); avoid full parse if possible
  - Sort entries alphabetically by filename
  - Subdirectories are also listed (no title extraction needed, use directory name as label)
- In `MarkdownResource`, replace the directory stub:
  1. Call `DirectoryIndexer.list()` with the resolved path
  2. Build breadcrumbs from URL path
  3. Construct `TemplateContext` with `title` (directory name), `files` list, `breadcrumbs`; `content` is null
  4. Pass to `TemplateRenderer` → return as 200 HTML response

## Definition of Done

- `GET /` with a directory of `.md` files returns an HTML listing with correct titles and links
- Subdirectories appear in the listing and link to their path
- Files without front matter or H1 fall back to filename as title
- Empty directory returns a valid page with an empty listing (no error)
- Integration test covering a directory with mixed files and subdirectories
