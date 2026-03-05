# md-serve

A Quarkus-based HTTP server that serves Markdown files from a configured directory as rendered HTML.

## Features

- Serves `.md` files as styled HTML pages via a Handlebars template
- Directory listings with automatic navigation
- YAML front matter support (title override, custom metadata)
- Breadcrumb navigation
- Built-in default template; fully replaceable with a custom `.hbs` file
- 404 and 500 error pages rendered through the same template pipeline
- Path traversal protection

## Quick start

```sh
./mvnw quarkus:dev
```

Open `http://localhost:8080`. Serves the `./docs` directory by default.

## Configuration

| Property | Default | Description |
|---|---|---|
| `md-serve.source-dir` | `./docs` | Directory of Markdown files to serve |
| `md-serve.template` | built-in | Path to a custom Handlebars `.hbs` template |

Set properties in `src/main/resources/application.properties` or on the command line with `-D<property>=<value>`.

## Front matter

Files may include a YAML front matter block:

```markdown
---
title: My Page
author: Ada Lovelace
---

Content here.
```

The `title` key overrides the H1-derived title. All keys are accessible in templates as `{{frontmatter.key}}`.

## Documentation

- [Getting Started](docs/user/getting-started.md)
- [Configuration Reference](docs/user/configuration.md)
- [Development Guide](docs/dev/development.md)
- [Architecture](docs/dev/architecture.md)
