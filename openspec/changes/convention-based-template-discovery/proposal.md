## Why

The current single `md-serve.template` config property forces all page types (Markdown files, directory listings, error pages) to use the same template, making it impossible to give each a distinct layout without complex conditionals inside one file. Convention-based discovery eliminates configuration in favour of a predictable directory, and role-specific templates make each page type independently customisable.

## What Changes

- **BREAKING**: `md-serve.template` configuration property is removed.
- A new `.md-serve/templates/` directory inside the source directory is the discovery location for user-provided templates.
- Three template roles are defined: `default` (Markdown pages), `directory` (directory listings), `error` (error pages).
- Each role has a bundled classpath fallback used when no user file is present.
- Markdown pages can override their template via `template: <name>` in YAML front matter.
- `TemplateLoader` is deleted; `TemplateRegistry` replaces it as the central template management bean.

## Capabilities

### New Capabilities

- `template-registry`: Discover, load, and serve templates by role name with per-role classpath fallback and front-matter-driven per-file override.

### Modified Capabilities

<!-- No existing spec-level requirements are changing — this is a new capability replacing an internal implementation. -->

## Impact

- `MdServeConfig` — `template()` method removed.
- `TemplateLoader` — deleted.
- `TemplateRenderer` — method signature changes to accept a template name.
- `FileRenderer`, `DirectoryRenderer`, `ErrorRenderer` — each passes the correct role name to the renderer.
- `src/main/resources/templates/` — two new bundled templates added (`directory.hbs`, `error.hbs`).
- Users relying on `md-serve.template` must migrate to placing `default.hbs` in `<source-dir>/.md-serve/templates/`.
