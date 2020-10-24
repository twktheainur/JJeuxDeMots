package org.jeuxdemots.model.graph.sql;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.tuple.Pair;
import org.jeuxdemots.model.api.graph.*;
import org.jeuxdemots.model.api.lexical.JDMLexicalAspect;
import org.jeuxdemots.model.graph.DefaultJDMNode;
import org.jeuxdemots.model.graph.DefaultJDMRelation;
import org.jeuxdemots.model.graph.DefaultJDMRelationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class SQLJeuxDeMots implements JeuxDeMots {


    private static final Logger logger = LoggerFactory.getLogger(SQLJeuxDeMots.class);

    private final Connection connection;
    private final Connection streamConnection;

    private final Map<String, JDMRelationType> relationTypesNameIndex;
    private final Map<Integer, JDMRelationType> relationTypesIdIndex;

    private final Map<Integer, JDMNode> nodeCache;
    private final Map<String, Integer> nodeNameCacheIndex;

    private JDMLexicalAspect lexicalAspect = null;

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public SQLJeuxDeMots(final Connection connection, final Connection streamConnection) throws SQLException {
        this.connection = connection;
        this.streamConnection = streamConnection;

        nodeCache = new HashMap<>();
        nodeNameCacheIndex = new HashMap<>();

        relationTypesIdIndex = new HashMap<>();
        relationTypesNameIndex = new HashMap<>();
        fetchRelationTypes();
    }

    private Optional<JDMNode> createJDMNode(final String name) {
        Optional<JDMNode> node = Optional.empty();
        Integer id = nodeNameCacheIndex.get(name);
        if (id != null) {
            node = createJDMNode(id);
        }
        return node;
    }

    private Optional<JDMNode> createJDMNode(final int id) {
        JDMNode node = nodeCache.get(id);
        if (node == null) {
            String query = "SELECT * FROM nodes";
            query += " WHERE id=?";

            try (final PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);
                node = nodesFromPreparedStatement(statement).get(0);
                nodeCache.put(node.getIdInt(), node);
                nodeNameCacheIndex.put(node.getName(), node.getIdInt());
            } catch (final SQLException e) {
                logger.error("getNode@SQLJeuxDeMots: {}", e.toString());
            }
        }
        return Optional.ofNullable(node);
    }

    private void fetchRelationTypes() throws SQLException {
        String query = "SELECT * FROM edge_types";
        try (final PreparedStatement statement = connection.prepareStatement(query)) {
            final List<JDMRelationType> relationTypes = relationTypesFromPreparedStatement(statement);
            for (JDMRelationType relationType : relationTypes) {
                relationTypesNameIndex.put(relationType.getName(), relationType);
                relationTypesIdIndex.put(relationType.getId().getValue(), relationType);
            }
        }
    }

    @Override
    public JDMLexicalAspect getLexicalAspect() {
        if (lexicalAspect == null) {
            lexicalAspect = new SQLLexicalAspect(this, connection, streamConnection);
        }
        return lexicalAspect;
    }


    @Override
    public void forEachNode(final Consumer<JDMNode> consumer) {
        forEachNodeDelegate(consumer, null);
    }

    @Override
    public void forEachNodeOfType(final Consumer<JDMNode> consumer, final NodeType nodeType) {
        forEachNodeDelegate(consumer, nodeType);
    }

    private void forEachNodeDelegate(final Consumer<JDMNode> consumer, final NodeType nodeType) {
        String query = "SELECT * FROM nodes";
        if (nodeType != null) {
            query += " WHERE type=?";
        }
        try (final PreparedStatement statement = streamConnection.prepareStatement(query,
                java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY)
        ) {
            statement.setFetchSize(Integer.MIN_VALUE);
            if (nodeType != null) {
                statement.setInt(1, nodeType.getCode());
            }
            try (final ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    consumer.accept(
                            new DefaultJDMNode(
                                    new MutableInt(resultSet.getInt(1)),
                                    resultSet.getString(2),
                                    resultSet.getInt(3),
                                    new MutableDouble(resultSet.getDouble(4))
                            )
                    );

                }
            }

        } catch (final SQLException e) {
            logger.error("[forEachNode | forEachNodeOfType]@SQLJeuxDeMots: {}", e.toString());
        }
    }

    @Override
    public Optional<JDMNode> getNode(final String name) {
        return createJDMNode(name);
    }

    @Override
    public Optional<JDMNode> getNode(final int id) {
        return createJDMNode(id);
    }


    private List<JDMNode> nodesFromPreparedStatement(final PreparedStatement statement) throws SQLException {
        final List<JDMNode> nodes = new ArrayList<>();
        try (final ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                nodes.add(new DefaultJDMNode(
                        new MutableInt(resultSet.getInt(1)),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        new MutableDouble(resultSet.getDouble(4))
                ));
            }
        }
        return nodes;
    }

    private List<JDMRelationType> relationTypesFromPreparedStatement(final PreparedStatement statement) throws SQLException {
        List<JDMRelationType> relationTypes = new ArrayList<>();
        try (final ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                final JDMRelationType relationType = new DefaultJDMRelationType(
                        new MutableInt(resultSet.getInt(1)),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4)
                );
                relationTypes.add(relationType);
            }
        }
        return relationTypes;
    }

    private JDMRelation relationFromPreparedStatement(final PreparedStatement statement) throws SQLException {
        final JDMRelation relation;
        try (final ResultSet resultSet = statement.executeQuery()) {
            relation = relationsFromResultSet(resultSet).get(0);
        }
        return relation;
    }

    private List<JDMRelation> relationsFromResultSet(final ResultSet resultSet) throws SQLException {
        final List<JDMRelation> relations = new ArrayList<>();
        while (resultSet.next()) {
            final int type = resultSet.getInt(4);
            final JDMRelationType relationType = findType(type)
                    .orElseThrow(() -> new AccessException(String.valueOf(type)));

            JDMRelation relation = new DefaultJDMRelation(
                    new MutableInt(resultSet.getInt(1)),
                    new MutableInt(resultSet.getInt(2)),
                    new MutableInt(resultSet.getInt(3)),
                    relationType,
                    new MutableDouble(resultSet.getDouble(5)));
            relations.add(relation);
        }
        return relations;
    }

    @Override
    public Optional<JDMRelationType> findType(final int id) {
        return Optional.ofNullable(relationTypesIdIndex.get(id));
    }

    @Override
    public Optional<JDMRelationType> findType(final String name) {
        return Optional.ofNullable(relationTypesNameIndex.get(name));
    }

    @Override
    public Optional<JDMRelation> getRelation(final int id) {
        String query = "SELECT * FROM edges";
        query += " WHERE id=?";
        JDMRelation relation = null;
        try (final PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            relation = relationFromPreparedStatement(statement);
            return Optional.of(relation);
        } catch (final SQLException e) {
            logger.error("getRelations@SQLJeuxDeMots: {}", e.toString());
        }
        return Optional.empty();
    }

    @Override
    public Collection<JDMRelation> getIncomingRelations(final JDMRelationType type, final JDMNode target) {
        String query = "SELECT * FROM edges WHERE destination=?";
        if (type != null) {
            query += " AND type=?";
        }
        return getInOutRelationsFromQuery(query, type, target);
    }

    @Override
    public Collection<JDMRelation> getOutgoingRelations(final JDMRelationType type, final JDMNode source) {
        String query = "SELECT * FROM edges WHERE source=?";
        if (type != null) {
            query += " AND type=?";
        }
        return getInOutRelationsFromQuery(query, type, source);
    }



    private Collection<JDMRelation> getInOutRelationsFromQuery(final String query, final JDMRelationType type, final JDMNode node) {
        Collection<JDMRelation> relations = Collections.emptyList();
        try (final PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, node
                    .getId()
                    .intValue());
            if (type != null) {
                statement.setInt(2, type
                        .getId()
                        .intValue());
            }

            try (final ResultSet resultSet = statement.executeQuery()) {
                relations = relationsFromResultSet(resultSet);
            }
        } catch (final SQLException e) {
            logger.error("getRelations@SQLJeuxDeMots: {}", e.toString());
        }
        return relations;

    }

    @Override
    public Collection<JDMRelation> getIncomingRelations(final JDMNode target) {
        return getIncomingRelations(null, target);
    }


    @Override
    public Collection<JDMRelation> getOutgoingRelations(final JDMNode source) {
        return getOutgoingRelations(null, source);
    }

    @Override
    public Collection<Pair<JDMRelation, JDMNode>> getOutgoingNeighbourhood(JDMNode source) {
        return null;
    }

    @Override
    public Optional<JDMNode> getRelationSource(final JDMRelation relation) {
        return getNode(relation
                .getSourceId()
                .intValue());
    }

    @Override
    public Optional<JDMNode> getRelationTarget(final JDMRelation relation) {
        return getNode(relation
                .getTargetId()
                .intValue());
    }
}
