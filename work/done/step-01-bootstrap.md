# Step 1: Project Bootstrap — DONE

## Goal

Establish a working Quarkus Maven project with all required dependencies declared. The application should start without errors in dev mode.

## Implementation Plan

- Generate project via Quarkus CLI or Maven archetype:
  - Group: `io.mdserve`, Artifact: `md-serve`, Java 21
  - Extensions: `quarkus-rest` (JAX-RS)
- Add remaining dependencies to `pom.xml`:
  - `flexmark-all` or `commonmark` — Markdown parsing
  - `github.jknack:handlebars` — Handlebars templating
  - `org.yaml:snakeyaml` — YAML front matter parsing (likely already a transitive dep)
- Remove generated example resource and test if present
- Confirm `application.properties` is in place (can be empty for now)

## Definition of Done

- `./mvnw quarkus:dev` starts without errors
- `./mvnw test` passes (even if no tests exist yet)
- All declared dependencies resolve without conflicts
