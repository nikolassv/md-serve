package de.nikolassv.mdserve;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ApplicationScoped
public class PathResolver {

    public enum Kind { FILE, DIRECTORY, NOT_FOUND }

    public record Result(Kind kind, Path path) {}

    private final Path sourceDir;

    @Inject
    public PathResolver(MdServeConfig config) {
        this.sourceDir = Paths.get(config.sourceDir()).toAbsolutePath().normalize();
    }

    public Result resolve(String urlPath) {
        // Normalize and strip leading slash to make it relative
        String stripped = urlPath == null ? "" : urlPath.replaceAll("^/+", "");

        // Security: reject any path segment that starts with a dot
        for (String segment : stripped.split("/")) {
            if (segment.startsWith(".")) {
                return new Result(Kind.NOT_FOUND, sourceDir.resolve(stripped));
            }
        }

        Path candidate = sourceDir.resolve(stripped).normalize();

        // Security: reject traversal outside source-dir
        if (!candidate.startsWith(sourceDir)) {
            return new Result(Kind.NOT_FOUND, candidate);
        }

        if (Files.isDirectory(candidate)) {
            return new Result(Kind.DIRECTORY, candidate);
        }

        if (Files.isRegularFile(candidate)) {
            return new Result(Kind.FILE, candidate);
        }

        // Try appending .md
        Path withMd = candidate.resolveSibling(candidate.getFileName() + ".md");
        if (Files.isRegularFile(withMd)) {
            return new Result(Kind.FILE, withMd);
        }

        return new Result(Kind.NOT_FOUND, candidate);
    }
}
