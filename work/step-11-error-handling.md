# Step 11: Error Handling

## Goal

Return meaningful, template-rendered error pages instead of raw error responses. Errors should be visually consistent with the rest of the application.

## Implementation Plan

- **404 Not Found**: replace the plain-text 404 from step 4 with a rendered page
  - Construct a `TemplateContext` with `title = "Not Found"`, a short `content` HTML string, and breadcrumbs
  - Return with HTTP 404 status
- **500 Internal Server Error**: catch unexpected exceptions in `MarkdownResource`
  - Log the exception with full stack trace
  - Return a rendered error page with `title = "Error"` and a generic message (do not expose internal details)
  - Return with HTTP 500 status
- **Unreadable file**: if reading a file throws an `IOException`, treat as 500
- **Template rendering failure**: if `TemplateRenderer` throws, fall back to a plain-text response to avoid an infinite error loop

## Definition of Done

- `GET /nonexistent` returns HTTP 404 with an HTML page rendered by the default template
- A simulated read error returns HTTP 500 with a rendered error page
- Exceptions are logged server-side with enough detail to diagnose the problem
- Error pages include breadcrumbs and are visually consistent with normal pages
- Integration tests assert correct HTTP status codes and that response `Content-Type` is `text/html` for error responses
