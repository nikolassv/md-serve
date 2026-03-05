# Development Outline

## 1. Project Bootstrap
- Generate Quarkus project (Maven, Java 21)
- Add dependencies: REST (JAX-RS), Handlebars, CommonMark/Flexmark, SnakeYAML
- Verify `./mvnw quarkus:dev` starts cleanly

## 2. Configuration
- Define `MdServeConfig` with `source-dir` and `template` properties
- Set defaults in `application.properties`

## 3. Path Resolution
- Implement `PathResolver`: map URL path to file system path under source-dir
- Handle `.md` extension (add if missing)
- Reject paths that escape source-dir (path traversal prevention)
- Return distinct results: file found, directory found, not found

## 4. HTTP Layer
- Implement `MarkdownResource` with single catch-all route `GET /{path:.*}`
- Return 404 for not-found paths
- Delegate to file or directory handling (stubs for now)

## 5. Markdown Rendering
- Implement `MarkdownRenderer`: parse Markdown string to HTML fragment

## 6. Front Matter Parsing
- Implement `FrontmatterParser`: detect and strip `--- ... ---` block
- Parse YAML content to `Map<String, Object>`
- Return front matter map + remaining Markdown source

## 7. Template Engine
- Implement `TemplateLoader`: load custom template from config path, fall back to classpath `templates/default.hbs`
- Implement `TemplateRenderer`: build context map, render Handlebars template to HTML string
- Create default `templates/default.hbs` with minimal but functional HTML

## 8. File Request Pipeline
- Wire together: `PathResolver` → `FrontmatterParser` → `MarkdownRenderer` → `TemplateRenderer`
- Extract title (frontmatter.title > first H1 > filename)
- Build breadcrumbs from URL path segments

## 9. Directory Listing
- Implement `DirectoryIndexer`: list `.md` files in a directory
- Extract title for each file (front matter or first H1 or filename)
- Render directory listing via template (reuse `TemplateRenderer` with `files` context)

## 10. Default Template
- Flesh out `default.hbs`: clean HTML5 layout, breadcrumb nav, directory listing, file content area
- Include minimal inline CSS (no external dependencies)

## 11. Error Handling
- Return proper 404 page (also rendered via template)
- Handle unreadable files (500)

## 12. Testing
- Unit tests: `PathResolver`, `FrontmatterParser`, `MarkdownRenderer`, `DirectoryIndexer`
- Integration test: `MarkdownResource` with a temp directory of fixture `.md` files

## 13. Documentation
- Fill in `docs/dev/development.md` (build, run, test instructions)
- Fill in `docs/user/getting-started.md` and `docs/user/configuration.md`
