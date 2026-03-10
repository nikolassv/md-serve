## Context

md-serve uses flexmark-all for Markdown parsing and HTML rendering. The active extensions are registered in `MarkdownRenderer` constructor via `Parser.EXTENSIONS`. The `flexmark-all` artifact already bundles `DefinitionListExtension` — no new dependency is required.

Definition list syntax (supported by PHP Markdown Extra and pandoc):
```
Term
: Definition text
```
Renders as `<dl><dt>Term</dt><dd>Definition text</dd></dl>`.

## Goals / Non-Goals

**Goals:**
- Parse and render Markdown definition list syntax as semantic `<dl>/<dt>/<dd>` HTML.
- Zero configuration — always enabled alongside other extensions.
- Ensure the default template renders `dl/dt/dd` elements readably.

**Non-Goals:**
- Making definition list support configurable (on/off toggle).
- Supporting non-standard definition list variants beyond what `DefinitionListExtension` handles.
- Changing the CSS framework or adding a new dependency.

## Decisions

### Add `DefinitionListExtension` to `MarkdownRenderer`

`flexmark-all` already includes this extension. Adding it to the `List.of(...)` call in `MarkdownRenderer` is the only code change needed. No factory method, no config wiring.

**Alternatives considered:**
- Using a separate `flexmark-ext-definition-list` artifact — unnecessary since `flexmark-all` already bundles it.
- Making it configurable — adds complexity with no clear user value; all other extensions are always enabled.

### Add minimal `dl/dt/dd` CSS to the default template

The default template (`default.hbs`) includes inline styles for code blocks and tables. `dt` elements should be bold and `dd` elements indented to match standard definition list rendering conventions. This keeps the out-of-the-box experience clean without adding a stylesheet dependency.

**Alternatives considered:**
- No CSS change — browsers render `dl/dt/dd` with some default styling, which is acceptable but inconsistent with the polished look of other elements.

## Risks / Trade-offs

- [Syntax conflict] Some Markdown authors use a line starting with `: ` for other purposes (e.g., blockquote-like indentation). The extension activates definition list parsing globally. → **Mitigation**: This matches standard PHP Markdown Extra behaviour; the risk is low and consistent with user expectations for definition list syntax.

## Migration Plan

No migration needed. This is purely additive — existing documents that don't use definition list syntax are unaffected. Deploy as a normal release.
