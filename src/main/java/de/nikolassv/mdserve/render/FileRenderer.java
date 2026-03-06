package de.nikolassv.mdserve.render;

import de.nikolassv.mdserve.markdown.DocumentParser;
import de.nikolassv.mdserve.template.Breadcrumb;
import de.nikolassv.mdserve.template.TemplateContext;
import de.nikolassv.mdserve.template.TemplateRenderer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.nio.file.Path;

@ApplicationScoped
public class FileRenderer {

    private final DocumentParser documentParser;
    private final TemplateRenderer templateRenderer;
    private final DirectoryTreeBuilder treeBuilder;

    @Inject
    public FileRenderer(DocumentParser documentParser, TemplateRenderer templateRenderer,
                        DirectoryTreeBuilder treeBuilder) {
        this.documentParser = documentParser;
        this.templateRenderer = templateRenderer;
        this.treeBuilder = treeBuilder;
    }

    public String render(Path filePath, String urlPath) throws IOException {
        DocumentParser.ParsedDocument doc = documentParser.parse(filePath);
        TemplateContext ctx = new TemplateContext(doc.title(), doc.content(), null,
                Breadcrumb.listFor(urlPath), doc.frontmatter(), treeBuilder.build("/" + urlPath));
        return templateRenderer.render(ctx);
    }
}
