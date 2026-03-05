package de.nikolassv.mdserve;

import com.github.jknack.handlebars.Context;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;

@ApplicationScoped
public class TemplateRenderer {

    private final TemplateLoader templateLoader;

    @Inject
    public TemplateRenderer(TemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    public String render(TemplateContext ctx) {
        Context context = Context.newBuilder(ctx)
                .combine("title", ctx.title())
                .combine("content", ctx.content())
                .combine("files", ctx.files())
                .combine("breadcrumbs", ctx.breadcrumbs())
                .combine("frontmatter", ctx.frontmatter())
                .build();
        try {
            return templateLoader.get().apply(context);
        } catch (IOException e) {
            throw new IllegalStateException("Template rendering failed", e);
        }
    }
}
