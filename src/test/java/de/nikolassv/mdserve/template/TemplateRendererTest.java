package de.nikolassv.mdserve.template;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class TemplateRendererTest {

    @Inject TemplateRenderer renderer;

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
}
