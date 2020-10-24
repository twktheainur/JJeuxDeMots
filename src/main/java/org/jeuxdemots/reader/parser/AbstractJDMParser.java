package org.jeuxdemots.reader.parser;

import org.jeuxdemots.util.JDMUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractJDMParser implements JDMParser {

    private static final Logger logger = LoggerFactory.getLogger(AbstractJDMParser.class);
    private static final double PERCENT_MAX = 100d;


    private enum ParsingStatus {
        METADATA, NODES, RELATION_TYPES, RELATIONS
    }

    private static final Pattern RELATION_TYPES_PATTERN = Pattern.compile("// ---- RELATION TYPES", Pattern.LITERAL);
    private static final Pattern NODES_PATTERN = Pattern.compile("// -- NODES", Pattern.LITERAL);
    private static final Pattern RELATIONS_PATTERN = Pattern.compile("// -- RELATIONS", Pattern.LITERAL);

    private static final Pattern NODE_MAX_ID_PATTERN = Pattern.compile("// MAX NODE ID = ([0-9]+)");
    private static final Pattern SEPARATOR_CHAR_PATTERN = Pattern.compile("SEPARATOR = (.*)");

    private static final Pattern RELATION_MAX_ID_PATTERN = Pattern.compile("// MAX RELATION ID = ([0-9]+)");

    private int numberOfRelations;
    private int numberOfNodes;
    private int total;
    private int currentElement;

    private final boolean loadNodes;
    private final boolean loadRelations;

    protected AbstractJDMParser(final boolean loadNodes, final boolean loadRelations) {
        this.loadNodes = loadNodes;
        this.loadRelations = loadRelations;
    }

    @Override
    @SuppressWarnings({"HardcodedFileSeparator", "IfStatementWithTooManyBranches"})
    public void parseJDM(final BufferedReader bufferedReader) throws IOException {
        ParsingStatus status = ParsingStatus.METADATA;
        String separatorChar = "\\|";
        String line = "";
        boolean initialized = false;

        while (line != null) {
            line = bufferedReader.readLine();
            if (line != null) {
                line = line.trim();

                if (!line.isEmpty()) {
                    if (line.startsWith("//")) {
                        status = ParsingStatus.METADATA;
                    }
                    if (status == ParsingStatus.METADATA) {
                        final Matcher nodeMaxIdMatcher = NODE_MAX_ID_PATTERN.matcher(line);
                        final Matcher relatioMaxIdMatcher = RELATION_MAX_ID_PATTERN.matcher(line);
                        final Matcher separatorCharMatcher = SEPARATOR_CHAR_PATTERN.matcher(line);

                        if (nodeMaxIdMatcher.matches()) {
                            numberOfNodes = Integer.valueOf(nodeMaxIdMatcher.group(1));
                        } else if (relatioMaxIdMatcher.matches()) {
                            numberOfRelations = Integer.valueOf(relatioMaxIdMatcher.group(1));
                        } else if (separatorCharMatcher.matches()) {
                            separatorChar = separatorCharMatcher.group(1);
                            if (separatorChar.equals("|")) {
                                separatorChar = "\\|";
                            }
                        } else if (matches(RELATION_TYPES_PATTERN, line)) {
                            status = ParsingStatus.RELATION_TYPES;
                        } else if (matches(NODES_PATTERN, line)) {
                            status = ParsingStatus.NODES;
                        } else if (matches(RELATIONS_PATTERN, line)) {
                            status = ParsingStatus.RELATIONS;
                        }
                    } else {

                        if (!initialized) {
                            initializeContainers(numberOfNodes, numberOfRelations);
                            initialized = true;
                            if (isLoadNodes()) {
                                total += numberOfNodes;
                            }
                            if (isLoadRelations()) {
                                total += numberOfRelations;
                            }
                        }

                        final Map<String, String> fields = JDMUtil.parseDataLine(line, separatorChar);

                        if (status == ParsingStatus.RELATION_TYPES) {
                            processRelationType(fields);
                        } else if (status == ParsingStatus.NODES) {
                            processNode(fields);
                            currentElement++;

                        } else if (status == ParsingStatus.RELATIONS) {
                            processRelation(fields);
                            currentElement++;
                        }
                    }
                }
            }
        }
    }

    protected void logProgress() {
        final double percentProgress = (currentElement / (double) total) * PERCENT_MAX;
        final int threshold = (int) (0.01 * total);
        if ((currentElement % threshold) == 0) {
            logger.info("\r[{}%] Processing element #{}                                   ", String.format("%2.2f", percentProgress), currentElement);
        }
    }

    private boolean matches(final Pattern pattern, final CharSequence line) {
        return pattern
                .matcher(line)
                .matches();
    }

    protected boolean isLoadNodes() {
        return loadNodes;
    }

    protected boolean isLoadRelations() {
        return loadRelations;
    }
}
