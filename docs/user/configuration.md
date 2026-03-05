# Configuration

## application.properties keys

| Property | Type | Default | Description |
|---|---|---|---|
| `md-serve.source-dir` | `String` | `./docs` | Directory containing Markdown files to serve. Relative paths are resolved from the working directory at startup. |
| `md-serve.template` | `String` | _(built-in)_ | Path to a custom Handlebars (`.hbs`) template file. When omitted, the bundled `templates/default.hbs` is used. |

### Example

```properties
md-serve.source-dir=/home/alice/notes
md-serve.template=/home/alice/notes/custom.hbs
```

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

**Supported built-in key:**

| Key | Effect |
|---|---|
| `title` | Overrides the page title (used in `<title>` and `{{title}}` context variable). |

All other keys are passed through as-is and are available as `{{frontmatter.<key>}}` in your template.

## Custom Handlebars template

Create a `.hbs` file and set `md-serve.template` to its path. The following context variables are available:

| Variable | Type | Description |
|---|---|---|
| `{{title}}` | `String` | Page title (front matter > first H1 > filename). |
| `{{{content}}}` | `String` (HTML) | Rendered Markdown HTML. Use triple braces to avoid escaping. `null` for directory listings. |
| `{{files}}` | list | Directory entries, each with `.name`, `.path`, and `.title`. `null` for file requests. |
| `{{breadcrumbs}}` | list | Navigation trail, each with `.label` and `.path`. Empty at the root. |
| `{{frontmatter}}` | map | Parsed YAML front matter. Empty map if none present. |

### Minimal template example

```hbs
<!DOCTYPE html>
<html>
<head><title>{{title}}</title></head>
<body>
  {{#if content}}
    {{{content}}}
  {{/if}}
  {{#if files}}
    <h1>{{title}}</h1>
    <ul>
      {{#each files}}<li><a href="{{path}}">{{name}}</a></li>{{/each}}
    </ul>
  {{/if}}
</body>
</html>
```

### Accessing front matter in a template

```hbs
{{#if frontmatter.author}}
  <p>By {{frontmatter.author}}</p>
{{/if}}
```
