# Step 7: Template Engine

## Goal

Load the correct Handlebars template and render a context map into a complete HTML page. Establish the default template as a working fallback.

## Implementation Plan

- Create `TemplateLoader` as an `@ApplicationScoped` bean injecting `MdServeConfig`:
  - If `md-serve.template` is set: load template from that file system path; throw a descriptive startup error if the file does not exist
  - Otherwise: load `templates/default.hbs` from the classpath
  - Cache the compiled `Template` instance (compilation is expensive)
- Create `TemplateRenderer` as an `@ApplicationScoped` bean injecting `TemplateLoader`:
  - Single public method:
    ```java
    String render(TemplateContext ctx)
    ```
  - `TemplateContext` is a record/POJO holding: `title`, `content`, `files`, `breadcrumbs`, `frontmatter`
  - Convert `TemplateContext` to a Handlebars `Context` object and apply the template
- Create `src/main/resources/templates/default.hbs`:
  - Minimal valid HTML5 skeleton: `<html>`, `<head>` with `<title>`, `<body>` with `{{{content}}}`
  - Placeholder comments where breadcrumbs and file listing will be added in step 10

## Definition of Done

- Application fails fast with a clear error message if a configured custom template path does not exist
- `TemplateRenderer.render()` produces valid HTML containing the provided title and content
- Default template is served correctly when no custom template is configured
- Unit test: render a minimal context with the default template and assert the output contains expected title and content
