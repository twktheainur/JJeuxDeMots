package org.jeuxdemots.model.api.graph;

import java.util.List;

public interface RelationContainer extends List<JDMRelation> {
    List<JDMRelation> outgoingRelations(JDMNode node);
    List<JDMRelation> incomingRelations(JDMNode node);
    JDMRelationType findType(int id);
    JDMRelationType findType(final String name);
    void addRelationType(JDMRelationType relationType);
}
