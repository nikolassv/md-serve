## Context

The repository already has a GitHub Actions release workflow (`.github/workflows/release.yml`). The project builds with Maven and targets Java 21. Tests are written using REST-assured and run via `mvn test`. There are no external service dependencies required to run the test suite.

## Goals / Non-Goals

**Goals:**
- Run all Maven tests automatically on every push to `main`
- Run all Maven tests on every pull request
- Reuse GitHub's hosted runners (no self-hosted infrastructure)

**Non-Goals:**
- Code coverage reporting
- Publishing test results as PR comments
- Matrix testing across multiple Java versions
- Caching Maven dependencies (nice-to-have, can be added later)

## Decisions

### Trigger: push to `main` + pull_request

The workflow triggers on `push` to `main` and on `pull_request` (all branches). This matches the stated requirement and is the standard GitHub Actions pattern. An alternative would be `pull_request` only, but that would miss direct commits to `main` (e.g. hotfixes or merge commits).

### Runner: `ubuntu-latest`

Standard, cost-free GitHub-hosted runner. The application is platform-agnostic (JVM), so no OS-specific runner is needed.

### Java setup: `actions/setup-java` with `temurin` distribution

Temurin (Eclipse Adoptium) is the de-facto standard OpenJDK distribution for CI. Version 21 matches `CLAUDE.md` and the project's `pom.xml`.

### Build command: `mvn verify`

`mvn verify` runs compile + test + integration-test phases. This is preferred over `mvn test` alone because Quarkus integration tests (using `@QuarkusTest`) run in the `integration-test` phase. Using `verify` ensures nothing is missed.

## Risks / Trade-offs

- **Slow cold starts** → Maven downloads dependencies on every run without caching. Acceptable for now; caching can be added as a follow-up.
- **PR workflows on forks** → GitHub restricts secrets for fork PRs, but this workflow requires no secrets, so it will work correctly.

## Migration Plan

1. Create `.github/workflows/ci.yml`
2. Push to `main` — workflow runs immediately on the push
3. No rollback needed; deleting the file disables CI
