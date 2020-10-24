package org.jeuxdemots.model.api.lexical;

import org.jeuxdemots.model.api.graph.JDMRelation;

public interface JDMSemanticRelation extends JDMRelation {
    JDMLexicalSense getSourceSense();
    JDMLexicalSense getTargetSense();
}
