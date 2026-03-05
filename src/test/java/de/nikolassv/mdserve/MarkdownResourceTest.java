package de.nikolassv.mdserve;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.smallrye.config.SmallRyeConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

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
                Files.writeString(tempDir.resolve("hello.md"), "# Hello");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return Map.of("md-serve.source-dir", tempDir.toString());
        }
    }

    @Test
    void rootReturnsDirectoryStub() {
        given()
            .when().get("/")
            .then().statusCode(200)
            .body(containsString("directory:"));
    }

    @Test
    void existingFileReturns200() {
        given()
            .when().get("/hello.md")
            .then().statusCode(200)
            .body(containsString("file:"));
    }

    @Test
    void nonExistentReturns404() {
        given()
            .when().get("/nonexistent")
            .then().statusCode(404);
    }

    @Test
    void pathTraversalReturns404() {
        given()
            .when().get("/../escape")
            .then().statusCode(404);
    }
}
