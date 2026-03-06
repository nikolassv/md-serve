## Why

There is no automated test execution on the repository. Tests only run when a developer remembers to run them locally, which risks regressions being merged into `main`. Adding a CI workflow ensures every commit to `main` is verified, and optionally also validates pull requests before merge.

## What Changes

- Add a GitHub Actions workflow file (`.github/workflows/ci.yml`) that runs the Maven test suite
- The workflow triggers on every push to `main`
- The workflow also triggers on pull requests (targeting any branch) — this can be disabled by the user if not desired
- Java 21 and Maven are set up in CI; no external services required

## Capabilities

### New Capabilities

- `ci-test-workflow`: GitHub Actions workflow that builds and runs all tests on push to `main` and on pull requests

### Modified Capabilities

<!-- none -->

## Impact

- New file: `.github/workflows/ci.yml`
- No changes to application code, dependencies, or configuration
- Requires the repository to be hosted on GitHub (already the case — release workflow exists)
