## ADDED Requirements

### Requirement: Workflow triggers on version tag
The release workflow SHALL be triggered automatically when a git tag matching the pattern `v*` is pushed to the repository.

#### Scenario: Tag push triggers workflow
- **WHEN** a git tag starting with `v` is pushed (e.g., `v1.0.0`)
- **THEN** the GitHub Actions release workflow starts

#### Scenario: Non-tag push does not trigger workflow
- **WHEN** a commit is pushed to a branch without a matching tag
- **THEN** the release workflow does NOT run

### Requirement: Uber JAR artifact
The workflow SHALL build a self-contained uber JAR using `quarkus.package.jar.type=uber-jar` and attach it to the GitHub Release.

#### Scenario: JAR is built and uploaded
- **WHEN** the release workflow runs
- **THEN** a file named `md-serve-<version>-runner.jar` is attached to the GitHub Release as a downloadable asset

### Requirement: Native executables for all target platforms
The workflow SHALL build native executables for Linux x86_64, macOS x86_64, macOS aarch64, and Windows x86_64 using a matrix strategy, and attach each to the GitHub Release.

#### Scenario: Linux native executable
- **WHEN** the release workflow runs
- **THEN** a native Linux x86_64 executable is attached to the GitHub Release

#### Scenario: macOS x86_64 native executable
- **WHEN** the release workflow runs
- **THEN** a native macOS x86_64 executable is attached to the GitHub Release

#### Scenario: macOS aarch64 native executable
- **WHEN** the release workflow runs
- **THEN** a native macOS aarch64 executable is attached to the GitHub Release

#### Scenario: Windows native executable
- **WHEN** the release workflow runs
- **THEN** a native Windows x86_64 executable (`.exe`) is attached to the GitHub Release

### Requirement: Release body from changelog
The GitHub Release body SHALL contain the changelog section corresponding to the released version, extracted from `CHANGELOG.md`.

#### Scenario: Matching version found in changelog
- **WHEN** the tag version exists as a section header in `CHANGELOG.md`
- **THEN** the GitHub Release body contains only that version's changelog section

#### Scenario: Missing version fails the workflow
- **WHEN** the tag version is NOT found as a section header in `CHANGELOG.md`
- **THEN** the workflow fails with a non-zero exit code before creating the release

### Requirement: README documents installation from releases
The project `README.md` SHALL include an installation section explaining how to download and run pre-built artifacts from GitHub Releases, covering both the JAR and the native executables.

#### Scenario: JAR install instructions present
- **WHEN** a user reads `README.md`
- **THEN** they find instructions for downloading the uber JAR and running it with `java -jar`

#### Scenario: Native binary install instructions present
- **WHEN** a user reads `README.md`
- **THEN** they find instructions for downloading the platform-appropriate native executable and running it directly

### Requirement: Release uses standard GITHUB_TOKEN
The workflow SHALL use the repository's built-in `GITHUB_TOKEN` for creating the GitHub Release and uploading assets, requiring no additional secrets configuration.

#### Scenario: Release created with GITHUB_TOKEN
- **WHEN** the workflow creates a GitHub Release
- **THEN** it authenticates using `secrets.GITHUB_TOKEN` only
