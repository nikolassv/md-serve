## Why

When a directory is requested and an `index.md` file exists within it, the server currently shows a generic directory listing. Users expect `index.md` to act as the "home page" of a directory, making it confusing to see a listing instead of the document.

## What Changes

- Directory requests that find an `index.md` file now return a `301 Moved Permanently` redirect to the `index.md` URL instead of rendering a directory listing.
- Directories without an `index.md` continue to show the directory listing as before.

## Capabilities

### New Capabilities

- `index-md-redirect`: When a directory contains `index.md`, a `GET` request to the directory path redirects (301) to `<path>/index.md`.

### Modified Capabilities

<!-- No existing spec-level behavior changes -->

## Impact

- `FileRequestHandler` (or equivalent JAX-RS resource) — add index.md detection before directory listing logic
- No new dependencies required
- No configuration changes required
- Existing directory listing behaviour is preserved for directories without `index.md`
