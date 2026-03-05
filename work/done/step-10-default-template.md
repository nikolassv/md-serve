# Step 10: Default Template — DONE

## Goal

Replace the minimal placeholder template from step 7 with a complete, polished default that renders file content, directory listings, breadcrumb navigation, and front matter — with clean styling and no external dependencies.

## Implementation Plan

- Update `src/main/resources/templates/default.hbs`:
  - Valid HTML5 with `<meta charset>` and `<meta name="viewport">`
  - `<title>{{title}}</title>`
  - Breadcrumb navigation: iterate `{{#each breadcrumbs}}` — render as `<a href="{{path}}">{{label}}</a>` separated by `/`; last entry not linked
  - Conditional content block: `{{#if content}}` render `{{{content}}}` (triple-stache, content is already HTML)
  - Conditional file listing: `{{#if files}}` render `<ul>` of `<a href="{{path}}">{{title}}</a>` entries
  - Minimal inline CSS in `<style>`: readable body font, max-width container, styled `<pre>`/`<code>`, basic link colors, breadcrumb separator — no frameworks, no external fonts
  - Front matter values are available to template authors via `{{frontmatter.*}}`; the default template itself does not render arbitrary front matter fields, but documents this capability via a comment

## Definition of Done

- Default template renders a complete, readable page for both file and directory requests
- Breadcrumb nav is present and links are correct
- File listing renders as a navigable link list
- `<pre>` / `<code>` blocks are visually distinct
- Page is usable without any external network requests (all styles inline)
- Manually verified in a browser for both a file page and a directory listing page
