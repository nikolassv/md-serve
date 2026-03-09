## ADDED Requirements

### Requirement: CHANGELOG.md follows Keep a Changelog 1.0.0
The repository SHALL maintain a `CHANGELOG.md` at the project root following the [Keep a Changelog 1.0.0](https://keepachangelog.com/en/1.0.0/) format.

#### Scenario: CHANGELOG.md exists at project root
- **WHEN** the repository is cloned
- **THEN** a `CHANGELOG.md` file is present at the root of the project

#### Scenario: Version sections use correct header format
- **WHEN** a new release version is added to the changelog
- **THEN** it appears as a second-level header in the format `## [x.y.z] - YYYY-MM-DD`

### Requirement: Unreleased section maintained
The changelog SHALL contain an `## [Unreleased]` section at the top for tracking changes not yet released.

#### Scenario: Unreleased section is present
- **WHEN** the changelog is opened
- **THEN** the first version section is `## [Unreleased]`

### Requirement: Change categories used
Each version section SHALL use standard Keep a Changelog categories (`Added`, `Changed`, `Deprecated`, `Removed`, `Fixed`, `Security`) as third-level headers, only including categories with actual entries.

#### Scenario: Categories appear only when non-empty
- **WHEN** a release has no security fixes
- **THEN** there is no `### Security` header in that version's section

### Requirement: CLAUDE.md instructs contributors to keep changelog current
`CLAUDE.md` SHALL contain a note in the development workflow section requiring contributors to update `CHANGELOG.md` under `[Unreleased]` as part of every change, before committing.

#### Scenario: CLAUDE.md contains changelog instruction
- **WHEN** a developer reads `CLAUDE.md`
- **THEN** they find a clear instruction to update `CHANGELOG.md` for every change

### Requirement: Initial version entry present
The `CHANGELOG.md` SHALL include an entry for the first released version documenting the initial capabilities of md-serve.

#### Scenario: First version entry documents initial features
- **WHEN** the first release tag is published
- **THEN** `CHANGELOG.md` contains a section for that version with at least one `Added` item describing the initial feature set
