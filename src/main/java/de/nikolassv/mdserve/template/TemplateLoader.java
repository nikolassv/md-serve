package de.nikolassv.mdserve.template;

import de.nikolassv.mdserve.MdServeConfig;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ApplicationScoped
public class TemplateLoader {

    private final Template template;

    @Inject
    public TemplateLoader(MdServeConfig config) {
        this.template = config.template()
                .map(this::loadFromFileSystem)
                .orElseGet(this::loadDefault);
    }

    public Template get() {
        return template;
    }

    private Template loadFromFileSystem(String templatePath) {
        Path path = Paths.get(templatePath);
        if (!Files.isRegularFile(path)) {
            throw new IllegalStateException(
                    "Custom template not found: " + path.toAbsolutePath());
        }
        try {
            String source = Files.readString(path, StandardCharsets.UTF_8);
            return handlebars().compileInline(source);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load template: " + templatePath, e);
        }
    }

    private Template loadDefault() {
        try (Reader reader = new InputStreamReader(
                getClass().getResourceAsStream("/templates/default.hbs"),
                StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[4096];
            int n;
            while ((n = reader.read(buf)) != -1) sb.append(buf, 0, n);
            return handlebars().compileInline(sb.toString());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load built-in default template", e);
        }
    }

    private Handlebars handlebars() {
        Handlebars hbs = new Handlebars();
        hbs.registerHelper("treeNav", new TreeNavHelper());
        return hbs;
    }
}
