package de.nikolassv.mdserve;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FrontmatterParserTest {

    FrontmatterParser parser;

    @BeforeEach
    void setUp() {
        parser = new FrontmatterParser();
    }

    @Test
    void validFrontmatterIsExtracted() {
        String input = "---\ntitle: Hello\nauthor: Alice\n---\n# Body";
        FrontmatterParser.ParseResult result = parser.parse(input);

        assertEquals("Hello", result.frontmatter().get("title"));
        assertEquals("Alice", result.frontmatter().get("author"));
        assertEquals("# Body", result.body());
    }

    @Test
    void noFrontmatterReturnsEmptyMapAndFullContent() {
        String input = "# Just a heading\n\nSome text.";
        FrontmatterParser.ParseResult result = parser.parse(input);

        assertTrue(result.frontmatter().isEmpty());
        assertEquals(input, result.body());
    }

    @Test
    void nestedObjectsAndListsAreParsed() {
        String input = "---\ntags:\n  - java\n  - quarkus\nmeta:\n  version: 1\n---\nbody";
        FrontmatterParser.ParseResult result = parser.parse(input);

        assertEquals(List.of("java", "quarkus"), result.frontmatter().get("tags"));
        assertEquals(Map.of("version", 1), result.frontmatter().get("meta"));
        assertEquals("body", result.body());
    }

    @Test
    void malformedYamlReturnsEmptyMapAndFullContent() {
        String input = "---\n: invalid: yaml: [\n---\n# Body";
        FrontmatterParser.ParseResult result = parser.parse(input);

        assertTrue(result.frontmatter().isEmpty());
        assertEquals(input, result.body());
    }

    @Test
    void unclosedFrontmatterTreatedAsNoFrontmatter() {
        String input = "---\ntitle: Oops\n# No closing delimiter";
        FrontmatterParser.ParseResult result = parser.parse(input);

        assertTrue(result.frontmatter().isEmpty());
        assertEquals(input, result.body());
    }
}
