package de.nikolassv.mdserve.render;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class AssetRenderer {

    public Response render(Path filePath) {
        String contentType;
        try {
            contentType = Files.probeContentType(filePath);
        } catch (Exception e) {
            contentType = null;
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        StreamingOutput stream = out -> {
            try (var in = Files.newInputStream(filePath)) {
                in.transferTo(out);
            }
        };

        return Response.ok(stream).type(contentType).build();
    }
}
