package de.nikolassv.mdserve.render;

import de.nikolassv.mdserve.MdServeConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryTreeBuilderTest {

    @TempDir Path root;

    DirectoryTreeBuilder builder;

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectory(root.resolve(".hidden"));
        Files.createDirectory(root.resolve("docs"));
        Files.writeString(root.resolve("docs/intro.md"), "# Intro");
        Files.writeString(root.resolve("docs/.draft.md"), "draft");
        Files.writeString(root.resolve("index.md"), "# Home");

        builder = new DirectoryTreeBuilder(configFor(root));
    }

    // --- Structural tests ---

    @Test
    void topLevelContainsDocsAndIndex() {
        List<TreeNode> nodes = builder.build("/");
        List<String> names = names(nodes);
        assertEquals(List.of("docs", "index"), names);
    }

    @Test
    void hiddenDirectoryIsExcluded() {
        List<TreeNode> nodes = builder.build("/");
        assertTrue(names(nodes).stream().noneMatch(n -> n.startsWith(".")));
    }

    @Test
    void docsChildrenContainsIntroOnly() {
        TreeNode docs = findByName(builder.build("/"), "docs");
        assertNotNull(docs);
        assertTrue(docs.directory());
        assertEquals(List.of("intro"), names(docs.children()));
    }

    @Test
    void introNodeHasCorrectAttributes() {
        TreeNode docs = findByName(builder.build("/"), "docs");
        TreeNode intro = findByName(docs.children(), "intro");
        assertNotNull(intro);
        assertEquals("/docs/intro", intro.path());
        assertFalse(intro.directory());
        assertTrue(intro.children().isEmpty());
    }

    @Test
    void draftMdIsExcluded() {
        TreeNode docs = findByName(builder.build("/"), "docs");
        assertTrue(names(docs.children()).stream().noneMatch(n -> n.startsWith(".")));
    }

    // --- Active-marking tests ---

    @Test
    void activeMarkingForFileNode() {
        List<TreeNode> nodes = builder.build("/docs/intro");
        TreeNode docs = findByName(nodes, "docs");
        TreeNode intro = findByName(docs.children(), "intro");
        assertTrue(intro.active(), "intro should be active");
        assertTrue(docs.active(), "docs should be active (ancestor)");
        assertFalse(findByName(nodes, "index").active(), "index should not be active");
    }

    @Test
    void activeMarkingForIndexNode() {
        List<TreeNode> nodes = builder.build("/index");
        assertTrue(findByName(nodes, "index").active(), "index should be active");
        assertFalse(findByName(nodes, "docs").active(), "docs should not be active");
    }

    @Test
    void activeMarkingForDirectoryNode() {
        List<TreeNode> nodes = builder.build("/docs");
        TreeNode docs = findByName(nodes, "docs");
        assertTrue(docs.active(), "docs should be active");
        assertFalse(findByName(docs.children(), "intro").active(), "intro should not be active");
    }

    @Test
    void nullUrlPathProducesNoActiveNodes() {
        List<TreeNode> nodes = builder.build(null);
        assertFalse(anyActive(nodes));
    }

    // --- Depth limit test ---

    @Test
    void maxDepthLimitsTraversal() throws IOException {
        // Create a chain: deep/a/b/c.md under root
        Path d = root.resolve("deep/a/b");
        Files.createDirectories(d);
        Files.writeString(d.resolve("c.md"), "deep");

        // maxDepth=2: root children (depth 0) and deep's children (depth 1) are listed,
        // but a's children (depth 2) are not — so "b" never appears.
        DirectoryTreeBuilder shallow = new DirectoryTreeBuilder(configFor(root, 2));
        TreeNode deep = findByName(shallow.build("/"), "deep");
        assertNotNull(deep);
        TreeNode a = findByName(deep.children(), "a");
        assertNotNull(a);
        assertTrue(a.children().isEmpty(), "children of 'a' should be empty at max depth");
    }

    // --- Helpers ---

    private List<String> names(List<TreeNode> nodes) {
        return nodes.stream().map(TreeNode::name).toList();
    }

    private TreeNode findByName(List<TreeNode> nodes, String name) {
        return nodes.stream().filter(n -> n.name().equals(name)).findFirst().orElse(null);
    }

    private boolean anyActive(List<TreeNode> nodes) {
        for (TreeNode n : nodes) {
            if (n.active() || anyActive(n.children())) return true;
        }
        return false;
    }

    private MdServeConfig configFor(Path dir) {
        return configFor(dir, 20);
    }

    private MdServeConfig configFor(Path dir, int depth) {
        return new MdServeConfig() {
            @Override public String sourceDir() { return dir.toString(); }
            @Override public int maxTreeDepth() { return depth; }
            @Override public int port() { return 8080; }
        };
    }
}
