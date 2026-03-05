# Step 12: Testing — DONE

## Goal

Ensure all components are covered by focused unit tests and at least one end-to-end integration test, so the application can be refactored and extended with confidence.

## Implementation Plan

Prior steps each define their own unit tests. This step fills any remaining gaps and adds a comprehensive integration test suite.

### Unit test coverage review
- `PathResolver` — traversal, extension handling, not-found (step 3)
- `FrontmatterParser` — valid/missing/malformed YAML (step 6)
- `MarkdownRenderer` — standard Markdown elements (step 5)
- `DirectoryIndexer` — mixed files and subdirectories (step 9)
- Title extraction helper — all three fallback levels (step 8)

### Integration tests (`@QuarkusTest`)
- Set up a temporary directory tree of fixture `.md` files via `@BeforeAll` / `@TempDir`; point `md-serve.source-dir` at it using `@QuarkusTestResource` or config override
- Test cases:
  - `GET /` → 200, HTML contains file listing
  - `GET /page` → 200, rendered content includes expected H1
  - `GET /page` with front matter → 200, title from front matter
  - `GET /subdir/` → 200, directory listing for subdirectory
  - `GET /nonexistent` → 404
  - `GET /../escape` → 404
  - Custom template via `md-serve.template` config → rendered output reflects custom template

### Coverage goal
- All public methods of all beans covered by at least one test
- No untested branches in `PathResolver` or `FrontmatterParser`

## Definition of Done

- `./mvnw test` passes with no failures
- Integration tests run against a real (in-process) Quarkus instance
- No Mockito or heavy mocking — prefer real implementations with fixture data
