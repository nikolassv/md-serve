package de.nikolassv.mdserve.render;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ApplicationScoped
public class DirectoryIndexer {

    private static final Logger LOG = Logger.getLogger(DirectoryIndexer.class);

    public List<FileEntry> list(Path directory, String urlBase) {
        List<FileEntry> entries = new ArrayList<>();
        try (var stream = Files.list(directory)) {
            stream.sorted(Comparator.comparing(p -> p.getFileName().toString()))
                  .forEach(p -> {
                      String name = p.getFileName().toString();
                      String entryPath = urlBase.endsWith("/")
                              ? urlBase + name
                              : urlBase + "/" + name;
                      if (Files.isDirectory(p) || name.endsWith(".md")) {
                          entries.add(new FileEntry(name, entryPath));
                      }
                  });
        } catch (IOException e) {
            LOG.warnf("Failed to list directory %s: %s", directory, e.getMessage());
        }
        return entries;
    }
}
