package org.icgc_argo.workflow_graph_lib.polyglot.enums;

public enum EdgeFunctionLanguage {
    JS("js"),
    PYTHON("python");

    private final String text;

    EdgeFunctionLanguage(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
