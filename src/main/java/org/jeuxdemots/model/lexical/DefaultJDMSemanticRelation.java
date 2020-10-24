package org.jeuxdemots.model.lexical;

import org.jeuxdemots.model.api.graph.JDMRelation;
import org.jeuxdemots.model.api.lexical.JDMLexicalSense;
import org.jeuxdemots.model.api.lexical.JDMSemanticRelation;
import org.jeuxdemots.model.graph.DefaultJDMRelation;

public class DefaultJDMSemanticRelation extends DefaultJDMRelation implements JDMSemanticRelation {
    private final JDMLexicalSense sourceSense;
    private final JDMLexicalSense targetSense;

    DefaultJDMSemanticRelation(final JDMRelation relation, final JDMLexicalSense sourceSense, final JDMLexicalSense targetSense) {
        super(relation.getId(), relation.getSourceId(), relation.getTargetId(), relation.getType(), relation.getWeight());
        this.sourceSense = sourceSense;
        this.targetSense = targetSense;
    }

    @Override
    public JDMLexicalSense getSourceSense() {
        return sourceSense;
    }

    @Override
    public JDMLexicalSense getTargetSense() {
        return targetSense;
    }
}
