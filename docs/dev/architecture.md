# Architecture

## Overview

md-serve is a Quarkus HTTP server that reads Markdown files from a configured directory and serves them as rendered HTML pages using a Handlebars template.

## Request Flow

```
HTTP GET /{path}
     │
     ▼
 PathResolver
     │  maps URL path → file system path under source-dir
     │  strips/adds .md extension, normalizes, prevents path traversal
     │
     ├─ path is a directory? → DirectoryIndexer
     │                              lists .md files → TemplateRenderer
     │
     └─ path is a file?      → MarkdownReader
                                    reads raw .md
                                         │
                                    FrontmatterParser  (strips YAML front matter, parses to map)
                                         │
                                    MarkdownRenderer   (parses remaining .md to HTML fragment)
                                         │
                                    TemplateRenderer   (merges context + template → full HTML)
```

## Components

| Component | Responsibility |
|---|---|
| `MarkdownResource` (JAX-RS) | Single catch-all `GET /{path:.*}` route; delegates to resolver |
| `PathResolver` | Translates URL path to absolute file path; validates it stays within source-dir |
| `FrontmatterParser` | Detects YAML front matter (`--- ... ---` block), strips it from Markdown source, parses it to a `Map<String, Object>` |
| `MarkdownRenderer` | Parses the remaining Markdown to an HTML fragment (CommonMark/Flexmark) |
| `DirectoryIndexer` | Lists `.md` files in a directory; extracts titles for link labels |
| `TemplateRenderer` | Loads Handlebars template (custom or default), merges context, returns full HTML |
| `TemplateLoader` | Resolves which template to use: custom path from config, else classpath default |
| `MdServeConfig` | MicroProfile Config bean: `source-dir`, `template` |

## Template Context

The following context is passed to the Handlebars template on every request:

```
{
  title:        string                // first H1 in content, or filename as fallback
  content:      html string           // rendered Markdown (null for directory listings)
  files:        [{name, path, title}] // directory listing entries (null for file requests)
  breadcrumbs:  [{label, path}]       // navigation trail derived from URL path
  frontmatter:  map                   // parsed YAML front matter, or empty map if absent
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

The parsed values are available in templates as `{{frontmatter.title}}`, `{{frontmatter.author}}`, etc. If a `title` key is present in the front matter, it takes precedence over the H1-derived title in the `title` context property.

## Configuration

| Property | Description | Default |
|---|---|---|
| `md-serve.source-dir` | Directory containing Markdown files | `./docs` |
| `md-serve.template` | Path to a custom Handlebars template file | built-in default |

## Key Design Decisions

- **Single route** — `GET /{path:.*}` handles both files and directories; returns 404 for missing paths.
- **No database** — purely file-system driven; files are read on each request (caching is out of scope for now).
- **Path traversal safety** — `PathResolver` rejects any resolved path outside `source-dir`.
- **Template override** — `TemplateLoader` checks `md-serve.template` first; falls back to `templates/default.hbs` on the classpath.
- **Title resolution order** — `frontmatter.title` > first H1 in content > filename.
