package de.nikolassv.mdserve.template;

import de.nikolassv.mdserve.MdServeConfig;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class TemplateRegistry {

    private static final Logger LOG = Logger.getLogger(TemplateRegistry.class);

    private final Map<String, Template> templates = new HashMap<>();

    @Inject
    public TemplateRegistry(MdServeConfig config) {
        Path templateDir = Paths.get(config.sourceDir()).resolve(".md-serve/templates");
        Handlebars handlebars = handlebars();

        for (TemplateRole role : TemplateRole.values()) {
            templates.put(role.id(), loadRole(handlebars, templateDir, role.id()));
        }

        if (Files.isDirectory(templateDir)) {
            try (var stream = Files.list(templateDir)) {
                stream.filter(p -> p.getFileName().toString().endsWith(".hbs"))
                      .forEach(p -> {
                          String name = p.getFileName().toString();
                          name = name.substring(0, name.length() - 4);
                          if (!templates.containsKey(name)) {
                              try {
                                  String source = Files.readString(p, StandardCharsets.UTF_8);
                                  templates.put(name, handlebars.compileInline(source));
                              } catch (IOException e) {
                                  LOG.warnf("Skipping template '%s': %s", p, e.getMessage());
                              }
                          }
                      });
            } catch (IOException e) {
                throw new IllegalStateException("Failed to scan template directory: " + templateDir, e);
            }
        }
    }

    public Template get(String name) {
        return templates.getOrDefault(name, templates.get(TemplateRole.DEFAULT.id()));
    }

    private Template loadRole(Handlebars hbs, Path templateDir, String role) {
        Path userFile = templateDir.resolve(role + ".hbs");
        if (Files.isRegularFile(userFile)) {
            try {
                String source = Files.readString(userFile, StandardCharsets.UTF_8);
                return hbs.compileInline(source);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to load template: " + userFile, e);
            }
        }
        return loadClasspath(hbs, role);
    }

    private Template loadClasspath(Handlebars hbs, String role) {
        String resource = "/templates/" + role + ".hbs";
        try (Reader reader = new InputStreamReader(
                getClass().getResourceAsStream(resource), StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[4096];
            int n;
            while ((n = reader.read(buf)) != -1) sb.append(buf, 0, n);
            return hbs.compileInline(sb.toString());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load built-in template for role: " + role, e);
        }
    }

    private Handlebars handlebars() {
        Handlebars hbs = new Handlebars();
        hbs.registerHelper("treeNav", new TreeNavHelper());
        return hbs;
    }
}
