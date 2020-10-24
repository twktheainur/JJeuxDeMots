package org.jeuxdemots.model.graph;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jeuxdemots.model.api.graph.JDMRelation;
import org.jeuxdemots.model.api.graph.JDMRelationType;

public class DefaultJDMRelation implements JDMRelation {
    private final MutableInt id;
    private final MutableInt sourceId;
    private final MutableInt targetId;
    private final JDMRelationType type;
    private final MutableDouble weight;

    public DefaultJDMRelation(final MutableInt id, final MutableInt sourceId, final MutableInt targetId, final JDMRelationType type, final MutableDouble weight) {
        this.id = id;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.type = type;
        this.weight = weight;
    }

    @Override
    public MutableInt getId() {
        return id;
    }

    @Override
    public MutableInt getSourceId() {
        return sourceId;
    }

    @Override
    public MutableInt getTargetId() {
        return targetId;
    }

    @Override
    public JDMRelationType getType() {
        return type;
    }

    @Override
    public MutableDouble getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "DefaultJDMRelation{" +
                "id=" + id +
                ", sourceId=" + sourceId +
                ", targetId=" + targetId +
                ", type=" + type +
                ", weight=" + weight +
                '}';
    }
}
