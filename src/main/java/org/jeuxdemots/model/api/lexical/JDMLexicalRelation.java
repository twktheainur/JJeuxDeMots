package org.jeuxdemots.model.api.lexical;

import org.jeuxdemots.model.api.graph.JDMRelation;

public interface JDMLexicalRelation extends JDMRelation {
    JDMLexicalEntry getSourceEntry();
    JDMLexicalEntry getTargetEntry();
}
