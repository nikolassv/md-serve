## Context

md-serve is a Java 21 / Quarkus 3 application built with Maven. There is currently no CI/CD pipeline for releases. Users must build the project themselves. The goal is to make production-ready releases available as GitHub Releases, triggered by a git tag push, with no manual intervention beyond tagging.

Quarkus supports native compilation via GraalVM/Mandrel. Cross-compilation for multiple OS targets is not supported natively; a matrix build strategy using GitHub-hosted runners (ubuntu, macos, windows) is the standard approach.

## Goals / Non-Goals

**Goals:**
- Publish a GitHub Release automatically when a `v*` tag is pushed
- Release body populated from the relevant `CHANGELOG.md` section
- Artifacts: uber JAR + native executables for Linux x86_64, macOS x86_64, macOS aarch64, Windows x86_64
- Keep CI configuration minimal and maintainable

**Non-Goals:**
- Snapshot/pre-release publishing to a package registry (Maven Central, GHCR)
- Docker image publishing
- Signing or notarizing binaries
- Automated changelog generation from commits

## Decisions

### D1: Native build matrix vs. cross-compilation

**Decision**: Use a GitHub Actions matrix with OS-specific runners (`ubuntu-latest`, `macos-13` for x86_64, `macos-latest` for aarch64, `windows-latest`).

**Rationale**: Quarkus native build with Mandrel/GraalVM does not support cross-compilation. Each runner builds the native executable for its host OS/arch. This is the documented Quarkus approach and avoids Docker-in-Docker or emulation complexity.

**Alternative considered**: Using `container: quay.io/quarkus/ubi-quarkus-mandrel-builder-image` on Linux and cross-compile for others — not feasible; GraalVM native-image is host-only.

### D2: JAR artifact type

**Decision**: Use Quarkus `uber-jar` packaging (`-Dquarkus.package.jar.type=uber-jar`) to produce a self-contained runnable JAR.

**Rationale**: Users should be able to run `java -jar md-serve.jar` without a separate lib/ directory. This is simpler than the default fast-jar layout for distribution.

**Alternative considered**: Distributing the fast-jar as a zip — more complex to package and document.

### D3: Changelog extraction

**Decision**: Parse `CHANGELOG.md` in the workflow using a shell script (`awk`/`sed`) to extract the section matching the tag version, and pass it as the GitHub Release body.

**Rationale**: Keeps tooling minimal — no extra action or Node dependency needed. The Keep a Changelog format is well-structured (`## [x.y.z]` headers) and straightforward to parse.

**Alternative considered**: Using a dedicated changelog action (e.g., `mindsers/changelog-reader-action`) — adds an external dependency; the shell approach is simpler and more transparent.

### D4: Quarkus native activation

**Decision**: Enable native build via Maven profile `-Pnative` (Quarkus standard) with GraalVM installed via `graalvm/setup-graalvm` GitHub Action.

**Rationale**: Quarkus provides a `native` profile out of the box. The `setup-graalvm` action handles GraalVM CE installation including `native-image` on all three platforms.

## Risks / Trade-offs

- [Risk] Native build time is long (5-15 min per platform) → Mitigation: Accepted; runs only on tag push, not on every PR.
- [Risk] macOS aarch64 runner (macos-latest) may differ between GitHub-hosted environments → Mitigation: Use `macos-latest` which maps to M-series; validate executable arch in workflow output.
- [Risk] Windows native build may require additional MSVC dependencies → Mitigation: `setup-graalvm` handles Visual Studio dependency setup automatically on Windows runners.
- [Risk] Changelog extraction fails if the tag version is not present in CHANGELOG.md → Mitigation: Workflow step fails loudly with a non-zero exit code, preventing a release with an empty body.

## Migration Plan

1. Add `CHANGELOG.md` with an initial entry for the first release version.
2. Add `.github/workflows/release.yml`.
3. Tag a release (`git tag v0.1.0 && git push --tags`) to verify the pipeline end-to-end.
4. Rollback: delete the GitHub Release and the tag if something goes wrong; no runtime code is affected.

## Open Questions

(none)
