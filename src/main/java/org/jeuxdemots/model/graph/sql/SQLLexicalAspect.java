package org.jeuxdemots.model.graph.sql;

import org.jeuxdemots.model.api.graph.JDMNode;
import org.jeuxdemots.model.api.graph.JDMRelation;
import org.jeuxdemots.model.api.graph.JDMRelationType;
import org.jeuxdemots.model.api.graph.JeuxDeMots;
import org.jeuxdemots.model.api.lexical.JDMLexicalAspect;
import org.jeuxdemots.model.api.lexical.JDMLexicalEntry;
import org.jeuxdemots.model.lexical.DefaultJDMLexicalAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SQLLexicalAspect extends DefaultJDMLexicalAspect {


    private final Logger logger = LoggerFactory.getLogger(SQLLexicalAspect.class);

    private final Connection connection;
    private final Connection streamConnection;

    SQLLexicalAspect(JeuxDeMots jeuxDeMots, final Connection connection, final Connection streamConnection) {
        super(jeuxDeMots);
        this.connection = connection;
        this.streamConnection = streamConnection;
    }

    protected JDMLexicalEntry nodeToLexicalEntry(final JDMNode node) {

        JDMLexicalEntry lexicalEntry = null;

        //Lexical entries have no incoming sense refinement relations, otherwise we are dealing with a lexical sense
        final Map<JDMRelationType, List<JDMRelation>> incomingRefinementRelations =
                JDMLexicalAspect.relationListToRelationMap(jeuxDeMots.getIncomingRelations(raffSemType, node));


        final String query = "select distinct nodes.id,\n" +
                "       nodes.name,\n" +
                "       nodes.type,\n" +
                "       nodes.weight,\n" +
                "       out_r.weight,\n" +
                "       out_r.type,\n" +
                "       out_r.destination,\n" +
                "       out_dest_n.type,\n" +
                "       out_dest_n.weight,\n" +
                "       out_dest_n.name\n" +
                "from nodes,\n" +
                "     edges as out_r,\n" +
                "     nodes as out_dest_n\n" +
                "where nodes.id = ?\n" +
                "  and out_r.destination = out_dest_n.id and nodes.id != out_dest_n.id;";

        try (final PreparedStatement statement = streamConnection.prepareStatement(query,
                java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY)
        ) {
            statement.setFetchSize(Integer.MIN_VALUE);

            statement.setInt(1, id);
            try (final ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {


//                            new MutableInt(resultSet.getInt(1)

                }
            }

        } catch (final SQLException e) {
            logger.error("[forEachNode | forEachNodeOfType]@SQLJeuxDeMots: {}", e.toString());
        }

    }

}
