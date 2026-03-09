## 1. Core Implementation

- [x] 1.1 In `MarkdownResource.serve()`, after `PathResolver` returns `Kind.DIRECTORY`, check if `resolved.resolve("index.md")` is a regular file
- [x] 1.2 If `index.md` exists, build the redirect URL (`/<urlPath>/index.md`, normalising double slashes) and return `Response.status(301).location(URI).build()`

## 2. Tests

- [x] 2.1 Add integration test: GET on a directory containing `index.md` returns 301 with correct `Location` header
- [x] 2.2 Add integration test: GET on a directory without `index.md` still returns 200 with directory listing
- [x] 2.3 Add integration test: GET on a path without trailing slash (e.g. `/docs`) with `index.md` present returns 301 to `/docs/index.md`

## 3. Documentation

- [x] 3.1 Update `docs/dev/architecture.md` to describe the index.md redirect behaviour in the request-handling section
- [x] 3.2 Update `docs/user/getting-started.md` to mention that placing `index.md` in a directory causes it to be the landing page for that directory
- [x] 3.3 Update `CHANGELOG.md` under `[Unreleased]`
