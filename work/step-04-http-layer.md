# Step 4: HTTP Layer

## Goal

Expose a single HTTP endpoint that accepts all GET requests and routes them to the appropriate handler. At this stage handlers can return stubs; the goal is to establish the routing skeleton.

## Implementation Plan

- Create `MarkdownResource` with `@Path("/") @GET` catch-all:
  ```java
  @Path("/")
  public class MarkdownResource {
      @GET
      @Path("{path:.*}")
      @Produces(MediaType.TEXT_HTML)
      public Response serve(@PathParam("path") String path) { ... }
  }
  ```
- Inject `PathResolver`; call it with the incoming path
- Switch on the resolved kind:
  - `FILE` → return stub HTML `"<p>file: {path}</p>"` with 200
  - `DIRECTORY` → return stub HTML `"<p>directory: {path}</p>"` with 200
  - `NOT_FOUND` → return 404 with plain text body for now
- Handle the root path (`""`) by treating it as a directory request for source-dir itself

## Definition of Done

- `GET /` returns 200 with directory stub
- `GET /some/file` returns 200 with file stub when the file exists under source-dir
- `GET /nonexistent` returns 404
- `GET /../escape` returns 404 (path traversal blocked by `PathResolver`)
- Verified manually via `curl` or browser against a temporary docs directory
