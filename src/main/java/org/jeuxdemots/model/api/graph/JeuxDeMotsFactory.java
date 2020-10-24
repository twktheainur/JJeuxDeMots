package org.jeuxdemots.model.api.graph;

public interface JeuxDeMotsFactory {
    JDMNode createNode(final int id, final String name, final int nodeType, final double weight);

    JDMRelation createRelation(final int id, final int sourceId, final int targetId, final JDMRelationType type, final double weight);

    JDMRelationType createRelationType(final int id, final String name, final String extendedName, final String info);


}
