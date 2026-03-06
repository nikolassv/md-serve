package de.nikolassv.mdserve.render;

import de.nikolassv.mdserve.template.Breadcrumb;
import de.nikolassv.mdserve.template.TemplateContext;
import de.nikolassv.mdserve.template.TemplateRenderer;
import de.nikolassv.mdserve.template.TemplateRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.Collections;

@ApplicationScoped
public class ErrorRenderer {

    private static final Logger LOG = Logger.getLogger(ErrorRenderer.class);

    @Inject TemplateRenderer templateRenderer;
    @Inject DirectoryTreeBuilder treeBuilder;

    public Response renderNotFound(String urlPath) {
        return renderErrorPage(urlPath, Response.Status.NOT_FOUND,
                "Not Found", "<p>The requested page could not be found.</p>");
    }

    public Response renderServerError(String urlPath) {
        return renderErrorPage(urlPath, Response.Status.INTERNAL_SERVER_ERROR,
                "Error", "<p>An unexpected error occurred.</p>");
    }

    private Response renderErrorPage(String urlPath, Response.Status status, String title, String content) {
        try {
            TemplateContext ctx = new TemplateContext(title, content, null,
                    Breadcrumb.listFor(urlPath), Collections.emptyMap(), treeBuilder.build(null));
            String html = templateRenderer.render(ctx, TemplateRole.ERROR.id());
            return Response.status(status).type(MediaType.TEXT_HTML).entity(html).build();
        } catch (Exception e) {
            LOG.error("Template rendering failed for error page", e);
            return Response.status(status).type(MediaType.TEXT_PLAIN).entity(title).build();
        }
    }
}
