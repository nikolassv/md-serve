# md-serve — CLAUDE.md

## Project

Java/Quarkus server that renders Markdown files from a directory as HTML using Handlebars templates.

## Stack

- Java 21, Quarkus, Maven
- Handlebars (template rendering)
- CommonMark or Flexmark (Markdown parsing — TBD)
- SnakeYAML or similar (front matter parsing — TBD)

## Structure

```
src/main/java/         Java source
src/main/resources/
  application.properties
  templates/default.hbs   built-in Handlebars template
docs/
  dev/                 developer documentation (Markdown)
  user/                user documentation (Markdown)
work/                  tickets and specs
```

## Key Design Decisions

- Config properties: `md-serve.source-dir`, `md-serve.template`
- Default template is bundled in resources; custom template path overrides it
- YAML front matter (`--- ... ---`) is stripped before Markdown rendering and passed to Handlebars as `frontmatter` map
- Title resolution order: `frontmatter.title` > first H1 in content > filename
- Single JAX-RS route `GET /{path:.*}` handles both file and directory requests
- Path traversal prevention: resolved path must stay within source-dir

## Conventions

- Follow standard Quarkus project layout
- Use MicroProfile Config for all configuration
- Keep dependencies minimal
