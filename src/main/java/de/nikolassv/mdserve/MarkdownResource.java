package de.nikolassv.mdserve;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class MarkdownResource {

    @Inject
    PathResolver pathResolver;

    @GET
    @Path("{path:.*}")
    @Produces(MediaType.TEXT_HTML)
    public Response serve(@PathParam("path") String path) {
        PathResolver.Result result = pathResolver.resolve(path);
        return switch (result.kind()) {
            case FILE -> Response.ok("<p>file: " + result.path() + "</p>").build();
            case DIRECTORY -> Response.ok("<p>directory: " + result.path() + "</p>").build();
            case NOT_FOUND -> Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.TEXT_PLAIN)
                    .entity("Not found: " + path)
                    .build();
        };
    }
}
