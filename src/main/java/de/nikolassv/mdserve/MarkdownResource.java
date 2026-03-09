package de.nikolassv.mdserve;

import de.nikolassv.mdserve.render.AssetRenderer;
import de.nikolassv.mdserve.render.DirectoryRenderer;
import de.nikolassv.mdserve.render.ErrorRenderer;
import de.nikolassv.mdserve.render.FileRenderer;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

@Path("/")
public class MarkdownResource {

    private static final Logger LOG = Logger.getLogger(MarkdownResource.class);

    @Inject PathResolver pathResolver;
    @Inject FileRenderer fileRenderer;
    @Inject AssetRenderer assetRenderer;
    @Inject DirectoryRenderer directoryRenderer;
    @Inject ErrorRenderer errorRenderer;

    @GET
    @Path("{path:.*}")
    @Produces(MediaType.WILDCARD)
    public Response serve(@PathParam("path") String path) {
        PathResolver.Result result = pathResolver.resolve(path);
        try {
            return switch (result.kind()) {
                case FILE -> {
                    if (isMarkdown(result.path())) {
                        try {
                            yield Response.ok(fileRenderer.render(result.path(), path))
                                    .type(MediaType.TEXT_HTML).build();
                        } catch (IOException e) {
                            LOG.error("Failed to read file: " + result.path(), e);
                            yield errorRenderer.renderServerError(path);
                        }
                    } else {
                        yield assetRenderer.render(result.path());
                    }
                }
                case DIRECTORY -> {
                    java.nio.file.Path indexFile = result.path().resolve("index.md");
                    if (Files.isRegularFile(indexFile)) {
                        String base = path.isEmpty() ? "" : "/" + path.replaceAll("/+$", "");
                        yield Response.status(301)
                                .location(URI.create(base + "/index.md"))
                                .build();
                    }
                    yield Response.ok(directoryRenderer.render(result.path(), path))
                            .type(MediaType.TEXT_HTML).build();
                }
                case NOT_FOUND -> errorRenderer.renderNotFound(path);
            };
        } catch (Exception e) {
            LOG.error("Unexpected error serving: " + path, e);
            return errorRenderer.renderServerError(path);
        }
    }

    private static boolean isMarkdown(java.nio.file.Path path) {
        return path.getFileName().toString().toLowerCase().endsWith(".md");
    }
}
