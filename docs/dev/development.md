# Development Guide

## Prerequisites

- Java 21
- Maven (or use the included `./mvnw` wrapper)

## Build

```sh
./mvnw package
```

Produces `target/quarkus-app/` with a runnable Quarkus fast-jar.

## Run in dev mode

```sh
./mvnw quarkus:dev
```

Starts the server on `http://localhost:8080` with live reload. The source directory defaults to `./docs`.

## Run tests

```sh
./mvnw test
```

Integration tests use `@QuarkusTest` with REST Assured and run against a real Quarkus instance.

## Project structure

```
src/main/java/de/nikolassv/mdserve/
  MarkdownResource.java       JAX-RS resource: single GET /{path:.*} route
  MdServeConfig.java          MicroProfile Config mapping (source-dir, template)
  PathResolver.java           URL path → file system path, path traversal check

  markdown/
    DocumentParser.java       Reads a .md file and splits it into front matter + body
    FrontmatterParser.java    Parses YAML front matter to Map<String, Object>
    MarkdownRenderer.java     Converts Markdown body to an HTML fragment (Flexmark)
    TitleResolver.java        Derives title: frontmatter.title > first H1 > filename

  render/
    FileEntry.java            DTO for a directory listing entry (name, path, title)
    FileRenderer.java         Orchestrates file rendering (parse → render → template)
    DirectoryRenderer.java    Orchestrates directory listing rendering
    DirectoryIndexer.java     Lists .md files in a directory; extracts their titles
    ErrorRenderer.java        Renders 404 / 500 error pages via the template

  template/
    Breadcrumb.java           DTO for a breadcrumb entry (label, path)
    TemplateContext.java      Record holding all template variables
    TemplateLoader.java       Resolves which .hbs to use: custom path or classpath default
    TemplateRenderer.java     Merges a TemplateContext into a Handlebars template

src/main/resources/
  application.properties      Default config (source-dir=./docs)
  templates/default.hbs       Built-in Handlebars template
```

## Adding a new template variable

1. Add a field to `TemplateContext` (a Java record in `template/TemplateContext.java`).
2. Populate it in `FileRenderer` (for file requests) and/or `DirectoryRenderer` (for directory requests).
3. Reference it in your `.hbs` template as `{{variableName}}`.

There is no registration step — Handlebars receives the `TemplateContext` record directly and exposes every field by name.
