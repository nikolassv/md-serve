package de.nikolassv.mdserve;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class DirectoryRenderer {

    @Inject DirectoryIndexer directoryIndexer;
    @Inject TemplateRenderer templateRenderer;

    public String render(Path directory, String urlPath) {
        String dirName = directory.getFileName() != null ? directory.getFileName().toString() : "/";
        List<DirectoryIndexer.FileEntry> files = directoryIndexer.list(directory, "/" + urlPath);
        List<String> breadcrumbs = buildBreadcrumbs(urlPath);
        TemplateContext ctx = new TemplateContext(dirName, null, files, breadcrumbs, Collections.emptyMap());
        return templateRenderer.render(ctx);
    }

    private List<String> buildBreadcrumbs(String urlPath) {
        if (urlPath == null || urlPath.isBlank()) return Collections.emptyList();
        List<String> crumbs = new ArrayList<>();
        StringBuilder cumulative = new StringBuilder();
        for (String part : urlPath.split("/")) {
            if (part.isBlank()) continue;
            cumulative.append("/").append(part);
            crumbs.add(cumulative.toString());
        }
        return crumbs;
    }
}
