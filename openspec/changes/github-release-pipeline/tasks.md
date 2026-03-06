## 1. Changelog

- [ ] 1.1 Create `CHANGELOG.md` at the project root following Keep a Changelog 1.0.0 format with an `[Unreleased]` section and an initial version entry documenting md-serve's existing capabilities

## 2. Maven Build Configuration

- [ ] 2.1 Verify the Quarkus uber-jar packaging produces a runnable self-contained JAR by adding `-Dquarkus.package.jar.type=uber-jar` to a local build and confirming the output file name
- [ ] 2.2 Ensure the Quarkus `native` Maven profile is present and functional (it is included by default in Quarkus projects — confirm it exists in `pom.xml`)

## 3. GitHub Actions Workflow

- [ ] 3.1 Create `.github/workflows/release.yml` with the `on: push: tags: ['v*']` trigger
- [ ] 3.2 Add a job to build and upload the uber JAR artifact
- [ ] 3.3 Add a matrix job for native builds across `ubuntu-latest`, `macos-13` (x86_64), `macos-latest` (aarch64), and `windows-latest` using `graalvm/setup-graalvm` and the `native` Maven profile
- [ ] 3.4 Add a changelog extraction step that parses the tag version from `CHANGELOG.md` using `awk` and fails with a non-zero exit if the version section is missing
- [ ] 3.5 Add a step to create the GitHub Release using `gh release create` (or `softprops/action-gh-release`) with the extracted changelog body and attach all built artifacts
- [ ] 3.6 Confirm the workflow uses `secrets.GITHUB_TOKEN` and no additional secrets are required

## 4. Validation

- [ ] 4.1 Update `docs/dev/architecture.md` to document the release pipeline
- [ ] 4.2 Update `docs/user/getting-started.md` to mention where to download pre-built artifacts
- [ ] 4.3 Add an installation section to `README.md` covering how to download and run both the uber JAR and the platform-native executables from GitHub Releases
- [ ] 4.4 Add a note to `CLAUDE.md` requiring contributors to update `CHANGELOG.md` under `[Unreleased]` for every change, before committing
