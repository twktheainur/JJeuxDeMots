package org.jeuxdemots.model.lexical;

import org.jeuxdemots.model.api.graph.JDMRelation;
import org.jeuxdemots.model.api.lexical.JDMLexicalEntry;
import org.jeuxdemots.model.api.lexical.JDMLexicalRelation;
import org.jeuxdemots.model.graph.DefaultJDMRelation;

public class DefaultJDMLexicalRelation extends DefaultJDMRelation implements JDMLexicalRelation {
    private final JDMLexicalEntry sourceEntry;
    private final JDMLexicalEntry targetEntry;

    DefaultJDMLexicalRelation(final JDMRelation relation, final JDMLexicalEntry sourceEntry, final JDMLexicalEntry targetEntry) {
        super(relation.getId(), relation.getSourceId(), relation.getTargetId(), relation.getType(), relation.getWeight());
        this.sourceEntry = sourceEntry;
        this.targetEntry = targetEntry;
    }

    @Override
    public JDMLexicalEntry getSourceEntry() {
        return sourceEntry;
    }

    @Override
    public JDMLexicalEntry getTargetEntry() {
        return targetEntry;
    }
}
