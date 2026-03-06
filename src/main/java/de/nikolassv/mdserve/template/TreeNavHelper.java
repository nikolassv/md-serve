package de.nikolassv.mdserve.template;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import de.nikolassv.mdserve.render.TreeNode;

import java.io.IOException;
import java.util.List;

/**
 * Handlebars helper that renders a {@code List<TreeNode>} as a nested
 * {@code <ul>} tree.  Directories use {@code <details>/<summary>} for
 * native browser collapse/expand (no JavaScript required).  An active
 * directory is rendered with the {@code open} attribute so its children
 * are visible on page load.
 *
 * <p>Usage in a template: {@code {{{treeNav tree}}}}
 */
public class TreeNavHelper implements Helper<List<TreeNode>> {

    @Override
    public Object apply(List<TreeNode> nodes, Options options) throws IOException {
        if (nodes == null || nodes.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        renderList(nodes, sb);
        return sb.toString();
    }

    private void renderList(List<TreeNode> nodes, StringBuilder sb) {
        sb.append("<ul class=\"tree-list\">");
        for (TreeNode node : nodes) {
            renderNode(node, sb);
        }
        sb.append("</ul>");
    }

    private void renderNode(TreeNode node, StringBuilder sb) {
        sb.append("<li");
        if (node.active()) sb.append(" class=\"active\"");
        sb.append(">");

        if (node.directory()) {
            sb.append("<details");
            if (node.active()) sb.append(" open");
            sb.append("><summary><a href=\"")
              .append(escape(node.path()))
              .append("\">")
              .append(escape(node.name()))
              .append("</a></summary>");
            if (!node.children().isEmpty()) {
                renderList(node.children(), sb);
            }
            sb.append("</details>");
        } else {
            sb.append("<a href=\"")
              .append(escape(node.path()))
              .append("\"")
              .append(node.active() ? " class=\"active\"" : "")
              .append(">")
              .append(escape(node.name()))
              .append("</a>");
        }

        sb.append("</li>");
    }

    private String escape(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
}
