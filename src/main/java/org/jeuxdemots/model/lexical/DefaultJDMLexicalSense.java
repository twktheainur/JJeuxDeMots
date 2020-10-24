package org.jeuxdemots.model.lexical;

import org.jeuxdemots.model.api.graph.*;
import org.jeuxdemots.model.api.lexical.JDMLexicalAspect;
import org.jeuxdemots.model.api.lexical.JDMLexicalSense;
import org.jeuxdemots.model.api.lexical.JDMSemanticRelation;
import org.jeuxdemots.model.graph.DefaultJDMNode;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultJDMLexicalSense extends DefaultJDMNode implements JDMLexicalSense {

    private final String gloss;
    private final WeakReference<JDMLexicalAspect> jeuxDeMots;

    private final Map<JDMRelationType, List<JDMRelation>> outgoingRelations;

    DefaultJDMLexicalSense(final JDMNode node, final JDMLexicalAspect jeuxDeMots) {
        super(node.getId(), node.getName(), node.getNodeType(), node.getWeight());
        final String glossIdString = extractGlossNodeId(node);
        this.jeuxDeMots = new WeakReference<>(jeuxDeMots);
        if (glossIdString.isEmpty()) {
            gloss = "";
        } else {
            final int glossNodeId = Integer.parseInt(extractGlossNodeId(node));
            gloss = getGlossString(jeuxDeMots, glossNodeId);
        }
        outgoingRelations = JDMLexicalAspect.relationListToRelationMap(jeuxDeMots.getOutgoingRelations(node));
    }

    private String extractGlossNodeId(final JDMNode jdmNode) {
        final String name = jdmNode.getName();
        return ((name != null) && name.contains(">")) ? name.split(">")[1] : "";
    }

    @SuppressWarnings("LawOfDemeter")
    private String getGlossString(final JeuxDeMots jeuxDeMots, final int nodeId) {
        final JDMNode node = jeuxDeMots.getNode(nodeId).orElseThrow(() -> new AccessException(Integer.toString(nodeId)));
        return node.getName();
    }

    @Override
    public String getGloss() {
        return gloss;
    }


    @Override
    public Collection<JDMSemanticRelation> getSemanticRelations() {
        final Collection<JDMSemanticRelation> semanticRelations = new ArrayList<>();
        final List<JDMRelation> flatOutgoingRelations = outgoingRelations.values()
                .stream().flatMap(Collection::stream)
                .filter(Objects::nonNull).collect(Collectors.toList());

        for (final JDMRelation outgoingRelation : flatOutgoingRelations) {
            final int targetId = outgoingRelation.getTargetId().intValue();
            final Optional<JDMLexicalSense> targetSense = Objects.requireNonNull(jeuxDeMots.get()).getLexicalSense(targetId);
            targetSense.ifPresent(jdmLexicalSense
                    -> semanticRelations.add(new DefaultJDMSemanticRelation(outgoingRelation, this, jdmLexicalSense)));
        }

        return semanticRelations;
    }

}
