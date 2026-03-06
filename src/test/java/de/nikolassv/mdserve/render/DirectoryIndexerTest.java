package de.nikolassv.mdserve.render;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class DirectoryIndexerTest {

    @TempDir Path tempDir;

    @Inject DirectoryIndexer indexer;

    @Test
    void listsMarkdownFilesAlphabetically() throws IOException {
        Files.writeString(tempDir.resolve("b.md"), "# B");
        Files.writeString(tempDir.resolve("a.md"), "# A");
        List<FileEntry> entries = indexer.list(tempDir, "/");
        assertEquals(List.of("a.md", "b.md"), entries.stream().map(FileEntry::name).toList());
    }

    @Test
    void subdirectoriesAreListed() throws IOException {
        Files.createDirectory(tempDir.resolve("subdir"));
        List<FileEntry> entries = indexer.list(tempDir, "/");
        assertEquals(1, entries.size());
        assertEquals("subdir", entries.get(0).name());
    }

    @Test
    void nonMdFilesAreExcluded() throws IOException {
        Files.writeString(tempDir.resolve("readme.txt"), "ignored");
        Files.writeString(tempDir.resolve("page.md"), "# Page");
        List<FileEntry> entries = indexer.list(tempDir, "/");
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
        List<FileEntry> entries = indexer.list(tempDir, "/docs");
        assertEquals("/docs/file.md", entries.get(0).path());
    }

    @Test
    void dotPrefixedEntriesAreExcluded() throws IOException {
        Files.writeString(tempDir.resolve(".env"), "secret");
        Files.createDirectory(tempDir.resolve(".hidden"));
        Files.writeString(tempDir.resolve("readme.md"), "# Readme");
        Files.createDirectory(tempDir.resolve("subdir"));

        List<FileEntry> entries = indexer.list(tempDir, "/");
        List<String> names = entries.stream().map(FileEntry::name).toList();
        assertFalse(names.contains(".env"));
        assertFalse(names.contains(".hidden"));
        assertTrue(names.contains("readme.md"));
        assertTrue(names.contains("subdir"));
    }
}
