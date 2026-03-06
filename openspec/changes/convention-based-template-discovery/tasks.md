## 1. Remove `md-serve.template` configuration

- [x] 1.1 Delete `template()` method from `MdServeConfig`
- [x] 1.2 Remove `md-serve.template` from `application.properties` (if present)
- [x] 1.3 Update any `@QuarkusTestProfile` overrides that set `md-serve.template`

## 2. Add bundled templates

- [x] 2.1 Create `src/main/resources/templates/directory.hbs` (directory listing layout)
- [x] 2.2 Create `src/main/resources/templates/error.hbs` (error page layout)

## 3. Implement `TemplateRegistry`

- [x] 3.1 Create `de.nikolassv.mdserve.template.TemplateRegistry` as `@ApplicationScoped`
- [x] 3.2 On construction, compute `templateDir = sourceDir.resolve(".md-serve/templates")`
- [x] 3.3 For each role (`default`, `directory`, `error`): load user file if present, else load classpath fallback; fail to start on compilation error
- [x] 3.4 Load any additional `.hbs` files found in `templateDir` into the registry by name
- [x] 3.5 Expose `Template get(String name)` returning `default` template for unknown names

## 4. Update `TemplateRenderer`

- [x] 4.1 Inject `TemplateRegistry` instead of `TemplateLoader`
- [x] 4.2 Change `render(TemplateContext ctx)` to `render(TemplateContext ctx, String templateName)`
- [x] 4.3 Look up template by name from registry; fall back to `default` if not found

## 5. Update renderers

- [x] 5.1 `FileRenderer`: read `frontmatter.getOrDefault("template", "default")` and pass to `TemplateRenderer`
- [x] 5.2 `DirectoryRenderer`: pass `"directory"` to `TemplateRenderer`
- [x] 5.3 `ErrorRenderer`: pass `"error"` to `TemplateRenderer`

## 6. Delete `TemplateLoader`

- [x] 6.1 Delete `src/main/java/de/nikolassv/mdserve/template/TemplateLoader.java`
- [x] 6.2 Verify no remaining imports or references to `TemplateLoader`

## 7. Tests

- [x] 7.1 Unit: `TemplateRegistryTest` — no template dir: all three roles load from classpath
- [x] 7.2 Unit: `TemplateRegistryTest` — user `default.hbs` overrides role; others still use bundled
- [x] 7.3 Unit: `TemplateRegistryTest` — extra `custom.hbs` in dir is accessible by name
- [x] 7.4 Unit: `TemplateRegistryTest` — unknown name falls back to `default`
- [x] 7.5 Unit: `FileRendererTest` — front matter `template: custom` causes renderer to request `custom`
- [x] 7.6 Unit: `FileRendererTest` — no `template` in front matter causes renderer to request `default`
- [x] 7.7 Integration: minimal `default.hbs` written to `tempDir/.md-serve/templates/`; response body matches
- [x] 7.8 Integration: `GET /` (directory) returns 200 with HTML body
- [x] 7.9 Integration: `GET /nonexistent` returns 404 with HTML body

## 8. Verify and clean up

- [x] 8.1 Run `mvn verify` — all tests pass
- [x] 8.2 Update `docs/dev/architecture.md` (TemplateRegistry, role-based dispatch, discovery convention)
- [x] 8.3 Update `docs/user/configuration.md` (remove `md-serve.template`; document `.md-serve/templates/` convention)
- [x] 8.4 Update `docs/user/getting-started.md` if default UX is affected
- [x] 8.5 Delete `work/step-17-template-discovery.md`
- [x] 8.6 Update `README.md`
