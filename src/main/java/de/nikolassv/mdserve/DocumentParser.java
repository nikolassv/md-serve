package de.nikolassv.mdserve;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@ApplicationScoped
public class DocumentParser {

    @Inject FrontmatterParser frontmatterParser;
    @Inject MarkdownRenderer markdownRenderer;
    @Inject TitleResolver titleResolver;

    public record ParsedDocument(String title, String content, Map<String, Object> frontmatter) {}

    public ParsedDocument parse(Path filePath) throws IOException {
        String raw = Files.readString(filePath, StandardCharsets.UTF_8);
        FrontmatterParser.ParseResult parsed = frontmatterParser.parse(raw);
        String content = markdownRenderer.render(parsed.body());
        String title = titleResolver.resolve(parsed.frontmatter(), content, filePath.getFileName().toString());
        return new ParsedDocument(title, content, parsed.frontmatter());
    }
}
