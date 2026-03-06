package de.nikolassv.mdserve.template;

import com.github.jknack.handlebars.Context;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;

@ApplicationScoped
public class TemplateRenderer {

    private final TemplateRegistry registry;

    @Inject
    public TemplateRenderer(TemplateRegistry registry) {
        this.registry = registry;
    }

    public String render(TemplateContext ctx, String templateName) {
        Context context = Context.newBuilder(ctx)
                .combine("title", ctx.title())
                .combine("content", ctx.content())
                .combine("files", ctx.files())
                .combine("breadcrumbs", ctx.breadcrumbs())
                .combine("frontmatter", ctx.frontmatter())
                .combine("tree", ctx.tree())
                .build();
        try {
            return registry.get(templateName).apply(context);
        } catch (IOException e) {
            throw new IllegalStateException("Template rendering failed", e);
        }
    }
}
