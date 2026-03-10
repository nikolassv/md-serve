## 1. Enable DefinitionList Extension

- [x] 1.1 Add `DefinitionListExtension.create()` to the extensions list in `MarkdownRenderer`
- [x] 1.2 Verify existing tests still pass with the new extension active

## 2. Default Template Styling

- [x] 2.1 Add CSS rules for `dl`, `dt`, and `dd` in `default.hbs` (bold `dt`, indented `dd`)

## 3. Tests

- [x] 3.1 Add integration test: definition list syntax renders `<dl>/<dt>/<dd>` HTML
- [x] 3.2 Add integration test: multiple definitions per term render multiple `<dd>` elements

## 4. Documentation & Changelog

- [x] 4.1 Update `docs/user/getting-started.md` to mention definition list support
- [x] 4.2 Update `docs/dev/architecture.md` to note the added flexmark extension
- [x] 4.3 Add entry to `CHANGELOG.md` under `[Unreleased]`
