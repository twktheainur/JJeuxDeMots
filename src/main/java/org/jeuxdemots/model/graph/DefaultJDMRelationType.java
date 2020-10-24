package org.jeuxdemots.model.graph;

import org.apache.commons.lang3.mutable.MutableInt;
import org.jeuxdemots.model.api.graph.JDMRelationType;

import java.util.Objects;

public class DefaultJDMRelationType implements JDMRelationType {

    static final char SINGLE_QUOTE = '\'';
    private final MutableInt id;
    private final String name;
    private final String extendedName;
    private final String info;

    public DefaultJDMRelationType(final MutableInt id, final String name, final String extendedName, final String info) {
        this.id = id;
        this.name = (name == null) ? "" : name;
        this.extendedName = extendedName;
        this.info = info;
    }

    @Override
    public MutableInt getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getExtendedName() {
        return extendedName;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "DefaultJDMRelationType{" +
                "id=" + id +
                ", name='" + name + SINGLE_QUOTE +
                ", extendedName='" + extendedName + SINGLE_QUOTE +
                ", info='" + info + SINGLE_QUOTE +
                '}';
    }

    @SuppressWarnings("all")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultJDMRelationType that = (DefaultJDMRelationType) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getExtendedName(), that.getExtendedName()) &&
                Objects.equals(getInfo(), that.getInfo());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getName(), getExtendedName(), getInfo());
    }
}
