## ADDED Requirements

### Requirement: Definition list syntax renders as semantic HTML
The system SHALL parse Markdown definition list syntax (a term on its own line followed by one or more lines beginning with `: `) and render it as `<dl>`, `<dt>`, and `<dd>` HTML elements.

#### Scenario: Single term with single definition
- **WHEN** a Markdown file contains a term followed by a `: definition` line
- **THEN** the rendered HTML SHALL contain a `<dl>` element with one `<dt>` (the term) and one `<dd>` (the definition)

#### Scenario: Multiple definitions for one term
- **WHEN** a Markdown file contains a term followed by multiple `: definition` lines
- **THEN** the rendered HTML SHALL contain one `<dt>` and multiple `<dd>` elements inside the `<dl>`

#### Scenario: Multiple terms in one list
- **WHEN** a Markdown file contains several term/definition pairs in sequence
- **THEN** all terms and definitions SHALL be grouped within a single `<dl>` element

#### Scenario: Non-definition-list content is unaffected
- **WHEN** a Markdown file contains no definition list syntax
- **THEN** the rendered HTML SHALL be identical to the output before this change

### Requirement: Default template styles definition lists readably
The default Handlebars template SHALL include CSS rules for `dl`, `dt`, and `dd` elements so that definition lists render with visible term emphasis and indented definitions in the out-of-the-box experience.

#### Scenario: Term is visually distinct
- **WHEN** a page containing a definition list is rendered with the default template
- **THEN** `<dt>` elements SHALL appear bold or otherwise visually distinct from body text

#### Scenario: Definition is indented
- **WHEN** a page containing a definition list is rendered with the default template
- **THEN** `<dd>` elements SHALL be indented relative to `<dt>` elements
