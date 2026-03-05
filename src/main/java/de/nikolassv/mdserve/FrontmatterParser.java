package de.nikolassv.mdserve;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class FrontmatterParser {

    private static final Logger LOG = Logger.getLogger(FrontmatterParser.class);
    private static final Pattern FRONTMATTER_PATTERN =
            Pattern.compile("^---\\r?\\n(.*?)\\r?\\n---\\r?\\n?(.*)", Pattern.DOTALL);

    public record ParseResult(Map<String, Object> frontmatter, String body) {}

    public ParseResult parse(String rawContent) {
        if (rawContent == null) {
            return new ParseResult(Collections.emptyMap(), "");
        }

        Matcher matcher = FRONTMATTER_PATTERN.matcher(rawContent);
        if (!matcher.matches()) {
            return new ParseResult(Collections.emptyMap(), rawContent);
        }

        String yamlBlock = matcher.group(1);
        String body = matcher.group(2);

        try {
            Object parsed = new Yaml().load(yamlBlock);
            if (parsed instanceof Map<?, ?> map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> frontmatter = (Map<String, Object>) map;
                return new ParseResult(frontmatter, body);
            }
        } catch (YAMLException e) {
            LOG.warnf("Malformed YAML front matter, treating as absent: %s", e.getMessage());
        }

        return new ParseResult(Collections.emptyMap(), rawContent);
    }
}
