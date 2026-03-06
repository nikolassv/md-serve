package de.nikolassv.mdserve.render;

import de.nikolassv.mdserve.template.Breadcrumb;
import de.nikolassv.mdserve.template.TemplateContext;
import de.nikolassv.mdserve.template.TemplateRenderer;
import de.nikolassv.mdserve.template.TemplateRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class DirectoryRenderer {

    @Inject DirectoryIndexer directoryIndexer;
    @Inject TemplateRenderer templateRenderer;
    @Inject DirectoryTreeBuilder treeBuilder;

    public String render(Path directory, String urlPath) {
        String dirName = directory.getFileName() != null ? directory.getFileName().toString() : "/";
        List<FileEntry> files = directoryIndexer.list(directory, "/" + urlPath);
        TemplateContext ctx = new TemplateContext(dirName, null, files,
                Breadcrumb.listFor(urlPath), Collections.emptyMap(), treeBuilder.build("/" + urlPath));
        return templateRenderer.render(ctx, TemplateRole.DIRECTORY.id());
    }
}
