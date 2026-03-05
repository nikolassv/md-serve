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

@QuarkusTest
@TestProfile(MarkdownResourceTest.Profile.class)
class MarkdownResourceTest {

    static Path tempDir;

    public static class Profile implements io.quarkus.test.junit.QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            try {
                tempDir = Files.createTempDirectory("md-serve-test");
                Files.createDirectory(tempDir.resolve("subdir"));
                Files.writeString(tempDir.resolve("hello.md"), "# Hello\n\nSome content.");
                Files.writeString(tempDir.resolve("fm.md"),
                        "---\ntitle: Front Matter Title\n---\n# H1 Title\n\nBody text.");
                Files.writeString(tempDir.resolve("h1only.md"), "# H1 Title\n\nNo front matter.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return Map.of("md-serve.source-dir", tempDir.toString());
        }
    }

    @Test
    void rootReturnsDirectoryPage() {
        given()
            .when().get("/")
            .then().statusCode(200)
            .body(containsString("<!DOCTYPE html>"));
    }

    @Test
    void fileRendersFullHtmlPage() {
        given()
            .when().get("/hello.md")
            .then().statusCode(200)
            .body(containsString("<!DOCTYPE html>"))
            .body(containsString("<h1>Hello</h1>"))
            .body(containsString("Some content."));
    }

    @Test
    void titleFromFrontmatterTakesPriority() {
        given()
            .when().get("/fm.md")
            .then().statusCode(200)
            .body(containsString("<title>Front Matter Title</title>"));
    }

    @Test
    void titleFromH1WhenNoFrontmatter() {
        given()
            .when().get("/h1only.md")
            .then().statusCode(200)
            .body(containsString("<title>H1 Title</title>"));
    }

    @Test
    void titleFallsBackToFilename() {
        given()
            .when().get("/hello")
            .then().statusCode(200)
            .body(containsString("<title>Hello</title>"));
    }

    @Test
    void nonExistentReturns404() {
        given()
            .when().get("/nonexistent")
            .then().statusCode(404)
            .contentType(containsString("text/html"))
            .body(containsString("<!DOCTYPE html>"))
            .body(containsString("Not Found"));
    }

    @Test
    void pathTraversalReturns404() {
        given()
            .when().get("/../escape")
            .then().statusCode(404)
            .contentType(containsString("text/html"));
    }

    @Test
    void subdirListingReturns200WithHtml() {
        given()
            .when().get("/subdir")
            .then().statusCode(200)
            .body(containsString("<!DOCTYPE html>"));
    }

}
