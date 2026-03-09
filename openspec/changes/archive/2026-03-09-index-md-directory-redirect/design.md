## Context

The `MarkdownResource.serve()` method delegates path resolution to `PathResolver`, which returns a `Result` with kind `DIRECTORY` when the resolved path is a directory. The resource then calls `DirectoryRenderer.render()` unconditionally. There is no existing hook for index file detection.

The change is localised: we only need to add a check in `MarkdownResource.serve()` (or `PathResolver.resolve()`) for the existence of `index.md` within the resolved directory, and return a redirect before the directory renderer is invoked.

## Goals / Non-Goals

**Goals:**
- Return HTTP 301 to `<path>/index.md` when a requested directory contains an `index.md` file
- No change to behaviour when `index.md` is absent — directory listing renders as before

**Non-Goals:**
- Supporting other index filenames (e.g. `README.md`, `index.html`)
- Rendering `index.md` inline instead of redirecting
- Making the redirect configurable

## Decisions

### Where to add the index check

**Decision:** Add the check inside `MarkdownResource.serve()`, after `PathResolver` returns `Kind.DIRECTORY`, rather than inside `PathResolver` itself.

**Rationale:** `PathResolver`'s responsibility is path validation and kind detection, not HTTP redirect logic. Putting redirect logic in the resource keeps the layers clean. `PathResolver` stays a pure path utility with no knowledge of HTTP semantics.

**Alternatives considered:**
- Add a new `Kind.DIRECTORY_WITH_INDEX` from `PathResolver` — rejected because it leaks HTTP concerns into the resolver and requires a new kind value for a one-liner check.
- Modify `DirectoryRenderer` to return a redirect — rejected because the renderer should not inspect sibling files or produce non-HTML responses.

### Redirect status code

**Decision:** Use `301 Moved Permanently`.

**Rationale:** The requirement specifies 301. `index.md` is a static file on disk; the redirect target won't change unless the file is deleted, so a permanent redirect is appropriate.

### Redirect URL construction

**Decision:** Build the redirect URL as `/<normalised-urlPath>/index.md` using JAX-RS `UriInfo` or a simple string construction from the incoming `urlPath`.

**Rationale:** The `urlPath` path parameter in `MarkdownResource.serve()` is already the normalised relative path. We just append `/index.md`, ensuring no double slashes by trimming any trailing slash.

## Risks / Trade-offs

- **Redirect loop**: If `index.md` somehow resolves back to a directory, the loop cannot occur because `PathResolver` will return `Kind.FILE` for a `.md` file, not `Kind.DIRECTORY`.
- **301 caching**: Browsers cache 301 permanently. If a user deletes `index.md` later, they may still be redirected. This is the standard trade-off of 301; it is consistent with the stated requirement.
