package org.jeuxdemots.reader.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public interface JDMParser {
    void parseJDM(final BufferedReader bufferedReader) throws IOException;
    void processRelationType(final Map<String, String> fields);
    void processNode(final Map<String, String> fields);
    void processRelation(final Map<String, String> fields);
    void initializeContainers(final int numberOfNodes, final int numberOfRelations) throws IOException;
}
