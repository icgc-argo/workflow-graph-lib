package org.icgc_argo.workflow_graph_lib.polyglot.enums;

public enum GraphFunctionLanguage {
    JS("js"),
    PYTHON("python");

    private final String text;

    GraphFunctionLanguage(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
