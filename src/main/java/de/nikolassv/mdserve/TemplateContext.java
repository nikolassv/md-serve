package de.nikolassv.mdserve;

import java.util.List;
import java.util.Map;

public record TemplateContext(
        String title,
        String content,
        List<DirectoryIndexer.FileEntry> files,
        List<Breadcrumb> breadcrumbs,
        Map<String, Object> frontmatter
) {
    public record Breadcrumb(String path, String label) {}
}
