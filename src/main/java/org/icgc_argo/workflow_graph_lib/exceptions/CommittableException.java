package org.icgc_argo.workflow_graph_lib.exceptions;

/**
 * Exceptions that are voluntary and not retryable (ie. activation function returns false)
 */
public abstract class CommittableException extends RuntimeException {
    public CommittableException(String exception) {
        super(exception);
    }

    public CommittableException() {
        super();
    }
}