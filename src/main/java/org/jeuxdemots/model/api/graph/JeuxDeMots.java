package org.jeuxdemots.model.api.graph;


import org.apache.commons.lang3.tuple.Pair;
import org.jeuxdemots.model.api.lexical.JDMLexicalAspect;
import org.jeuxdemots.model.graph.inmemory.JDMInMemoryLoaderFromDump;
import org.jeuxdemots.model.graph.sql.SQLJeuxDeMots;
import org.jeuxdemots.reader.JDMLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

public interface JeuxDeMots {

    static JeuxDeMots createFromDump(InputStream jdmInputStream) throws IOException {
        JDMLoader loader = new JDMInMemoryLoaderFromDump(jdmInputStream);
        return loader.load();
    }

    static JeuxDeMots createFromDump(Path jdmDumpPath) throws IOException {
        final InputStream jdmInputStream = Files.newInputStream(jdmDumpPath, StandardOpenOption.READ);
        return createFromDump(jdmInputStream);
    }

    static JeuxDeMots createFromDump(String jdmDumpPath) throws IOException {
        return createFromDump(Paths.get(jdmDumpPath));
    }

    static JeuxDeMots createFromSQL(final String jdbcUrl, final String user, final String password) throws SQLException {
        final Connection connection =
                DriverManager.getConnection(jdbcUrl, user, password);

        final Connection streamConnection =
                DriverManager.getConnection(jdbcUrl, user, password);
        return new SQLJeuxDeMots(connection, streamConnection);
    }

    JDMLexicalAspect getLexicalAspect();

    void forEachNode(final Consumer<JDMNode> consumer);

    void forEachNodeOfType(final Consumer<JDMNode> consumer, final NodeType nodeType);

    Optional<JDMNode> getNode(String name);

    Optional<JDMNode> getNode(int id);

    Optional<JDMRelationType> findType(int id);

    Optional<JDMRelationType> findType(String name);

    Optional<JDMRelation> getRelation(int id);

    Collection<JDMRelation> getIncomingRelations(JDMRelationType type, JDMNode target);

    Collection<JDMRelation> getOutgoingRelations(JDMRelationType type, JDMNode source);

    Collection<JDMRelation> getIncomingRelations(JDMNode target);

    Collection<JDMRelation> getOutgoingRelations(JDMNode source);

    Collection<Pair<JDMRelation, JDMNode>> getOutgoingNeighbourhood(JDMNode source);

    Optional<JDMNode> getRelationSource(JDMRelation relation);

    Optional<JDMNode> getRelationTarget(JDMRelation relation);

}
