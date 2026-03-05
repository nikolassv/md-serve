package de.nikolassv.mdserve;

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

    @GET
    @Path("{path:.*}")
    @Produces(MediaType.TEXT_HTML)
    public Response serve(@PathParam("path") String path) {
        PathResolver.Result result = pathResolver.resolve(path);
        return switch (result.kind()) {
            case FILE -> serveFile(path, result);
            case DIRECTORY -> Response.ok("<p>directory: " + result.path() + "</p>").build();
            case NOT_FOUND -> Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.TEXT_PLAIN)
                    .entity("Not found: " + path)
                    .build();
        };
    }

    private Response serveFile(String urlPath, PathResolver.Result result) {
        try {
            return Response.ok(fileRenderer.render(result.path(), urlPath)).build();
        } catch (IOException e) {
            return Response.serverError().entity("Failed to read file").build();
        }
    }
}
