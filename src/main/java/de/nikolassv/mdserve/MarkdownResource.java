package de.nikolassv.mdserve;

import de.nikolassv.mdserve.render.DirectoryRenderer;
import de.nikolassv.mdserve.render.FileRenderer;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;

@Path("/")
public class MarkdownResource {

    @Inject PathResolver pathResolver;
    @Inject FileRenderer fileRenderer;
    @Inject DirectoryRenderer directoryRenderer;

    @GET
    @Path("{path:.*}")
    @Produces(MediaType.TEXT_HTML)
    public Response serve(@PathParam("path") String path) {
        PathResolver.Result result = pathResolver.resolve(path);
        return switch (result.kind()) {
            case FILE -> {
                try {
                    yield Response.ok(fileRenderer.render(result.path(), path)).build();
                } catch (IOException e) {
                    yield Response.serverError().entity("Failed to read file").build();
                }
            }
            case DIRECTORY -> Response.ok(directoryRenderer.render(result.path(), path)).build();
            case NOT_FOUND -> Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.TEXT_PLAIN)
                    .entity("Not found: " + path)
                    .build();
        };
    }
}
