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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class FileRenderer {

    private static final Pattern H1_PATTERN = Pattern.compile("<h1[^>]*>(.*?)</h1>", Pattern.CASE_INSENSITIVE);

    @Inject FrontmatterParser frontmatterParser;
    @Inject MarkdownRenderer markdownRenderer;
    @Inject TemplateRenderer templateRenderer;

    public String render(Path filePath, String urlPath) throws IOException {
        String raw = Files.readString(filePath, StandardCharsets.UTF_8);
        FrontmatterParser.ParseResult parsed = frontmatterParser.parse(raw);
        String htmlContent = markdownRenderer.render(parsed.body());
        String title = resolveTitle(parsed.frontmatter(), htmlContent, filePath.getFileName().toString());
        List<String> breadcrumbs = buildBreadcrumbs(urlPath);
        TemplateContext ctx = new TemplateContext(title, htmlContent, null, breadcrumbs, parsed.frontmatter());
        return templateRenderer.render(ctx);
    }

    private String resolveTitle(Map<String, Object> frontmatter, String html, String filename) {
        Object fm = frontmatter.get("title");
        if (fm != null) return fm.toString();
        Matcher m = H1_PATTERN.matcher(html);
        if (m.find()) return m.group(1);
        return filename.replaceAll("\\.md$", "");
    }

    private List<String> buildBreadcrumbs(String urlPath) {
        if (urlPath == null || urlPath.isBlank()) return Collections.emptyList();
        String[] parts = urlPath.split("/");
        List<String> crumbs = new ArrayList<>();
        StringBuilder cumulative = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) continue;
            cumulative.append("/").append(part);
            crumbs.add(cumulative.toString());
        }
        return crumbs;
    }
}
