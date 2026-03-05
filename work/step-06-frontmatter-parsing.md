# Step 6: Front Matter Parsing

## Goal

Detect and extract a YAML front matter block from the top of a Markdown file, parse it into a map, and return the remaining Markdown content separately.

## Implementation Plan

- Create `FrontmatterParser` as an `@ApplicationScoped` bean (or a plain utility class — no dependencies)
- Detection rule: file content starts with `---\n` (or `---\r\n`); the block ends at the next `---` line
- Single public method returning a result record:
  ```java
  record ParseResult(Map<String, Object> frontmatter, String body) {}
  ParseResult parse(String rawContent)
  ```
- If no front matter block is detected, return an empty map and the full content as body
- Parse the YAML block with SnakeYAML; cast result to `Map<String, Object>`
- Malformed YAML should not crash the application — log a warning and treat as absent (return empty map + full content)

## Definition of Done

- Unit tests cover:
  - File with valid front matter: map contains correct values, body is the remaining Markdown
  - File without front matter: empty map, full content as body
  - Front matter with nested objects and lists
  - Malformed YAML: logs warning, returns empty map + full content (no exception)
  - Content that starts with `---` but has no closing `---`: treated as no front matter
