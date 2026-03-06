## ADDED Requirements

### Requirement: Bundled fallback templates for all roles
The system SHALL bundle classpath templates for the `default`, `directory`, and `error` roles. These templates SHALL be used when no user-provided template exists for that role.

#### Scenario: No user template directory present
- **WHEN** the server starts and `<source-dir>/.md-serve/templates/` does not exist
- **THEN** all three roles load their bundled classpath templates without error

#### Scenario: User template overrides one role
- **WHEN** `<source-dir>/.md-serve/templates/default.hbs` exists
- **THEN** the `default` role uses the user-provided file and other roles still use bundled templates

### Requirement: Convention-based template discovery
The system SHALL scan `<source-dir>/.md-serve/templates/` at startup and load any `.hbs` files found there, keyed by filename without extension.

#### Scenario: Additional custom template available
- **WHEN** `<source-dir>/.md-serve/templates/custom.hbs` exists
- **THEN** `custom` is a valid template name resolvable by `TemplateRegistry`

#### Scenario: Template compilation failure is fatal
- **WHEN** a user-provided `.hbs` file contains a syntax error
- **THEN** the server SHALL fail to start with a descriptive error

### Requirement: Role-based template selection by renderers
Each renderer SHALL request a specific role name from `TemplateRegistry`:
- `FileRenderer` requests `default` (or the value of the `template` front matter key).
- `DirectoryRenderer` requests `directory`.
- `ErrorRenderer` requests `error`.

#### Scenario: Markdown page rendered with default role
- **WHEN** a request is made for a Markdown file with no `template` front matter key
- **THEN** the response is rendered using the `default` template

#### Scenario: Directory page rendered with directory role
- **WHEN** a request is made for a directory path
- **THEN** the response is rendered using the `directory` template

#### Scenario: Error page rendered with error role
- **WHEN** a path does not exist or another error occurs
- **THEN** the response is rendered using the `error` template

### Requirement: Per-file template override via front matter
A Markdown file MAY specify `template: <name>` in its YAML front matter. The system SHALL render that file using the named template if it exists in `TemplateRegistry`.

#### Scenario: Valid front matter template override
- **WHEN** a Markdown file has `template: custom` in its front matter and `custom.hbs` exists in the templates directory
- **THEN** the file is rendered using `custom.hbs`

#### Scenario: Unknown front matter template falls back to default
- **WHEN** a Markdown file has `template: nonexistent` in its front matter and no such template exists
- **THEN** the file is rendered using the `default` template without error

### Requirement: Removal of `md-serve.template` configuration property
The `md-serve.template` configuration property SHALL be removed. Setting it SHALL have no effect.

#### Scenario: Property absent from config
- **WHEN** the server starts without `md-serve.template` in `application.properties`
- **THEN** the server starts successfully and uses convention-based discovery
