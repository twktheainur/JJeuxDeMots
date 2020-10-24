package org.jeuxdemots.reader;


import org.jeuxdemots.model.api.graph.JeuxDeMots;

import java.io.IOException;

@FunctionalInterface
public interface JDMLoader {
    JeuxDeMots load() throws IOException;
}
