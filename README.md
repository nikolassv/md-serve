# md-serve

A Quarkus-based HTTP server that serves Markdown files from a configured directory as rendered HTML.

You have a folder full of Markdown files — notes, project docs, a personal wiki — and you want to read them in a browser without uploading them anywhere or wrestling with a static-site generator. md-serve is a single binary you drop into a directory and run: it turns your `.md` files into clean HTML pages, complete with navigation, breadcrumbs, and templating, and serves them locally over HTTP. No build step, no cloud account, no configuration required to get started.

It's the right tool when you want a lightweight, private, local web view of your Markdown without committing to a full documentation platform. Developers reach for it to browse their own notes or internal team wikis during development; it also works well as a small self-hosted docs server for projects that don't need the weight of GitBook or MkDocs.

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

### Manual download

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

### jbang

Install [jbang](https://www.jbang.dev/download/) once, then run md-serve directly — no manual download needed:

```sh
jbang md-serve@nikolassv/md-serve
```

To install as a named command available system-wide:

```sh
jbang app install md-serve@nikolassv/md-serve
md-serve
```
To upgrade from the latest release JAR on Github:

```sh
jbang app install --force md-serve@nikolassv/md-serve
```

## Quick start

```sh
./mvnw quarkus:dev
```

Open `http://localhost:8080`. Serves the current working directory by default.

## Configuration

| Property | Default | Description |
|---|---|---|
| `md-serve.source-dir` | `.` | Directory of Markdown files to serve |
| `md-serve.max-tree-depth` | `20` | Maximum directory depth for the navigation tree |
| `md-serve.port` | `int` | `8080` | TCP port the HTTP server listens on. |

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
