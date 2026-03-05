package de.nikolassv.mdserve;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryIndexerTest {

    @TempDir Path tempDir;

    DirectoryIndexer indexer;

    @BeforeEach
    void setUp() {
        indexer = new DirectoryIndexer();
    }

    @Test
    void listsMarkdownFilesAlphabetically() throws IOException {
        Files.writeString(tempDir.resolve("b.md"), "# B");
        Files.writeString(tempDir.resolve("a.md"), "# A");
        List<DirectoryIndexer.FileEntry> entries = indexer.list(tempDir, "/");
        assertEquals(List.of("a.md", "b.md"), entries.stream().map(DirectoryIndexer.FileEntry::name).toList());
    }

    @Test
    void subdirectoriesAreListed() throws IOException {
        Files.createDirectory(tempDir.resolve("subdir"));
        List<DirectoryIndexer.FileEntry> entries = indexer.list(tempDir, "/");
        assertEquals(1, entries.size());
        assertEquals("subdir", entries.get(0).name());
    }

    @Test
    void nonMdFilesAreExcluded() throws IOException {
        Files.writeString(tempDir.resolve("readme.txt"), "ignored");
        Files.writeString(tempDir.resolve("page.md"), "# Page");
        List<DirectoryIndexer.FileEntry> entries = indexer.list(tempDir, "/");
        assertEquals(1, entries.size());
        assertEquals("page.md", entries.get(0).name());
    }

    @Test
    void emptyDirectoryReturnsEmptyList() {
        assertTrue(indexer.list(tempDir, "/").isEmpty());
    }

    @Test
    void pathsAreCorrectlyBuilt() throws IOException {
        Files.writeString(tempDir.resolve("file.md"), "# F");
        List<DirectoryIndexer.FileEntry> entries = indexer.list(tempDir, "/docs");
        assertEquals("/docs/file.md", entries.get(0).path());
    }
}
