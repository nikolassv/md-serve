# Architecture

## Overview

md-serve is a Quarkus HTTP server that reads Markdown files from a configured directory and serves them as rendered HTML pages using role-based Handlebars templates.

## Request Flow

```
HTTP GET /{path}
     │
     ▼
 MarkdownResource (JAX-RS)
     │  single catch-all GET /{path:.*} route
     │
     ▼
 PathResolver
     │  maps URL path → file system path under source-dir
     │  strips/adds .md extension, normalizes, prevents path traversal
     │
     ├─ path is a directory? → DirectoryRenderer
     │                              DirectoryIndexer lists .md files
     │                              TemplateRenderer renders directory listing
     │
     ├─ path is a file?      → FileRenderer
     │                              DocumentParser reads and parses .md:
     │                                FrontmatterParser  strips YAML block → Map
     │                                MarkdownRenderer   renders body → HTML fragment
     │                                TitleResolver      derives title
     │                              TemplateRenderer renders full HTML page
     │
     └─ not found / error    → ErrorRenderer
                                    TemplateRenderer renders error page (404/500)
```

## Components

| Component | Package | Responsibility |
|---|---|---|
| `MarkdownResource` | root | Single `GET /{path:.*}` JAX-RS route; delegates to renderers |
| `PathResolver` | root | Translates URL path to absolute `Path`; validates it stays within source-dir |
| `MdServeConfig` | root | MicroProfile Config mapping: `source-dir`, `max-tree-depth` |
| `DocumentParser` | markdown | Reads a `.md` file; coordinates front matter parsing, Markdown rendering, and title resolution; returns a `ParsedDocument` record |
| `FrontmatterParser` | markdown | Detects `--- ... ---` block, strips it from the source, parses it to `Map<String, Object>` |
| `MarkdownRenderer` | markdown | Converts Markdown body text to an HTML fragment using Flexmark |
| `TitleResolver` | markdown | Derives a page title: `frontmatter.title` > first H1 in HTML > filename |
| `FileEntry` | render | DTO for a directory listing entry (`name`, `path`, `title`) |
| `TreeNode` | render | Record representing one node in the site navigation tree (`name`, `path`, `directory`, `active`, `children`) |
| `DirectoryTreeBuilder` | render | Walks the source directory recursively and returns a `List<TreeNode>`; marks active nodes based on the current URL path; skips hidden entries and respects `max-tree-depth`; never follows symlinks |
| `FileRenderer` | render | Orchestrates file requests: parse document → build context → render template |
| `DirectoryRenderer` | render | Orchestrates directory requests: list files → build context → render template |
| `DirectoryIndexer` | render | Lists `.md` files in a directory; extracts their titles for link labels |
| `ErrorRenderer` | render | Renders 404 and 500 error pages through the template pipeline; falls back to plain text if the template itself fails |
| `Breadcrumb` | template | DTO for a breadcrumb navigation entry (`label`, `path`) |
| `TemplateContext` | template | Record holding all template variables passed to Handlebars |
| `TemplateRegistry` | template | Loads all templates at startup from `<source-dir>/.md-serve/templates/` (user-provided) with per-role classpath fallbacks; exposes `get(name)` with fallback to `default`; registers Handlebars helpers |
| `TemplateRenderer` | template | Resolves a named template from `TemplateRegistry`, merges a `TemplateContext`, returns full HTML |
| `TreeNavHelper` | template | Handlebars `Helper` that recursively renders a `List<TreeNode>` into nested `<ul>` HTML using `<details>`/`<summary>` for directory collapse/expand |

## Template Context

The following variables are available in every Handlebars template:

```
{
  title:        string                 // derived title (frontmatter > H1 > filename)
  content:      html string            // rendered Markdown HTML (null for directory listings)
  files:        [{name, path, title}]  // directory entries (null for file requests)
  breadcrumbs:  [{label, path}]        // navigation trail; empty list at root
  frontmatter:  map                    // parsed YAML front matter, or empty map
  tree:         [TreeNode]             // recursive site navigation tree (see below)
}
```

Each `TreeNode` in the tree has:

```
{
  name:       string      // display name (filename without .md for files)
  path:       string      // absolute URL path, e.g. "/docs/intro"
  directory:  boolean
  active:     boolean     // true if this node IS the current page or an ancestor of it
  children:   [TreeNode]  // empty list for leaf files
}
```

All three renderers (file, directory, error) populate `tree`. Error pages receive the tree with no node marked active.

### Front Matter

Markdown files may optionally begin with a YAML front matter block:

```markdown
---
title: My Custom Title
author: Ada Lovelace
tags: [quarkus, markdown]
template: custom
---

# Document content starts here
```

The parsed values are available in templates as `{{frontmatter.title}}`, `{{frontmatter.author}}`, etc. If a `title` key is present, it takes precedence over the H1-derived title in the `title` context property. If a `template` key is present, that named template is used to render the file (falling back to `default` if not found).

## Configuration

| Property | Description | Default |
|---|---|---|
| `md-serve.source-dir` | Directory containing Markdown files | `.` |
| `md-serve.max-tree-depth` | Maximum directory depth for the site navigation tree | `20` |

## Handlebars Helpers

Custom helpers are registered on the shared `Handlebars` instance inside `TemplateRegistry` at startup, so they are available in all templates — bundled or user-provided.

| Helper | Signature | Description |
|---|---|---|
| `treeNav` | `{{{treeNav tree}}}` | Renders the `tree` context variable as a fully recursive, collapsible `<ul>` navigation tree. Directories become `<details>/<summary>` elements (opened automatically when active). Active nodes receive the `active` CSS class. Use triple braces to avoid HTML escaping. |

## Release Pipeline

The GitHub Actions workflow at `.github/workflows/release.yml` triggers on `v*` tag pushes and produces a GitHub Release containing:

- **Uber JAR** (`md-serve.jar`) — built with `-Dquarkus.package.jar.type=uber-jar`; runnable with `java -jar md-serve.jar`
- **Native executables** — built with `-Pnative` (Quarkus native profile) and `graalvm/setup-graalvm` on four runners:
  - `ubuntu-latest` → `md-serve-linux-x86_64`
  - `macos-13` → `md-serve-macos-x86_64`
  - `macos-latest` (M-series) → `md-serve-macos-aarch64`
  - `windows-latest` → `md-serve-windows-x86_64.exe`

The release body is extracted from the `## [<version>]` section in `CHANGELOG.md` using `awk`. The workflow fails loudly if that section is missing.

Only `secrets.GITHUB_TOKEN` is required — no additional repository secrets.

## Key Design Decisions

- **Single route** — `GET /{path:.*}` handles files, directories, and errors; 404 for missing paths, 500 for unexpected failures.
- **No database** — purely file-system driven; files are read on each request.
- **Path traversal safety** — `PathResolver` rejects any resolved path outside `source-dir`.
- **Convention-based template discovery** — `TemplateRegistry` scans `<source-dir>/.md-serve/templates/` at startup. Three roles are defined (`default`, `directory`, `error`); each has a bundled classpath fallback. Additional `.hbs` files in that directory are loaded by name for per-file front matter overrides. The `.md-serve/` directory is hidden from the router by the dot-prefix rule.
- **Role-based dispatch** — `FileRenderer` uses the `default` role (or `template` from front matter), `DirectoryRenderer` uses `directory`, and `ErrorRenderer` uses `error`. Unknown names fall back to `default`.
- **Title resolution order** — `frontmatter.title` > first H1 in content > filename.
- **Subpackage structure** — code is split into `markdown` (parsing), `render` (orchestration), and `template` (output) packages to keep concerns separate.
