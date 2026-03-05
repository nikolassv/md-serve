package de.nikolassv.mdserve.render;

import de.nikolassv.mdserve.MdServeConfig;
import de.nikolassv.mdserve.markdown.DocumentParser;
import de.nikolassv.mdserve.markdown.FrontmatterParser;
import de.nikolassv.mdserve.markdown.MarkdownRenderer;
import de.nikolassv.mdserve.markdown.TitleResolver;
import de.nikolassv.mdserve.template.TemplateLoader;
import de.nikolassv.mdserve.template.TemplateRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FileRendererTest {

    @TempDir Path tempDir;

    FileRenderer renderer;

    @BeforeEach
    void setUp() {
        MdServeConfig config = new MdServeConfig() {
            public String sourceDir() { return tempDir.toString(); }
            public Optional<String> template() { return Optional.empty(); }
        };
        TemplateRenderer templateRenderer = new TemplateRenderer(new TemplateLoader(config));
        DocumentParser documentParser = new DocumentParser(new FrontmatterParser(), new MarkdownRenderer(), new TitleResolver());
        renderer = new FileRenderer(documentParser, templateRenderer);
    }

    @Test
    void titleFromFrontmatterTakesPriority() throws IOException {
        Path file = tempDir.resolve("page.md");
        Files.writeString(file, "---\ntitle: FM Title\n---\n# H1 Title\n\nBody.");
        String html = renderer.render(file, "/page.md");
        assertTrue(html.contains("<title>FM Title</title>"), "expected frontmatter title");
    }

    @Test
    void titleFromH1WhenNoFrontmatter() throws IOException {
        Path file = tempDir.resolve("page.md");
        Files.writeString(file, "# H1 Title\n\nBody.");
        String html = renderer.render(file, "/page.md");
        assertTrue(html.contains("<title>H1 Title</title>"), "expected H1 title");
    }

    @Test
    void titleFallsBackToFilename() throws IOException {
        Path file = tempDir.resolve("my-page.md");
        Files.writeString(file, "No heading here.");
        String html = renderer.render(file, "/my-page.md");
        assertTrue(html.contains("<title>my-page</title>"), "expected filename title");
    }

    @Test
    void markdownIsRenderedToHtml() throws IOException {
        Path file = tempDir.resolve("content.md");
        Files.writeString(file, "# Hello\n\n**Bold** text.");
        String html = renderer.render(file, "/content.md");
        assertTrue(html.contains("<h1>Hello</h1>"));
        assertTrue(html.contains("<strong>Bold</strong>"));
    }
}
