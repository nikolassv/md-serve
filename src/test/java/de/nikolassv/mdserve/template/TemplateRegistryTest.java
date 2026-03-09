package de.nikolassv.mdserve.template;

import de.nikolassv.mdserve.MdServeConfig;
import com.github.jknack.handlebars.Context;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class TemplateRegistryTest {

    @TempDir Path tempDir;

    private TemplateRegistry registry() {
        return registry(tempDir.toString());
    }

    private TemplateRegistry registry(String sourceDir) {
        MdServeConfig config = new MdServeConfig() {
            public String sourceDir() { return sourceDir; }
            public int maxTreeDepth() { return 20; }
            public int port() { return 8080; }
        };
        return new TemplateRegistry(config);
    }

    @Test
    void noTemplateDirLoadsAllRolesFromClasspath() {
        TemplateRegistry reg = registry();
        assertNotNull(reg.get("default"), "default role should load from classpath");
        assertNotNull(reg.get("directory"), "directory role should load from classpath");
        assertNotNull(reg.get("error"), "error role should load from classpath");
    }

    @Test
    void userDefaultHbsOverridesDefaultRole() throws IOException {
        Path tplDir = Files.createDirectories(tempDir.resolve(".md-serve/templates"));
        Files.writeString(tplDir.resolve("default.hbs"), "USER-DEFAULT:{{title}}");

        TemplateRegistry reg = registry();
        Context ctx = Context.newBuilder(null).combine("title", "T").build();
        String result = reg.get("default").apply(ctx);
        assertTrue(result.contains("USER-DEFAULT:T"), "user template should override bundled default");
    }

    @Test
    void otherRolesStillUseBundledWhenOnlyDefaultOverridden() throws IOException {
        Path tplDir = Files.createDirectories(tempDir.resolve(".md-serve/templates"));
        Files.writeString(tplDir.resolve("default.hbs"), "OVERRIDDEN");

        TemplateRegistry reg = registry();
        Context ctx = Context.newBuilder(null).combine("title", "T").build();
        String dirResult = reg.get("directory").apply(ctx);
        assertTrue(dirResult.contains("<!DOCTYPE html>"), "directory role should still use bundled template");
    }

    @Test
    void extraCustomHbsIsAccessibleByName() throws IOException {
        Path tplDir = Files.createDirectories(tempDir.resolve(".md-serve/templates"));
        Files.writeString(tplDir.resolve("custom.hbs"), "MY-CUSTOM");

        TemplateRegistry reg = registry();
        assertNotNull(reg.get("custom"), "extra template should be accessible by name");
        Context ctx = Context.newBuilder(null).build();
        assertEquals("MY-CUSTOM", reg.get("custom").apply(ctx));
    }

    @Test
    void unknownNameFallsBackToDefault() {
        TemplateRegistry reg = registry();
        assertSame(reg.get("default"), reg.get("nonexistent"),
                "unknown name should return the default template");
    }
}
