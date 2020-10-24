package org.jeuxdemots.model.api.lexical;

import org.jeuxdemots.model.api.graph.JDMNode;

import java.util.Collection;

public interface JDMLexicalSense extends JDMNode{
    String getGloss();

    Collection<JDMSemanticRelation> getSemanticRelations();
}
