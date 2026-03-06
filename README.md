# md-serve

A Quarkus-based HTTP server that serves Markdown files from a configured directory as rendered HTML.

## Features

- Serves `.md` files as styled HTML pages via Handlebars templates
- Directory listings with automatic navigation
- YAML front matter support (title override, template selection, custom metadata)
- Breadcrumb navigation
- Three role-based templates (`default`, `directory`, `error`) with bundled defaults; override any or all by placing `.hbs` files in `<source-dir>/.md-serve/templates/`
- Per-file template selection via `template:` front matter key
- 404 and 500 error pages rendered through the template pipeline
- Path traversal protection

## Installation

Download a pre-built release from the [GitHub Releases page](https://github.com/nikolassv/md-serve/releases).

**Uber JAR** (any platform with Java 21):

```sh
java -jar md-serve.jar
```

**Native executables** (no JVM required):

| Archive | Platform | Contains |
|---|---|---|
| `md-serve-linux-x86_64.tar.gz` | Linux x86_64 | `md-serve` |
| `md-serve-macos-x86_64.tar.gz` | macOS Intel | `md-serve` |
| `md-serve-macos-aarch64.tar.gz` | macOS Apple Silicon | `md-serve` |
| `md-serve-windows-x86_64.zip` | Windows x86_64 | `md-serve.exe` |

```sh
# Linux / macOS
tar -xzf md-serve-linux-x86_64.tar.gz
chmod +x md-serve
./md-serve
```

## Quick start

```sh
./mvnw quarkus:dev
```

Open `http://localhost:8080`. Serves the `./docs` directory by default.

## Configuration

| Property | Default | Description |
|---|---|---|
| `md-serve.source-dir` | `./docs` | Directory of Markdown files to serve |
| `md-serve.max-tree-depth` | `20` | Maximum directory depth for the navigation tree |

Set properties in `src/main/resources/application.properties` or on the command line with `-D<property>=<value>`.

To customise templates, place `.hbs` files in `<source-dir>/.md-serve/templates/` — see [Configuration Reference](docs/user/configuration.md).

## Front matter

Files may include a YAML front matter block:

```markdown
---
title: My Page
author: Ada Lovelace
---

Content here.
```

The `title` key overrides the H1-derived title. The `template` key selects a named template from `.md-serve/templates/`. All keys are accessible in templates as `{{frontmatter.key}}`.

## Documentation

- [Getting Started](docs/user/getting-started.md)
- [Configuration Reference](docs/user/configuration.md)
- [Development Guide](docs/dev/development.md)
- [Architecture](docs/dev/architecture.md)
