package org.jeuxdemots.model.graph;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jeuxdemots.model.api.graph.JDMNode;
import org.jeuxdemots.model.api.graph.NodeType;

import java.util.regex.Pattern;

public class DefaultJDMNode implements JDMNode {
    private static final Pattern UNESCAPE_APOSTROPHE = Pattern.compile("\\\\\'");
    private static final Pattern UNESCAPE_QUOTES = Pattern.compile("\\\\\"");
    private final MutableInt id;
    private final String name;
    private final MutableDouble weight;
    private final NodeType nodeType;

    public DefaultJDMNode(final MutableInt id, final CharSequence name, final int nodeType, final MutableDouble weight) {
        this.id = id;
        this.name = UNESCAPE_QUOTES.matcher(UNESCAPE_APOSTROPHE.matcher(name).replaceAll("\"")).replaceAll("\"");
        this.weight = weight;
        this.nodeType = NodeType.fromCode(nodeType);
    }

    protected DefaultJDMNode(final MutableInt id, final String name, final NodeType nodeType, final MutableDouble weight) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.nodeType = nodeType;
    }

    @Override
    public MutableInt getId() {
        return id;
    }

    @Override
    public int getIdInt() {
        return id.intValue();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MutableDouble getWeight() {
        return weight;
    }

    @Override
    public void incrementWeight(final double value) {
        weight.add(value);
    }

    @Override
    public void decrementWeight(final double value) {
        weight.subtract(value);
    }

    @Override
    public NodeType getNodeType() {
        return nodeType;
    }

    @Override
    public String toString() {
        return "Node[" +
                id +
                " | " + name +
                "(" + weight + ")" +
                " T=" + nodeType + "]";
    }
}
