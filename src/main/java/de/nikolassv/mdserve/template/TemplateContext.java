package de.nikolassv.mdserve.template;

import de.nikolassv.mdserve.render.FileEntry;
import de.nikolassv.mdserve.render.TreeNode;

import java.util.List;
import java.util.Map;

public record TemplateContext(
        String title,
        String content,
        List<FileEntry> files,
        List<Breadcrumb> breadcrumbs,
        Map<String, Object> frontmatter,
        List<TreeNode> tree
) {
}
