package org.jeuxdemots.model.graph.inmemory;

import org.jeuxdemots.model.api.graph.JeuxDeMots;
import org.jeuxdemots.model.api.graph.JeuxDeMotsFactory;
import org.jeuxdemots.model.api.graph.NodeContainer;
import org.jeuxdemots.model.api.graph.RelationContainer;
import org.jeuxdemots.model.graph.inmemory.InMemoryJeuxDeMots;
import org.jeuxdemots.model.graph.inmemory.InMemoryJeuxDeMotsFactory;
import org.jeuxdemots.reader.JDMLoader;
import org.jeuxdemots.reader.parser.AbstractJDMParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JDMInMemoryLoaderFromDump extends AbstractJDMParser implements JDMLoader {


    private final InputStream jdmInputStream;
    private NodeContainer nodes;
    private RelationContainer relations;
    private final JeuxDeMotsFactory jeuxDeMotsFactory;


    public JDMInMemoryLoaderFromDump(final InputStream jdmInputStream) {
        super(true,true);
        this.jdmInputStream = jdmInputStream;
        this.jeuxDeMotsFactory = new InMemoryJeuxDeMotsFactory();
    }

    @Override
    public JeuxDeMots load() throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(jdmInputStream, StandardCharsets.ISO_8859_1))) {
            parseJDM(bufferedReader);
        }

        return new InMemoryJeuxDeMots(nodes,relations);
    }


    @Override
    public void processRelationType(final Map<String, String> fields) {
        relations.addRelationType(
                jeuxDeMotsFactory.createRelationType(
                        Integer.parseInt(fields.get("rtid")),
                        fields.get("name"),
                        fields.get("nom_etendu"),
                        fields.get("info")));
    }

    @Override
    public void processNode(final Map<String, String> fields) {
        final String eid = fields.get("eid");
        final String t = fields.get("t");
        final String w = fields.get("w");
        if((eid != null) && (t != null) && (w != null)) {
            nodes.add(
                    jeuxDeMotsFactory.createNode(
                            Integer.parseInt(fields.get("eid")),
                            fields.get("n"),
                            Integer.parseInt(fields.get("t")),
                            Double.parseDouble(fields.get("w"))
                    )
            );
        }
    }

    @Override
    public void processRelation(final Map<String, String> fields) {
        relations.add(
                jeuxDeMotsFactory.createRelation(
                        Integer.parseInt(fields.get("rid")),
                        Integer.parseInt(fields.get("n1")),
                        Integer.parseInt(fields.get("n2")),
                        relations.findType(Integer.parseInt(fields.get("t"))),
                        Double.parseDouble(fields.get("w"))
                )
        );
    }

    @Override
    public void initializeContainers(final int numberOfNodes, final int numberOfRelations) throws IOException {
        if ((numberOfNodes == 0) || (numberOfRelations == 0)) {
            throw new IOException("Incorrect dump file, max node id or max relation id missing");
        }
        relations = new InMemoryRelationContainer(numberOfRelations, numberOfNodes);
        nodes = new InMemoryNodeContainer(numberOfNodes);
    }
}
