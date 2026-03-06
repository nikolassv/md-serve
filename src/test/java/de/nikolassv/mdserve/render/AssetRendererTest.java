package de.nikolassv.mdserve.render;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssetRendererTest {

    @TempDir Path tempDir;

    final AssetRenderer renderer = new AssetRenderer();

    @Test
    void cssFileHasCorrectContentType() throws IOException {
        Path css = tempDir.resolve("style.css");
        Files.writeString(css, "body { color: red; }");

        Response response = renderer.render(css);

        assertEquals(200, response.getStatus());
        assertTrue(response.getMediaType().toString().startsWith("text/css"),
                "expected text/css, got: " + response.getMediaType());
    }

    @Test
    void cssBodyContainsFileContents() throws IOException {
        Path css = tempDir.resolve("style.css");
        String content = "body { color: red; }";
        Files.writeString(css, content);

        Response response = renderer.render(css);
        byte[] body = readBody(response);

        assertArrayEquals(content.getBytes(), body);
    }

    @Test
    void binaryFileBodyMatchesBytes() throws IOException {
        Path png = tempDir.resolve("image.png");
        byte[] data = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        Files.write(png, data);

        Response response = renderer.render(png);
        byte[] body = readBody(response);

        assertArrayEquals(data, body);
    }

    @Test
    void unknownExtensionFallsBackToOctetStream() throws IOException {
        Path file = tempDir.resolve("data.xyzunknown");
        Files.writeString(file, "some data");

        Response response = renderer.render(file);

        assertEquals("application/octet-stream", response.getMediaType().toString());
    }

    private byte[] readBody(Response response) throws IOException {
        StreamingOutput so = (StreamingOutput) response.getEntity();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        so.write(out);
        return out.toByteArray();
    }
}
