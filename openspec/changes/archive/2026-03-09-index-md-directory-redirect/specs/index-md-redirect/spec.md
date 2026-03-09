## ADDED Requirements

### Requirement: Directory with index.md redirects to index file
When a GET request resolves to a directory and that directory contains a file named `index.md`, the server SHALL respond with HTTP 301 and a `Location` header pointing to `/<path>/index.md`.

#### Scenario: Root directory with index.md
- **WHEN** a GET request is made to `/`
- **AND** the source directory root contains `index.md`
- **THEN** the server responds with HTTP 301
- **AND** the `Location` header is `/index.md`

#### Scenario: Subdirectory with index.md
- **WHEN** a GET request is made to `/docs/`
- **AND** the `docs` directory contains `index.md`
- **THEN** the server responds with HTTP 301
- **AND** the `Location` header is `/docs/index.md`

#### Scenario: Directory without index.md shows listing
- **WHEN** a GET request is made to a directory path
- **AND** the directory does NOT contain `index.md`
- **THEN** the server responds with HTTP 200 and an HTML directory listing

#### Scenario: Trailing slash is normalised
- **WHEN** a GET request is made to `/docs` (no trailing slash)
- **AND** the `docs` directory contains `index.md`
- **THEN** the server responds with HTTP 301
- **AND** the `Location` header is `/docs/index.md`
