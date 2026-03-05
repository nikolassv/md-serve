# Architecture

## Overview

md-serve is a Quarkus HTTP server that reads Markdown files from a configured directory and serves them as rendered HTML pages using a Handlebars template.

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
| `MdServeConfig` | root | MicroProfile Config mapping: `source-dir`, `template` |
| `DocumentParser` | markdown | Reads a `.md` file; coordinates front matter parsing, Markdown rendering, and title resolution; returns a `ParsedDocument` record |
| `FrontmatterParser` | markdown | Detects `--- ... ---` block, strips it from the source, parses it to `Map<String, Object>` |
| `MarkdownRenderer` | markdown | Converts Markdown body text to an HTML fragment using Flexmark |
| `TitleResolver` | markdown | Derives a page title: `frontmatter.title` > first H1 in HTML > filename |
| `FileEntry` | render | DTO for a directory listing entry (`name`, `path`, `title`) |
| `FileRenderer` | render | Orchestrates file requests: parse document → build context → render template |
| `DirectoryRenderer` | render | Orchestrates directory requests: list files → build context → render template |
| `DirectoryIndexer` | render | Lists `.md` files in a directory; extracts their titles for link labels |
| `ErrorRenderer` | render | Renders 404 and 500 error pages through the template pipeline; falls back to plain text if the template itself fails |
| `Breadcrumb` | template | DTO for a breadcrumb navigation entry (`label`, `path`) |
| `TemplateContext` | template | Record holding all template variables passed to Handlebars |
| `TemplateLoader` | template | Resolves which template to use: custom path from config, else `templates/default.hbs` on the classpath |
| `TemplateRenderer` | template | Loads the Handlebars template via `TemplateLoader`, merges a `TemplateContext`, returns full HTML |

## Template Context

The following variables are available in every Handlebars template:

```
{
  title:        string                 // derived title (frontmatter > H1 > filename)
  content:      html string            // rendered Markdown HTML (null for directory listings)
  files:        [{name, path, title}]  // directory entries (null for file requests)
  breadcrumbs:  [{label, path}]        // navigation trail; empty list at root
  frontmatter:  map                    // parsed YAML front matter, or empty map
}
```

### Front Matter

Markdown files may optionally begin with a YAML front matter block:

```markdown
---
title: My Custom Title
author: Ada Lovelace
tags: [quarkus, markdown]
---

# Document content starts here
```

The parsed values are available in templates as `{{frontmatter.title}}`, `{{frontmatter.author}}`, etc. If a `title` key is present, it takes precedence over the H1-derived title in the `title` context property.

## Configuration

| Property | Description | Default |
|---|---|---|
| `md-serve.source-dir` | Directory containing Markdown files | `./docs` |
| `md-serve.template` | Path to a custom Handlebars template file | built-in default |

## Key Design Decisions

- **Single route** — `GET /{path:.*}` handles files, directories, and errors; 404 for missing paths, 500 for unexpected failures.
- **No database** — purely file-system driven; files are read on each request.
- **Path traversal safety** — `PathResolver` rejects any resolved path outside `source-dir`.
- **Template override** — `TemplateLoader` checks `md-serve.template` first; falls back to `templates/default.hbs` on the classpath.
- **Title resolution order** — `frontmatter.title` > first H1 in content > filename.
- **Subpackage structure** — code is split into `markdown` (parsing), `render` (orchestration), and `template` (output) packages to keep concerns separate.
