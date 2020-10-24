package org.jeuxdemots.model.api.graph;


@SuppressWarnings("SerializableHasSerializationMethods")
public class AccessException extends RuntimeException {
    private static final long serialVersionUID = 7293313748296122794L;

    public AccessException(final String message) {
        super("No such node or relation exists " + message);
    }
}
