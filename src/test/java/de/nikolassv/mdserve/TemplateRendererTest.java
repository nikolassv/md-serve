package de.nikolassv.mdserve;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TemplateRendererTest {

    TemplateRenderer renderer;

    @BeforeEach
    void setUp() {
        MdServeConfig config = new MdServeConfig() {
            public String sourceDir() { return "./docs"; }
            public Optional<String> template() { return Optional.empty(); }
        };
        renderer = new TemplateRenderer(new TemplateLoader(config));
    }

    @Test
    void rendersDefaultTemplateWithTitleAndContent() {
        TemplateContext ctx = new TemplateContext(
                "My Title",
                "<p>Hello</p>",
                List.of(),
                List.of(),
                Collections.emptyMap()
        );
        String html = renderer.render(ctx);

        assertTrue(html.contains("<title>My Title</title>"), "expected title tag");
        assertTrue(html.contains("<p>Hello</p>"), "expected content");
        assertTrue(html.contains("<!DOCTYPE html>"), "expected HTML5 doctype");
    }

    @Test
    void missingCustomTemplateThrowsOnStartup() {
        MdServeConfig config = new MdServeConfig() {
            public String sourceDir() { return "./docs"; }
            public Optional<String> template() { return Optional.of("/nonexistent/template.hbs"); }
        };
        assertThrows(IllegalStateException.class, () -> new TemplateLoader(config));
    }
}
