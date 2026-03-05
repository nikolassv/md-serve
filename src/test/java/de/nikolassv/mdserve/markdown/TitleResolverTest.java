package de.nikolassv.mdserve.markdown;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TitleResolverTest {

    TitleResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new TitleResolver();
    }

    @Test
    void frontmatterTitleTakesPriority() {
        String result = resolver.resolve(Map.of("title", "FM Title"), "<h1>H1 Title</h1>", "file.md");
        assertEquals("FM Title", result);
    }

    @Test
    void h1UsedWhenNoFrontmatterTitle() {
        String result = resolver.resolve(Map.of(), "<h1>H1 Title</h1>", "file.md");
        assertEquals("H1 Title", result);
    }

    @Test
    void filenameFallbackWhenNoH1() {
        String result = resolver.resolve(Map.of(), "<p>no heading</p>", "my-page.md");
        assertEquals("my-page", result);
    }

    @Test
    void filenameFallbackWithoutMdExtension() {
        String result = resolver.resolve(Map.of(), "", "about.md");
        assertEquals("about", result);
    }

    @Test
    void filenameFallbackNoExtension() {
        String result = resolver.resolve(Map.of(), "", "readme");
        assertEquals("readme", result);
    }
}
