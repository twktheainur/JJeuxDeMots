package org.jeuxdemots.model.graph.inmemory;

import org.apache.commons.lang3.tuple.Pair;
import org.jeuxdemots.model.api.graph.*;
import org.jeuxdemots.model.api.lexical.JDMLexicalAspect;
import org.jeuxdemots.model.lexical.DefaultJDMLexicalAspect;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class InMemoryJeuxDeMots implements JeuxDeMots {

    private final NodeContainer jdmNodes;
    private final RelationContainer jdmRelations;
    private JDMLexicalAspect lexicalAspect = null;

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    InMemoryJeuxDeMots(final NodeContainer jdmNodes, final RelationContainer jdmRelations) {
        this.jdmNodes = jdmNodes;
        this.jdmRelations = jdmRelations;
    }


    @Override
    public JDMLexicalAspect getLexicalAspect() {
        if (lexicalAspect == null) {
            lexicalAspect = new DefaultJDMLexicalAspect(this);
        }
        return lexicalAspect;
    }

    @Override
    public void forEachNode(final Consumer<JDMNode> consumer) {
        jdmNodes.forEach(consumer);
    }

    @Override
    public void forEachNodeOfType(final Consumer<JDMNode> consumer, final NodeType nodeType) {
        jdmNodes.typedIterable(nodeType).forEach(consumer);
    }

    @Override
    public Optional<JDMNode> getNode(final String name) {
        return Optional.empty();
    }

    @Override
    public Optional<JDMNode> getNode(final int id) {
        return Optional.of(jdmNodes.get(id - 1));
    }

    @Override
    public Optional<JDMRelationType> findType(final int id) {
        return Optional.of(jdmRelations.get(id - 1).getType());
    }

    @Override
    public Optional<JDMRelationType> findType(final String name) {
        return Optional.of(jdmRelations.findType(name));
    }

    @Override
    public Optional<JDMRelation> getRelation(final int id) {
        return Optional.of(jdmRelations.get(id - 1));
    }

    @Override
    public Collection<JDMRelation> getIncomingRelations(final JDMRelationType type, final JDMNode target) {
        return jdmRelations.incomingRelations(target).stream().filter(jdmRelation -> jdmRelation.getType() == type).collect(Collectors.toList());
    }

    @Override
    public Collection<JDMRelation> getOutgoingRelations(final JDMRelationType type, final JDMNode source) {
        return jdmRelations.outgoingRelations(source).stream().filter(jdmRelation -> jdmRelation.getType() == type).collect(Collectors.toList());
    }

    @Override
    public Collection<JDMRelation> getIncomingRelations(final JDMNode target) {
        return jdmRelations.incomingRelations(target);
    }

    @Override
    public Collection<JDMRelation> getOutgoingRelations(final JDMNode source) {
        return jdmRelations.outgoingRelations(source);
    }

    @Override
    public Collection<Pair<JDMRelation, JDMNode>> getOutgoingNeighbourhood(JDMNode source) {
        Collection<JDMRelation> outgoingRelations = getOutgoingRelations(source);
        return outgoingRelations.stream()
                .map(relation -> Pair.of(
                        relation,
                        getNode(relation.getTargetId().getValue())
                                .orElseThrow(() ->
                                        new AccessException(String.valueOf(relation.getTargetId().getValue()))
                                )
                        )
                ).collect(Collectors.toList());
    }

    @Override
    public Optional<JDMNode> getRelationSource(final JDMRelation relation) {
        return Optional.of(jdmNodes.get(relation.getSourceId().intValue() - 1));
    }

    @Override
    public Optional<JDMNode> getRelationTarget(final JDMRelation relation) {
        return Optional.of(jdmNodes.get(relation.getTargetId().intValue() - 1));
    }
}
