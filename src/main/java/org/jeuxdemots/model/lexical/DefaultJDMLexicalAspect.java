package org.jeuxdemots.model.lexical;

import org.jeuxdemots.model.api.graph.*;
import org.jeuxdemots.model.api.lexical.JDMLexicalAspect;
import org.jeuxdemots.model.api.lexical.JDMLexicalEntry;
import org.jeuxdemots.model.api.lexical.JDMLexicalSense;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class DefaultJDMLexicalAspect implements JDMLexicalAspect {

    static final String R_RAFF_SEM = "r_raff_sem";
    protected final JeuxDeMots jeuxDeMots;
    protected final JDMRelationType raffSemType;

    public DefaultJDMLexicalAspect(final JeuxDeMots jeuxDeMots) {
        this.jeuxDeMots = jeuxDeMots;

        raffSemType = jeuxDeMots.findType(R_RAFF_SEM)
                .orElseThrow(() -> new CorruptedJeuxDeMotData("Cannot find semantic refinement relation type"));
    }

    @Override
    public void forEachLexicalEntry(final Consumer<JDMLexicalEntry> consumer) {
        jeuxDeMots.forEachNodeOfType(node -> {
                    final JDMLexicalEntry lexicalEntry = nodeToLexicalEntry(node);
                    if (lexicalEntry != null) {
                        consumer.accept(lexicalEntry);
                    }
                },
                NodeType.TERM);
    }

    @Override
    public Optional<JDMLexicalEntry> getLexicalEntry(final int id) {
        return jeuxDeMots.getNode(id).map(this::nodeToLexicalEntry);
    }


    @Override
    public Optional<JDMLexicalSense> getLexicalSense(final int id) {
        return jeuxDeMots.getNode(id).map(this::nodeToLexicalSense);
    }

    @Override
    public Optional<JDMLexicalEntry> getLexicalEntry(JDMNode node) {
        return getLexicalEntry(node.getIdInt());
    }

    @Override
    public Optional<JDMLexicalSense> getLexicalSense(JDMNode node) {
        return getLexicalSense(node.getIdInt());
    }

    @Override
    public Optional<JDMLexicalEntry> getLexicalEntry(String name) {
        Optional<JDMNode> node = jeuxDeMots.getNode(name);
        if (node.isPresent()) {
            return getLexicalEntry(node.get());
        }
        return Optional.empty();
    }

    /**
     * Converts a JDMNode into a corresponding lexical entry, if the JDMNode corresponds to a lexical entry,
     * otherwise returns null.
     *
     * @return The corresponding lexical entry or null
     */
    protected JDMLexicalEntry nodeToLexicalEntry(final JDMNode node) {

        JDMLexicalEntry lexicalEntry = null;

        //Lexical entries have no incoming sense refinement relations, otherwise we are dealing with a lexical sense
        final Map<JDMRelationType, List<JDMRelation>> incomingRefinementRelations = JDMLexicalAspect.relationListToRelationMap(jeuxDeMots.getIncomingRelations(raffSemType, node));
        if (incomingRefinementRelations.isEmpty()) {
            final Map<JDMRelationType, List<JDMRelation>> outgoingRelationMap = JDMLexicalAspect.relationListToRelationMap(jeuxDeMots.getOutgoingRelations(node));

            lexicalEntry = new DefaultJDMLexicalEntry(this, node, outgoingRelationMap);
        }

        return lexicalEntry;
    }


    protected JDMLexicalSense nodeToLexicalSense(final JDMNode node) {

        JDMLexicalSense lexicalSense = null;
        if (node.getName().contains(">")) {
            lexicalSense = new DefaultJDMLexicalSense(node, this);
        }
        return lexicalSense;
    }


    @Override
    public JDMLexicalAspect getLexicalAspect() {
        return this;
    }

    @Override
    public void forEachNode(final Consumer<JDMNode> consumer) {
        jeuxDeMots.forEachNode(consumer);
    }

    @Override
    public void forEachNodeOfType(final Consumer<JDMNode> consumer, final NodeType nodeType) {
        jeuxDeMots.forEachNodeOfType(consumer, nodeType);
    }

    @Override
    public Optional<JDMNode> getNode(final String name) {
        return jeuxDeMots.getNode(name);
    }

    @Override
    public Optional<JDMNode> getNode(final int id) {
        return jeuxDeMots.getNode(id);
    }

    @Override
    public Optional<JDMRelationType> findType(final int id) {
        return jeuxDeMots.findType(id);
    }

    @Override
    public Optional<JDMRelationType> findType(final String name) {
        return jeuxDeMots.findType(name);
    }

    @Override
    public Optional<JDMRelation> getRelation(final int id) {
        return jeuxDeMots.getRelation(id);
    }

    @Override
    public Collection<JDMRelation> getIncomingRelations(final JDMRelationType type, final JDMNode target) {
        return jeuxDeMots.getIncomingRelations(type, target);
    }

    @Override
    public Collection<JDMRelation> getOutgoingRelations(final JDMRelationType type, final JDMNode source) {
        return jeuxDeMots.getOutgoingRelations(type, source);
    }

    @Override
    public Collection<JDMRelation> getIncomingRelations(final JDMNode target) {
        return jeuxDeMots.getIncomingRelations(target);
    }

    @Override
    public Collection<JDMRelation> getOutgoingRelations(final JDMNode source) {
        return jeuxDeMots.getOutgoingRelations(source);
    }

    @Override
    public Optional<JDMNode> getRelationSource(final JDMRelation relation) {
        return jeuxDeMots.getRelationSource(relation);
    }

    @Override
    public Optional<JDMNode> getRelationTarget(final JDMRelation relation) {
        return jeuxDeMots.getRelationTarget(relation);
    }
}
