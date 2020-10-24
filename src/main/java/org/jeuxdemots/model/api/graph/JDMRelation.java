package org.jeuxdemots.model.api.graph;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;

public interface JDMRelation {
    MutableInt getId();

    MutableInt getSourceId();

    MutableInt getTargetId();

    JDMRelationType getType();

    MutableDouble getWeight();
}
