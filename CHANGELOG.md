# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Quarkus 3 HTTP server serving Markdown files from a configured directory as rendered HTML
- Handlebars template rendering with three built-in roles: `default` (file pages), `directory` (listings), `error` (404/500 pages)
- Convention-based template discovery: place `.hbs` files in `<source-dir>/.md-serve/templates/` to override any bundled template
- Per-file template selection via `template:` YAML front matter key
- YAML front matter parsing (`--- ... ---` block): values available in templates as `{{frontmatter.key}}`
- Title resolution: `frontmatter.title` > first H1 in content > filename
- Recursive site navigation tree with collapsible directories (`treeNav` Handlebars helper)
- Breadcrumb navigation on all pages
- Directory index listings with file titles
- Path traversal protection: resolved paths are constrained to the configured source directory
- Configuration properties: `md-serve.source-dir` (default: `.` - the current working directory), `md-serve.max-tree-depth` (default: `20`)
- GitHub Actions release pipeline: builds `md-serve.jar` (uber JAR) and native executables packaged as `md-serve-<platform>.tar.gz` / `.zip` (each containing a binary named `md-serve`) for Linux, macOS (x86_64 + aarch64), and Windows on every `v*` tag push
- GitHub Actions CI workflow (`.github/workflows/ci.yml`): runs `mvn verify` on every push to `main` and on every pull request

[Unreleased]: https://github.com/nikolassv/md-serve/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/nikolassv/md-serve/releases/tag/v0.1.0
