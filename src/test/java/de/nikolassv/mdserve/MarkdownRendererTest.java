package de.nikolassv.mdserve;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkdownRendererTest {

    MarkdownRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new MarkdownRenderer();
    }

    @Test
    void emptyInputReturnsEmptyString() {
        assertEquals("", renderer.render(""));
    }

    @Test
    void paragraphIsWrappedInPTag() {
        String html = renderer.render("Hello world");
        assertTrue(html.contains("<p>Hello world</p>"), "expected <p> tag, got: " + html);
    }

    @Test
    void headingIsRendered() {
        String html = renderer.render("# Title");
        assertTrue(html.contains("<h1>Title</h1>"), "expected <h1>, got: " + html);
    }

    @Test
    void boldAndItalicAreRendered() {
        String html = renderer.render("**bold** and *italic*");
        assertTrue(html.contains("<strong>bold</strong>"), "expected <strong>, got: " + html);
        assertTrue(html.contains("<em>italic</em>"), "expected <em>, got: " + html);
    }

    @Test
    void fencedCodeBlockIsRenderedAsPreCode() {
        String html = renderer.render("```\ncode here\n```");
        assertTrue(html.contains("<pre>"), "expected <pre>, got: " + html);
        assertTrue(html.contains("<code>"), "expected <code>, got: " + html);
    }

    @Test
    void tableIsRenderedAsTableElement() {
        String markdown = "| A | B |\n|---|---|\n| 1 | 2 |";
        String html = renderer.render(markdown);
        assertTrue(html.contains("<table>"), "expected <table>, got: " + html);
    }

    @Test
    void plainTextIsWrappedInParagraph() {
        String html = renderer.render("just plain text");
        assertTrue(html.contains("<p>just plain text</p>"), "expected plain text in <p>, got: " + html);
    }
}
