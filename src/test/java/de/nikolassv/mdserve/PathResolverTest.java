package de.nikolassv.mdserve;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PathResolverTest {

    @TempDir
    Path tempDir;

    PathResolver resolver;

    @BeforeEach
    void setUp() {
        MdServeConfig config = new MdServeConfig() {
            public String sourceDir() { return tempDir.toString(); }
            public Optional<String> template() { return Optional.empty(); }
            public int maxTreeDepth() { return 20; }
            public int port() { return 8080; }
        };
        resolver = new PathResolver(config);
    }

    @Test
    void normalFileResolvesCorrectly() throws IOException {
        Path file = tempDir.resolve("hello.md");
        Files.writeString(file, "# Hello");

        PathResolver.Result result = resolver.resolve("/hello.md");
        assertEquals(PathResolver.Kind.FILE, result.kind());
        assertEquals(file, result.path());
    }

    @Test
    void pathWithoutExtensionResolvesToMdFile() throws IOException {
        Path file = tempDir.resolve("hello.md");
        Files.writeString(file, "# Hello");

        PathResolver.Result result = resolver.resolve("/hello");
        assertEquals(PathResolver.Kind.FILE, result.kind());
        assertEquals(file, result.path());
    }

    @Test
    void directoryIsIdentifiedAsDirectory() throws IOException {
        Path dir = tempDir.resolve("subdir");
        Files.createDirectory(dir);

        PathResolver.Result result = resolver.resolve("/subdir");
        assertEquals(PathResolver.Kind.DIRECTORY, result.kind());
        assertEquals(dir, result.path());
    }

    @Test
    void pathTraversalIsRejected() {
        PathResolver.Result result = resolver.resolve("/../etc/passwd");
        assertEquals(PathResolver.Kind.NOT_FOUND, result.kind());
    }

    @Test
    void pathOutsideSourceDirIsRejected() {
        PathResolver.Result result = resolver.resolve("/../../secret");
        assertEquals(PathResolver.Kind.NOT_FOUND, result.kind());
    }

    @Test
    void nonExistentPathReturnsNotFound() {
        PathResolver.Result result = resolver.resolve("/does-not-exist");
        assertEquals(PathResolver.Kind.NOT_FOUND, result.kind());
    }

    @Test
    void dotPrefixedFileIsRejected() {
        PathResolver.Result result = resolver.resolve("/.env");
        assertEquals(PathResolver.Kind.NOT_FOUND, result.kind());
    }

    @Test
    void dotPrefixedDirectorySegmentIsRejected() throws IOException {
        Path hidden = tempDir.resolve(".git").resolve("config");
        Files.createDirectories(hidden.getParent());
        Files.writeString(hidden, "secret");

        PathResolver.Result result = resolver.resolve("/.git/config");
        assertEquals(PathResolver.Kind.NOT_FOUND, result.kind());
    }

    @Test
    void dotSegmentAtDepthIsRejected() throws IOException {
        Path hidden = tempDir.resolve("docs").resolve(".hidden").resolve("page.md");
        Files.createDirectories(hidden.getParent());
        Files.writeString(hidden, "# Hidden");

        PathResolver.Result result = resolver.resolve("/docs/.hidden/page.md");
        assertEquals(PathResolver.Kind.NOT_FOUND, result.kind());
    }
}
