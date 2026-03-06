# Step 15 — Hide Dot-Prefixed Files and Directories

## Summary

Treat any file or directory whose name starts with a dot as hidden. A request whose URL path contains a hidden segment returns 404. Hidden entries are excluded from directory listings. This prevents accidentally exposing `.git/`, `.env`, `.md-serve/` (introduced in step 17), and similar internal paths.

## Background / Current Behavior

- `PathResolver.resolve()` resolves URL paths to filesystem paths and checks for traversal, but does not inspect individual path segments.
- `DirectoryIndexer.list()` already filters out non-Markdown non-directory entries, but does not filter dot-prefixed names.
- A request for `/.git/config` currently resolves to a file and is rendered as Markdown (or will be served as a raw asset after step 14).
- A directory listing for the source root shows `.git/` if it is present.

## Desired Behavior

- Any URL path segment that starts with a dot causes `PathResolver.resolve()` to return `Kind.NOT_FOUND`.
- `DirectoryIndexer.list()` skips entries whose filename starts with a dot.
- Both rules apply regardless of depth (e.g. `docs/.hidden/page.md` is also blocked).
- The `.md-serve/` convention directory (introduced in step 17) is automatically hidden by this rule.

## Scope

**In scope**
- Segment check in `PathResolver.resolve()` before filesystem access.
- Filtering in `DirectoryIndexer.list()`.
- Unit tests for both.

**Out of scope**
- Configurable allowlist/denylist of hidden patterns.
- Hiding files whose content is sensitive but whose name does not start with a dot.

## Implementation Plan

1. **`PathResolver.resolve()`**: After stripping the leading slash, split the stripped path on `/` and check each segment. If any segment starts with `.`, return `new Result(Kind.NOT_FOUND, candidate)` immediately (before any filesystem access).

2. **`DirectoryIndexer.list()`**: Inside the `forEach` lambda, add an early-continue if `name.startsWith(".")`.

3. **Tests**:
   - `PathResolverTest`: assert that `resolve("/.git/config")`, `resolve(".env")`, and `resolve("docs/.hidden/page.md")` all return `Kind.NOT_FOUND`.
   - `DirectoryIndexerTest`: write a `.hidden` directory and a `.env` file alongside normal entries; assert neither appears in the result.
   - Integration: extend `MarkdownResourceTest` with a `GET /.secret` test asserting 404.

## Relevant Code Pointers

- `src/main/java/de/nikolassv/mdserve/PathResolver.java:24` — `resolve()`, add segment check at the top
- `src/main/java/de/nikolassv/mdserve/render/DirectoryIndexer.java:23` — `forEach` lambda, add dot filter
- `src/test/java/de/nikolassv/mdserve/PathResolverTest.java` — extend with hidden-path cases
- `src/test/java/de/nikolassv/mdserve/render/DirectoryIndexerTest.java` — extend with dot-entry cases
- `src/test/java/de/nikolassv/mdserve/MarkdownResourceTest.java` — extend with hidden-file 404 case

## Acceptance Criteria

- [ ] `GET /.git/config` returns 404.
- [ ] `GET /.env` returns 404.
- [ ] `GET /docs/.hidden/page.md` returns 404 even if the file exists on disk.
- [ ] A directory listing does not include any entry whose name starts with `.`.
- [ ] Normal files and directories are unaffected.
- [ ] `mvn verify` passes with no new failures.

## Testing Plan

**Unit — `PathResolverTest`**
- Single leading-dot segment: `resolve(".env")` → `NOT_FOUND`.
- Dot segment at depth: `resolve("docs/.hidden/page.md")` → `NOT_FOUND`.
- Normal path still resolves: existing passing tests remain green.

**Unit — `DirectoryIndexerTest`**
- Place `.hidden/`, `.env`, `readme.md`, and `subdir/` in a `@TempDir`; call `list()`; assert only `readme.md` and `subdir/` are returned.

**Integration — `MarkdownResourceTest`**
- Write `.secret` to `tempDir`; assert `GET /.secret` → 404.

## Definition of Done

- [ ] Segment check implemented in `PathResolver.resolve()`.
- [ ] Dot-entry filter implemented in `DirectoryIndexer.list()`.
- [ ] Unit and integration tests added and passing.
- [ ] `mvn verify` passes.
- [ ] Step marked DONE and moved to `work/done/`.
