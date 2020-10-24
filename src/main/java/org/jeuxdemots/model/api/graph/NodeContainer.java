package org.jeuxdemots.model.api.graph;

import java.util.Iterator;
import java.util.List;

public interface NodeContainer extends List<JDMNode> {
    Iterator<JDMNode> iteratorByType(NodeType type);
    Iterable<JDMNode> typedIterable(NodeType type);
}
