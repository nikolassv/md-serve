package de.nikolassv.mdserve;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class FileRenderer {

    @Inject FrontmatterParser frontmatterParser;
    @Inject MarkdownRenderer markdownRenderer;
    @Inject TemplateRenderer templateRenderer;
    @Inject TitleResolver titleResolver;

    public String render(Path filePath, String urlPath) throws IOException {
        String raw = Files.readString(filePath, StandardCharsets.UTF_8);
        FrontmatterParser.ParseResult parsed = frontmatterParser.parse(raw);
        String htmlContent = markdownRenderer.render(parsed.body());
        String title = titleResolver.resolve(parsed.frontmatter(), htmlContent, filePath.getFileName().toString());
        List<TemplateContext.Breadcrumb> breadcrumbs = buildBreadcrumbs(urlPath);
        TemplateContext ctx = new TemplateContext(title, htmlContent, null, breadcrumbs, parsed.frontmatter());
        return templateRenderer.render(ctx);
    }

    private List<TemplateContext.Breadcrumb> buildBreadcrumbs(String urlPath) {
        if (urlPath == null || urlPath.isBlank()) return Collections.emptyList();
        List<TemplateContext.Breadcrumb> crumbs = new ArrayList<>();
        StringBuilder cumulative = new StringBuilder();
        for (String part : urlPath.split("/")) {
            if (part.isBlank()) continue;
            cumulative.append("/").append(part);
            crumbs.add(new TemplateContext.Breadcrumb(cumulative.toString(), part));
        }
        return crumbs;
    }
}
