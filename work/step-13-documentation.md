# Step 13: Documentation

## Goal

Ensure all documentation files contain accurate, complete content that reflects the finished application.

## Implementation Plan

### `docs/dev/development.md`
- Prerequisites: Java 21, Maven
- Build: `./mvnw package`
- Run in dev mode: `./mvnw quarkus:dev`
- Run tests: `./mvnw test`
- Project structure walkthrough (key packages and classes)
- How to add a new Handlebars template variable (extension point)

### `docs/user/getting-started.md`
- What md-serve does (one paragraph)
- How to run: download/build, point at a directory, open browser
- Minimal example: a single Markdown file served as HTML

### `docs/user/configuration.md`
- All supported `application.properties` keys with types, defaults, and descriptions
- Example `application.properties` snippets
- Front matter reference: supported keys, how to use custom keys in a template
- Custom template guide: how to create a `.hbs` file, available context variables

### `README.md`
- Update to reflect final state: correct configuration table, usage instructions, link to docs

## Definition of Done

- All four documents are complete with no `TODO` placeholders remaining
- Configuration table in `docs/user/configuration.md` matches the actual `MdServeConfig` interface
- A new user can follow `docs/user/getting-started.md` from zero to a running server without additional guidance
- `README.md` gives an accurate one-page overview of the project
