package org.jeuxdemots.model.api.graph;


@SuppressWarnings("SerializableHasSerializationMethods")
public class InvalidNodeTypeCodeException extends RuntimeException {
    private static final long serialVersionUID = 7293313748296122794L;

    public InvalidNodeTypeCodeException(final String message) {
        super("Unknown node type: " + message);
    }
}
