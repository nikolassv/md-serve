# md-serve — CLAUDE.md

## Project

Java/Quarkus server that renders Markdown files from a directory as HTML using Handlebars templates.

## Stack

- Java 21, Quarkus 3.32.2, Maven
- `com.github.jknack:handlebars:4.4.0` (template rendering)
- `com.vladsch.flexmark:flexmark-all:0.64.8` (Markdown parsing)
- `org.yaml:snakeyaml` via Quarkus BOM (front matter parsing)
- `io.rest-assured:rest-assured` (integration testing)

## Structure

```
src/main/java/de/nikolassv/mdserve/   Java source (base package: de.nikolassv.mdserve)
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

## Development Workflow

- Implementation is broken into numbered steps; specs live in `work/step-NN-*.md`
- When a step is complete and verified, mark it `— DONE` and move it to `work/done/`
- Verify each step's definition of done before moving on
- Commit after each completed step
- Do not add `Co-Authored-By` trailers to commit messages

### Documentation is mandatory before committing

**Always update `docs/` before creating a commit.** This is a hard requirement, not optional. For every step:

1. Update `docs/dev/architecture.md` for any new or changed components, helpers, design decisions, or template context variables.
2. Update `docs/user/configuration.md` for any new config properties or template variables/helpers visible to users.
3. Update `docs/user/getting-started.md` if the change affects the out-of-the-box user experience.

Do not wait to be reminded. Docs must be updated as part of the same step as the code, before the commit is made.
