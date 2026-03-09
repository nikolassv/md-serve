# Configuration

## application.properties keys

| Property | Type | Default | Description |
|---|---|---|---|
| `md-serve.source-dir` | `String` | `.` | Directory containing Markdown files to serve. Relative paths are resolved from the working directory at startup. |
| `md-serve.port` | `int` | `8080` | TCP port the HTTP server listens on. |
| `md-serve.max-tree-depth` | `int` | `20` | Maximum directory depth when building the site navigation tree. Directories beyond this depth are silently omitted from the tree. Symlinks are never followed regardless of this limit. |

### Example

```properties
md-serve.source-dir=/home/alice/notes
```

## Custom templates

md-serve uses convention-based template discovery. Place `.hbs` files in a `.md-serve/templates/` directory inside your source directory:

```
<source-dir>/
  .md-serve/
    templates/
      default.hbs     ← Markdown file pages
      directory.hbs   ← Directory listing pages
      error.hbs       ← Error pages (404, 500)
      custom.hbs      ← Any additional named templates
  your-docs.md
```

If a file is absent, the bundled classpath template for that role is used as a fallback. You only need to provide the templates you want to override.

The `.md-serve/` directory is hidden from the request router — its contents are never served directly.

### Template roles

| Role file | Used for |
|---|---|
| `default.hbs` | All Markdown file pages |
| `directory.hbs` | Directory listing pages |
| `error.hbs` | 404 and 500 error pages |

### Per-file template override

A Markdown file can request a named template via its front matter:

```markdown
---
template: custom
---

# My Page
```

`custom.hbs` must exist in `.md-serve/templates/`. If the named template is not found, `default.hbs` is used silently.

## Template context variables

The following variables are available in every Handlebars template:

| Variable | Type | Description |
|---|---|---|
| `{{title}}` | `String` | Page title (front matter > first H1 > filename). |
| `{{{content}}}` | `String` (HTML) | Rendered Markdown HTML. Use triple braces to avoid escaping. `null` for directory listings. |
| `{{files}}` | list | Directory entries, each with `.name`, `.path`, and `.title`. `null` for file requests. |
| `{{breadcrumbs}}` | list | Navigation trail, each with `.label` and `.path`. Empty at the root. |
| `{{frontmatter}}` | map | Parsed YAML front matter. Empty map if none present. |
| `{{tree}}` | list | Recursive site navigation tree. Each node has `.name`, `.path`, `.directory`, `.active`, and `.children`. `active` is `true` for the current page and all its ancestor directories. Hidden entries and non-`.md` files are excluded. Use the built-in `treeNav` helper (see below) to render it. |

## Front matter

Markdown files may begin with a YAML front matter block delimited by `---`:

```markdown
---
title: My Document Title
author: Ada Lovelace
date: 2024-01-15
tags: [quarkus, markdown]
---

# Content starts here
```

- The block is stripped before Markdown rendering.
- Parsed values are available in templates as `{{frontmatter.key}}`.
- If `title` is set in front matter it overrides the H1-derived title everywhere.

**Recognised front matter keys:**

| Key | Effect |
|---|---|
| `title` | Overrides the page title (used in `<title>` and `{{title}}` context variable). |
| `template` | Selects a named template from `.md-serve/templates/`. Falls back to `default` if not found. |

All other keys are passed through as-is and are available as `{{frontmatter.<key>}}` in your template.

## Handlebars helpers

### `treeNav`

A built-in helper renders the full site tree as a collapsible navigation sidebar. Directories expand and collapse using the browser's native `<details>`/`<summary>` behaviour — no JavaScript required.

```hbs
{{#if tree}}
<nav>
  {{{treeNav tree}}}
</nav>
{{/if}}
```

Use triple braces (`{{{...}}}`) to prevent Handlebars from HTML-escaping the output.

### Accessing front matter in a template

```hbs
{{#if frontmatter.author}}
  <p>By {{frontmatter.author}}</p>
{{/if}}
```
