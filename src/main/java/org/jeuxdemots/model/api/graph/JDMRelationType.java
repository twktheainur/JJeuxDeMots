package org.jeuxdemots.model.api.graph;

import org.apache.commons.lang3.mutable.MutableInt;

public interface JDMRelationType {
    MutableInt getId();
    String getName();
    String getExtendedName();
    String getInfo();

}
