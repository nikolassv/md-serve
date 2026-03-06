package de.nikolassv.mdserve.render;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record FileEntry(String name, String path) {}
