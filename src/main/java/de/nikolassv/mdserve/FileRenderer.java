package de.nikolassv.mdserve;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class FileRenderer {

    @Inject DocumentParser documentParser;
    @Inject TemplateRenderer templateRenderer;

    public String render(Path filePath, String urlPath) throws IOException {
        DocumentParser.ParsedDocument doc = documentParser.parse(filePath);
        List<TemplateContext.Breadcrumb> breadcrumbs = buildBreadcrumbs(urlPath);
        TemplateContext ctx = new TemplateContext(doc.title(), doc.content(), null, breadcrumbs, doc.frontmatter());
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
