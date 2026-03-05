package de.nikolassv.mdserve.template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record Breadcrumb(String path, String label) {

    public static List<Breadcrumb> listFor(String urlPath) {
        if (urlPath == null || urlPath.isBlank()) return Collections.emptyList();
        List<Breadcrumb> crumbs = new ArrayList<>();
        StringBuilder cumulative = new StringBuilder();
        for (String part : urlPath.split("/")) {
            if (part.isBlank()) continue;
            cumulative.append("/").append(part);
            crumbs.add(new Breadcrumb(cumulative.toString(), part));
        }
        return crumbs;
    }
}
