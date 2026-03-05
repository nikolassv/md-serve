package de.nikolassv.mdserve;

import de.nikolassv.mdserve.render.FileRenderer;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
@TestProfile(MarkdownResourceErrorTest.Profile.class)
class MarkdownResourceErrorTest {

    static Path tempDir;

    public static class Profile implements io.quarkus.test.junit.QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            try {
                tempDir = Files.createTempDirectory("md-serve-error-test");
                Files.writeString(tempDir.resolve("readable.md"), "# Hello\n\nContent.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return Map.of("md-serve.source-dir", tempDir.toString());
        }
    }

    @InjectMock
    FileRenderer fileRenderer;

    @Test
    void fileReadErrorReturns500() throws IOException {
        Mockito.when(fileRenderer.render(Mockito.any(), Mockito.any()))
                .thenThrow(new IOException("Simulated read failure"));

        given()
            .when().get("/readable.md")
            .then().statusCode(500)
            .contentType(containsString("text/html"))
            .body(containsString("<!DOCTYPE html>"));
    }
}
