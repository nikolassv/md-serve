# Step 14 — Serve Static Asset Files from Source Directory — DONE

## Summary

Markdown files often reference local assets (images, PDFs, downloadable files) using relative paths. Currently, any request that does not resolve to a `.md` file or directory returns a 404. This step adds pass-through serving of arbitrary files from the source directory with correct MIME types, bypassing the Markdown/template pipeline entirely.

## Background / Current Behavior

- `PathResolver.resolve()` (`PathResolver.java:38`) already returns `Kind.FILE` for any `Files.isRegularFile(candidate)` match, regardless of extension.
- `MarkdownResource.serve()` (`MarkdownResource.java:34`) routes `Kind.FILE` exclusively to `FileRenderer.render()`, which parses the file as Markdown and wraps it in a Handlebars template. Serving a PNG through this pipeline would produce garbage or an exception.
- `PathResolver` already enforces path traversal prevention for all paths; asset serving inherits this for free.
- There is no MIME type detection or binary file handling anywhere in the codebase today.

## Desired Behavior

- A request for any file within `source-dir` that is **not** a Markdown file (`.md`) is served as a raw byte stream with the correct `Content-Type` header derived from the file extension.
- Markdown files continue to be rendered as before.
- Path traversal prevention is unchanged — non-Markdown files outside `source-dir` return 404.
- Binary files (images, PDFs) and text files (CSS, JS, plain text) are both handled correctly.
- No template wrapping, no front matter parsing, no HTML generation.

## Scope

**In scope**
- Detecting whether a resolved `FILE` path is a Markdown file vs. an asset.
- Reading and streaming the asset file bytes with the correct `Content-Type`.
- Reusing the existing `PathResolver` result and traversal check.
- Unit test for the new asset-serving code path.
- Integration test in `MarkdownResourceTest` for at least one binary and one text asset type.

**Out of scope**
- Caching, ETags, or `Last-Modified` headers.
- Range requests / partial content (HTTP 206).
- Listing assets in directory indexes.
- Configurable allowlist/blocklist of file extensions.

## Implementation Plan

1. **Detect Markdown vs. asset in `MarkdownResource`**: After `PathResolver` returns `Kind.FILE`, check whether the resolved path ends with `.md` (case-insensitive). If not, branch to asset handling.

2. **Determine MIME type**: Use `java.nio.file.Files.probeContentType(path)` for MIME detection. Fall back to `application/octet-stream` if it returns `null`.

3. **Stream the file**: Return a JAX-RS `Response` with the file bytes. Use `StreamingOutput` backed by `Files.newInputStream()` to avoid loading the entire file into memory.

4. **Relax `@Produces`**: Change `@Produces(MediaType.TEXT_HTML)` on `MarkdownResource.serve()` to `@Produces(MediaType.WILDCARD)` so JAX-RS does not reject requests whose `Accept` header does not include `text/html`.

5. **Add an `AssetRenderer`**: A thin `@ApplicationScoped` bean in `de.nikolassv.mdserve.render` that accepts a `Path` and returns a `Response`. This mirrors the `FileRenderer`/`DirectoryRenderer`/`ErrorRenderer` pattern and keeps `MarkdownResource` slim.

6. **Tests**:
   - Unit: `AssetRendererTest` — verify correct MIME type and body bytes for a PNG and a `.css` file; verify `application/octet-stream` fallback.
   - Integration: extend `MarkdownResourceTest.Profile` to write a small binary fixture and a `.css` file; assert status 200, correct `Content-Type`, and non-HTML body.

## Relevant Code Pointers

- `src/main/java/de/nikolassv/mdserve/MarkdownResource.java` — add asset branch in `serve()`, relax `@Produces`
- `src/main/java/de/nikolassv/mdserve/render/FileRenderer.java` — pattern to follow for new `AssetRenderer`
- `src/main/java/de/nikolassv/mdserve/render/ErrorRenderer.java` — pattern for a renderer returning a full `Response`
- `src/main/java/de/nikolassv/mdserve/PathResolver.java` — traversal check already covers assets; no changes expected
- Tests:
  - `src/test/java/de/nikolassv/mdserve/MarkdownResourceTest.java` — extend with asset test cases
  - `src/test/java/de/nikolassv/mdserve/render/FileRendererTest.java` — pattern for new `AssetRendererTest`

## API / Interface Changes

 | Request               | Before               | After                       |
 |-----------------------|----------------------|-----------------------------|
 | `GET /image.png`      | 404 (no `.md` match) | 200 `image/png`             |
 | `GET /docs/style.css` | 404                  | 200 `text/css`              |
 | `GET /hello.md`       | 200 `text/html`      | 200 `text/html` (unchanged) |
 | `GET /../secret`      | 404                  | 404 (unchanged)             |

## Considerations

**Security**
- Path traversal is already prevented by `PathResolver`; no new exposure.
- All files within `source-dir` are intentionally public by design (that is the server's purpose). No additional authZ is needed.

**Performance**
- Use `StreamingOutput` / `Files.newInputStream()` rather than `Files.readAllBytes()` to avoid heap pressure on large files.

**Edge cases**
- `Files.probeContentType()` may return `null` on some JVMs/OSes — handle with `application/octet-stream` fallback.
- A file named `readme.MD` (uppercase extension) should still be treated as Markdown; use a case-insensitive extension check.
- Empty asset files: stream an empty body with 200 and correct `Content-Type`.
- Symlinks pointing outside `source-dir`: `PathResolver`'s `normalize()` + `startsWith()` check handles this correctly already.

**Dependencies**
- `java.nio.file.Files.probeContentType()` — JDK standard library, no new Maven dependency.
- `jakarta.ws.rs.core.StreamingOutput` — already on the classpath via Quarkus RESTEasy.

## Acceptance Criteria

- [ ] `GET /<image>.png` returns 200 with `Content-Type: image/png` and the raw file bytes.
- [ ] `GET /<file>.css` returns 200 with `Content-Type: text/css` and the file contents.
- [ ] `GET /<doc>.md` continues to return 200 with `Content-Type: text/html` and rendered HTML.
- [ ] `GET /nonexistent.png` returns 404.
- [ ] `GET /../escape.png` returns 404 (path traversal blocked).
- [ ] A file with an unrecognized extension is served with `Content-Type: application/octet-stream`.
- [ ] Asset files are not processed by `DocumentParser` or `TemplateRenderer`.

## Testing Plan

**Unit tests (`AssetRendererTest`)**
- Write a small binary blob and a `.css` file to a `@TempDir`; call `AssetRenderer` directly and assert `Content-Type` and body bytes.
- Assert fallback to `application/octet-stream` for a file with an unrecognized extension.

**Integration tests (extend `MarkdownResourceTest`)**
- In `Profile.getConfigOverrides()`: write a `style.css` and a minimal binary file to `tempDir`.
- Assert `GET /style.css` → 200, `Content-Type` contains `text/css`, body contains expected CSS text.
- Assert `GET /nonexistent.gif` → 404.
- Existing tests must continue to pass unchanged.

**Manual verification**
1. Start server with `mvn quarkus:dev -Dmd-serve.source-dir=<dir-with-images>`.
2. Open a Markdown file referencing `![alt](image.png)` — verify the image loads in the browser.
3. Directly navigate to the image URL — verify the browser displays it inline.

## Definition of Done

- [ ] Code changes implemented per plan and consistent with existing render-layer patterns.
- [ ] `@Produces` annotation updated to `WILDCARD` on `MarkdownResource.serve()`.
- [ ] `AssetRenderer` added in the `render` package.
- [ ] Unit and integration tests added and passing.
- [ ] `mvn verify` passes with no new failures.
- [ ] Markdown files rendered through `FileRenderer` are unaffected.
- [ ] Step marked DONE and moved to `work/done/`.

## Assumptions

- A1: All files within `source-dir` are intended to be publicly accessible; no per-file access control is needed.
- A2: Serving `.html` files as raw assets (not template-rendered) is acceptable; if they should be rendered, that is a separate decision.
- A3: Directory index listings do not need to distinguish asset files from Markdown files (out of scope for this step).
