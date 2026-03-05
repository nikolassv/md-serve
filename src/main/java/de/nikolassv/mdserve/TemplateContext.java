package de.nikolassv.mdserve;

import java.util.List;
import java.util.Map;

public record TemplateContext(
        String title,
        String content,
        List<DirectoryIndexer.FileEntry> files,
        List<String> breadcrumbs,
        Map<String, Object> frontmatter
) {}
