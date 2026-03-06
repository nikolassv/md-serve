package de.nikolassv.mdserve.template;

public enum TemplateRole {
    DEFAULT("default"),
    DIRECTORY("directory"),
    ERROR("error");

    private final String id;

    TemplateRole(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }
}
