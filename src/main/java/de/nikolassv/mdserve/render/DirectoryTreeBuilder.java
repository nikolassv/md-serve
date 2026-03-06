package de.nikolassv.mdserve.render;

import de.nikolassv.mdserve.MdServeConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ApplicationScoped
public class DirectoryTreeBuilder {

    private static final Logger LOG = Logger.getLogger(DirectoryTreeBuilder.class);

    private final Path sourceDir;
    private final int maxDepth;

    @Inject
    public DirectoryTreeBuilder(MdServeConfig config) {
        this.sourceDir = Paths.get(config.sourceDir()).toAbsolutePath().normalize();
        this.maxDepth = config.maxTreeDepth();
    }

    public List<TreeNode> build(String currentUrlPath) {
        return buildChildren(sourceDir, currentUrlPath, 0);
    }

    private List<TreeNode> buildChildren(Path dir, String currentUrlPath, int depth) {
        if (depth >= maxDepth) {
            LOG.warnf("Max tree depth (%d) reached at %s; skipping deeper entries", maxDepth, dir);
            return List.of();
        }

        List<TreeNode> nodes = new ArrayList<>();
        try (var stream = Files.list(dir)) {
            stream.sorted(Comparator.comparing(p -> p.getFileName().toString()))
                  .forEach(p -> {
                      String fileName = p.getFileName().toString();
                      if (fileName.startsWith(".")) return;

                      // Do not follow symlinks — avoids loops
                      if (Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS)) {
                          String relativePath = sourceDir.relativize(p).toString().replace('\\', '/');
                          String nodePath = "/" + relativePath;
                          List<TreeNode> children = buildChildren(p, currentUrlPath, depth + 1);
                          boolean anyChildActive = children.stream().anyMatch(TreeNode::active);
                          boolean active = isActive(nodePath, currentUrlPath) || anyChildActive;
                          nodes.add(new TreeNode(fileName, nodePath, true, active, children));
                      } else if (fileName.endsWith(".md") && Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS)) {
                          String relativePath = sourceDir.relativize(p).toString().replace('\\', '/');
                          String name = fileName.substring(0, fileName.length() - 3);
                          String nodePath = "/" + relativePath.substring(0, relativePath.length() - 3);
                          nodes.add(new TreeNode(name, nodePath, false, isActive(nodePath, currentUrlPath), List.of()));
                      }
                  });
        } catch (IOException e) {
            LOG.warnf("Failed to list directory %s: %s", dir, e.getMessage());
        }
        return nodes;
    }

    private boolean isActive(String nodePath, String currentUrlPath) {
        if (currentUrlPath == null) return false;
        return currentUrlPath.equals(nodePath) || currentUrlPath.startsWith(nodePath + "/");
    }
}
