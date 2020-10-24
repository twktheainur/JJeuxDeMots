package org.jeuxdemots.model.lexical;

import org.apache.commons.lang3.tuple.Pair;
import org.jeuxdemots.model.api.graph.*;
import org.jeuxdemots.model.api.lexical.*;
import org.jeuxdemots.model.graph.DefaultJDMNode;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("LawOfDemeter")
public class DefaultJDMLexicalEntry extends DefaultJDMNode implements JDMLexicalEntry {

    private final List<JDMLexicalSense> senses;
    private final Map<JDMRelationType, List<JDMRelation>> outgoingRelations;
    private final WeakReference<JDMLexicalAspect> jeuxDeMots;
    private final List<Pair<JDMNode, Double>> sentiment;
    private JDMPolarity polarity;
    private String domain;
    private String posTag = "";
    private List<String> morphologicalInformation;


    DefaultJDMLexicalEntry(final JDMLexicalAspect jeuxDeMots, final JDMNode node,
                           final Map<JDMRelationType, List<JDMRelation>> outgoingRelations) {
        super(node.getId(), node.getName(), node.getNodeType(), node.getWeight());
        this.jeuxDeMots = new WeakReference<>(jeuxDeMots);

        final List<String> grammaticalInformation = pos(outgoingRelations);
        if (!grammaticalInformation.isEmpty()) {
            morphologicalInformation = new ArrayList<>();
            final String[] mainPsParts = grammaticalInformation.get(0).split(":");
            posTag = mainPsParts[0];
            morphologicalInformation = grammaticalInformation;
        }

        senses = senses(outgoingRelations);
        sentiment = sentiment(outgoingRelations);
        extract_meta_information(outgoingRelations);
        this.outgoingRelations = Collections.unmodifiableMap(outgoingRelations);
    }


    @Override
    public String getPosTag() {
        return posTag;
    }

    @Override
    public List<String> getMorphologicalInformation() {
        return Collections.unmodifiableList(morphologicalInformation);
    }


    @Override
    public List<JDMLexicalSense> getSenses() {
        return Collections.unmodifiableList(senses);
    }

    @Override
    public List<Pair<JDMLexicalEntry, Double>> getSentiment() {
        return sentiment.stream().map(pair ->
                Pair.of(Objects.requireNonNull(jeuxDeMots.get())
                                .getLexicalEntry(pair.getKey().getId().intValue()).orElse(null),
                        pair.getValue())).filter(pair -> pair.getKey() != null)
                .collect(Collectors.toList());
    }

    private List<Pair<JDMNode, Double>> sentiment(final Map<JDMRelationType, List<JDMRelation>> outgoingRelations) {
        final JDMLexicalAspect jeuxDeMots = this.jeuxDeMots.get();
        final JDMRelationType relationType = Objects.requireNonNull(jeuxDeMots).findType("r_sentiment").orElseThrow(() -> new AccessException("r_sentiment"));
        List<JDMRelation> sentiments = outgoingRelations.get(relationType);
        if (sentiments == null) {
            sentiments = Collections.emptyList();
        }
        outgoingRelations.remove(relationType);
        return sentiments.stream()
                .map(jdmRelation ->
                        Pair.of(jeuxDeMots.getNode(jdmRelation.getTargetId().intValue())
                                        .orElseThrow(() -> new AccessException(String.valueOf(jdmRelation.getTargetId().intValue()))),
                                jdmRelation.getWeight().getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<JDMLexicalRelation> getLexicalRelations() {
        final Collection<JDMLexicalRelation> lexicalRelations = new ArrayList<>();
        final List<JDMRelation> flatOutgoingRelations = outgoingRelations.values()
                .stream().flatMap(Collection::stream)
                .filter(Objects::nonNull).collect(Collectors.toList());

        for (final JDMRelation outgoingRelation : flatOutgoingRelations) {
            final int targetId = outgoingRelation.getTargetId().intValue();
            final Optional<JDMLexicalEntry> targetEntry = Objects.requireNonNull(jeuxDeMots.get()).getLexicalEntry(targetId);
            targetEntry.ifPresent(jdmLexicalEntry -> lexicalRelations.add(new DefaultJDMLexicalRelation(outgoingRelation, this, jdmLexicalEntry)));
        }

        return lexicalRelations;
    }


    private List<String> pos(final Map<JDMRelationType, List<JDMRelation>> outgoingRelations) {
        final JeuxDeMots jeuxDeMots = this.jeuxDeMots.get();
        final JDMRelationType relationType = Objects.requireNonNull(jeuxDeMots).findType("r_pos").orElseThrow(() -> new AccessException("r_pos"));
        List<JDMRelation> POS = outgoingRelations.get(relationType);
        if (POS == null) {
            POS = Collections.emptyList();
        }
        outgoingRelations.remove(relationType);
        return POS.stream()
                .map(jdmRelation -> jeuxDeMots.getNode(jdmRelation.getTargetId().intValue()))
                .map(node -> node.orElseThrow(() -> new AccessException(node.toString())).getName())
                .collect(Collectors.toList());
    }

    private List<JDMLexicalSense> senses(final Map<JDMRelationType, List<JDMRelation>> relationTypeMap) {
        final JDMLexicalAspect jeuxDeMots = this.jeuxDeMots.get();
        final JDMRelationType raff = Objects.requireNonNull(jeuxDeMots).findType(DefaultJDMLexicalAspect.R_RAFF_SEM)
                .orElseThrow(() -> new AccessException(DefaultJDMLexicalAspect.R_RAFF_SEM));
        final List<JDMRelation> senses = relationTypeMap.get(raff);
        relationTypeMap.remove(raff);

        return ((senses != null) && !senses.isEmpty()) ? senses
                .stream()
                .map(JDMRelation::getTargetId)
                .map(nodeId -> jeuxDeMots.getLexicalSense(nodeId.intValue())).map(Optional::get)
                .collect(Collectors.toList()) : Collections.emptyList();
    }

    @SuppressWarnings("IfStatementWithTooManyBranches")
    private void extract_meta_information(final Map<JDMRelationType, List<JDMRelation>> relationTypeMap) {
        final JDMLexicalAspect jeuxDeMots = this.jeuxDeMots.get();
        final JDMRelationType infopot = Objects.requireNonNull(jeuxDeMots).findType("r_infopot")
                .orElseThrow(() -> new AccessException("r_infopot"));
        final List<JDMRelation> information = relationTypeMap.getOrDefault(infopot, Collections.emptyList());
        relationTypeMap.remove(infopot);

        double positivePolarity = 0d;
        double neutralPolarity = 0d;
        double negativePolarity = 0d;

        for (final JDMRelation relation : information) {
            final JDMNode target = jeuxDeMots.getNode(relation.getTargetId().intValue())
                    .orElseThrow(() -> new AccessException(String.valueOf(relation.getTargetId().intValue())));
            final double relationWeight = relation.getWeight().getValue();
            final String targetName = target.getName();
            if (targetName.contains("_INFO-VOC-")) {
                domain = targetName.split("-")[2];
            } else if (targetName.contains("_POL-POS_PC")) {
                positivePolarity = relationWeight;
            } else if (targetName.contains("_POL-NEUTRE_PC")) {
                neutralPolarity = relationWeight;
            } else if (targetName.contains("_POL-NEG_PC")) {
                negativePolarity = relationWeight;
            }
        }

        polarity = new DefaultJDMPolarity(negativePolarity, neutralPolarity, positivePolarity);
    }


    @Override
    public JDMPolarity getPolarity() {
        return polarity;
    }

    @Override
    public String getDomain() {
        return domain;
    }
}
