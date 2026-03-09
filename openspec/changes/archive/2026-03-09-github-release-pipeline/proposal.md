## Why

md-serve has no automated release process, making it hard to distribute usable artifacts to end users. A production-ready release pipeline ensures every tagged version produces a changelog and downloadable binaries without manual steps.

## What Changes

- Add a `CHANGELOG.md` file following the [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) standard
- Add a GitHub Actions workflow that triggers on version tags (`v*`)
- The workflow builds and publishes a GitHub Release containing:
  - A fat/uber JAR
  - Native standalone executables for Linux, macOS (x86_64 and aarch64), and Windows (x86_64)
  - The changelog entry for the released version as the release body

## Capabilities

### New Capabilities

- `release-pipeline`: GitHub Actions workflow that builds and publishes versioned releases with changelog and multi-platform artifacts
- `changelog`: Maintained `CHANGELOG.md` following Keep a Changelog 1.0.0 convention

### Modified Capabilities

(none)

## Impact

- New files: `.github/workflows/release.yml`, `CHANGELOG.md`
- Maven build: Quarkus native build enabled for multiple targets via cross-compilation or matrix strategy
- No changes to runtime Java code or configuration
- Requires GitHub repository secrets/permissions for publishing releases (standard `GITHUB_TOKEN` is sufficient)
