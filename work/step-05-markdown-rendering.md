# Step 5: Markdown Rendering

## Goal

Convert a Markdown string into an HTML fragment. This component is pure and stateless — it has no knowledge of files, templates, or configuration.

## Implementation Plan

- Create `MarkdownRenderer` as an `@ApplicationScoped` bean
- Single public method:
  ```java
  String render(String markdown)
  ```
- Use Flexmark (preferred for its extension support) or CommonMark:
  - Enable at minimum: tables, fenced code blocks, strikethrough, autolinks
- The output is an HTML fragment (no `<html>` / `<body>` wrapper)
- Parser and renderer instances should be created once at bean construction (they are thread-safe)

## Definition of Done

- Unit tests cover:
  - Basic paragraph, headings, bold, italic
  - Fenced code block rendered as `<pre><code>`
  - Markdown table rendered as `<table>`
  - Input with no Markdown syntax passes through as plain HTML paragraph
  - Empty string input returns empty string
