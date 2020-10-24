package org.jeuxdemots.model.api.graph;

public class CorruptedJeuxDeMotData extends RuntimeException {
    public CorruptedJeuxDeMotData(String message) {
        super("Corrupted or invalid JDM Structure: " + message);
    }
}
