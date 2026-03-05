package de.nikolassv.mdserve;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class TitleResolver {

    private static final Pattern H1_PATTERN = Pattern.compile("<h1[^>]*>(.*?)</h1>", Pattern.CASE_INSENSITIVE);

    /**
     * Resolves a page title in priority order:
     * 1. frontmatter.title
     * 2. First H1 in the rendered HTML
     * 3. Filename without .md extension
     */
    public String resolve(Map<String, Object> frontmatter, String html, String filename) {
        Object fm = frontmatter.get("title");
        if (fm != null) return fm.toString();

        if (html != null) {
            Matcher m = H1_PATTERN.matcher(html);
            if (m.find()) return m.group(1);
        }

        return filename.replaceAll("\\.md$", "");
    }
}
