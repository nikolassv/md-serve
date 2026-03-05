package de.nikolassv.mdserve;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

@QuarkusTest
@TestProfile(MarkdownResourceCustomTemplateTest.Profile.class)
class MarkdownResourceCustomTemplateTest {

    static Path tempDir;
    static Path templateFile;

    public static class Profile implements io.quarkus.test.junit.QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            try {
                tempDir = Files.createTempDirectory("md-serve-custom-tpl-test");
                templateFile = tempDir.resolve("custom.hbs");
                Files.writeString(templateFile, "CUSTOM:{{title}}|{{{content}}}");
                Files.writeString(tempDir.resolve("page.md"), "# My Page\n\nHello.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return Map.of(
                "md-serve.source-dir", tempDir.toString(),
                "md-serve.template", templateFile.toString()
            );
        }
    }

    @Test
    void customTemplateIsUsed() {
        given()
            .when().get("/page")
            .then().statusCode(200)
            .body(containsString("CUSTOM:My Page"))
            .body(not(containsString("<!DOCTYPE html>")));
    }
}
