## Why

md-serve currently renders standard Markdown but lacks support for definition lists — a useful semantic HTML construct (`<dl>/<dt>/<dd>`) commonly used in documentation for glossaries, API references, and technical specs. Adding this enables richer, more structured documentation output without requiring users to fall back to raw HTML.

## What Changes

- Enable the flexmark `DefinitionList` extension so that Markdown definition list syntax is parsed and rendered as `<dl>/<dt>/<dd>` HTML elements.
- No configuration required — the extension is always active.

## Capabilities

### New Capabilities

- `definition-list-rendering`: Parses Markdown definition list syntax (term on its own line, followed by `: definition`) and renders it as semantic `<dl>/<dt>/<dd>` HTML.

### Modified Capabilities

<!-- None -->

## Impact

- **Dependencies**: No new dependencies — `flexmark-all` already bundles the `DefinitionListExtension`.
- **Code**: `MarkdownService` (or equivalent parser configuration) needs the extension added to the flexmark options.
- **Output HTML**: Pages containing definition list syntax will now render structured `<dl>` elements instead of plain paragraphs.
- **Templates**: Default template may need basic CSS styling for `dl/dt/dd` elements to render readably, but no structural changes required.
