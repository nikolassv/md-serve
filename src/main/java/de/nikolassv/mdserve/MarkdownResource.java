package de.nikolassv.mdserve;

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

@Path("/")
public class MarkdownResource {

    private static final Logger LOG = Logger.getLogger(MarkdownResource.class);

    @Inject PathResolver pathResolver;
    @Inject FileRenderer fileRenderer;
    @Inject DirectoryRenderer directoryRenderer;
    @Inject ErrorRenderer errorRenderer;

    @GET
    @Path("{path:.*}")
    @Produces(MediaType.TEXT_HTML)
    public Response serve(@PathParam("path") String path) {
        PathResolver.Result result = pathResolver.resolve(path);
        try {
            return switch (result.kind()) {
                case FILE -> {
                    try {
                        yield Response.ok(fileRenderer.render(result.path(), path)).build();
                    } catch (IOException e) {
                        LOG.error("Failed to read file: " + result.path(), e);
                        yield errorRenderer.renderServerError(path);
                    }
                }
                case DIRECTORY -> Response.ok(directoryRenderer.render(result.path(), path)).build();
                case NOT_FOUND -> errorRenderer.renderNotFound(path);
            };
        } catch (Exception e) {
            LOG.error("Unexpected error serving: " + path, e);
            return errorRenderer.renderServerError(path);
        }
    }
}
