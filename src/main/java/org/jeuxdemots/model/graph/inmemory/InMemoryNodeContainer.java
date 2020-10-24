package org.jeuxdemots.model.graph.inmemory;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jeuxdemots.model.api.graph.JDMNode;
import org.jeuxdemots.model.api.graph.NodeContainer;
import org.jeuxdemots.model.api.graph.NodeType;
import org.jeuxdemots.model.graph.DefaultJDMNode;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class InMemoryNodeContainer implements NodeContainer {

    private final NodeType[] nodeTypes;
    private final String[] nodeNames;
    private final MutableDouble[] nodeWeights;

    private final Map<NodeType, List<MutableInt>> nodeTypeIndex;

    InMemoryNodeContainer(final int numberOfNodes) {
        nodeTypes = new NodeType[numberOfNodes];
        nodeNames = new String[numberOfNodes];
        nodeWeights = new MutableDouble[numberOfNodes];
        nodeTypeIndex = new EnumMap<>(NodeType.class);
        for (final NodeType type : NodeType.values()) {
            nodeTypeIndex.put(type, new ArrayList<>());
        }
    }

    @Override
    public int size() {
        return nodeTypes.length;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(final Object o) {
        final JDMNode node = (JDMNode) o;
        final int id = node.getId().getValue();
        return ((id - 1) < size()) && (nodeNames[id - 1] != null);
    }


    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return null;
    }

    @SuppressWarnings("FeatureEnvy")
    @Override
    public boolean add(final JDMNode jdmNode) {
        final MutableInt id = jdmNode.getId();
        final int idVal = jdmNode.getId().getValue();
        if ((idVal - 1) < size()) {
            final NodeType nodeType = jdmNode.getNodeType();
            nodeTypes[idVal -1] = nodeType;
            nodeNames[idVal-1] = jdmNode.getName();
            nodeWeights[idVal-1] = jdmNode.getWeight();
            nodeTypeIndex.get(nodeType).add(id);
        }
        return (idVal - 1) < size();
    }

    /**
     * This is an add-only container. Cannot remove anything.
     *
     * @param o Object to remove
     * @return false
     */
    @Override
    public boolean remove(final Object o) {
        return false;
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        boolean result = true;
        for (final Object o : c) {
            if (result) {
                result = contains(o);
            }
        }
        return result;
    }

    @Override
    public boolean addAll(final Collection<? extends JDMNode> c) {
        boolean result = true;
        for (final JDMNode node : c) {
            if (result) {
                result = add(node);
            }
        }
        return result;
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends JDMNode> c) {
        return false;
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
    }

    @Override
    public JDMNode get(final int index) {
        JDMNode result = null;
        if (index < size()) {
            result = new DefaultJDMNode(new MutableInt(index+1), nodeNames[index], nodeTypes[index].getCode(), nodeWeights[index]);
        }
        return result;
    }

    @Override
    public JDMNode set(final int index, final JDMNode element) {
        JDMNode node = null;
        if (add(element)) {
            node = element;
        }
        return node;
    }

    @Override
    public void add(final int index, final JDMNode element) {
        add(element);
    }

    @Override
    public JDMNode remove(final int index) {
        return null;
    }

    @Override
    public int indexOf(final Object o) {
        int result = -1;
        final MutableInt nodeId = ((JDMNode) o).getId();
        if ((nodeId.intValue() - 1) < size()) {
            result = (nodeId.intValue() - 1);
        }
        return result;
    }

    @Override
    public int lastIndexOf(final Object o) {
        return indexOf(o);
    }

    @Override
    public ListIterator<JDMNode> listIterator() {
        return null;
    }

    @Override
    public ListIterator<JDMNode> listIterator(final int index) {
        return null;
    }

    @Override
    public List<JDMNode> subList(final int fromIndex, final int toIndex) {
        return Collections.emptyList();
    }

    @Override
    public Iterator<JDMNode> iterator() {
        return new JDMNodeIterator(this);
    }

    @Override
    public Iterator<JDMNode> iteratorByType(final NodeType type) {
        return new JDMNodeIterator(this, nodeTypeIndex, type);
    }

    @Override
    public Iterable<JDMNode> typedIterable(final NodeType type) {
        return new JDMNodeIterable(this, nodeTypeIndex, type);
    }

    private static class JDMNodeIterator implements Iterator<JDMNode> {
        int currentPosition;
        final List<JDMNode> container;
        NodeType nodeType;
        private final Map<NodeType, List<MutableInt>> nodeTypeIndex;

        JDMNodeIterator(final List<JDMNode> container) {
            this.container = Collections.unmodifiableList(container);
            currentPosition = 0;
            nodeType = NodeType.UNDEFINED;
            nodeTypeIndex = new EnumMap<>(NodeType.class);
        }

        JDMNodeIterator(final List<JDMNode> container, final Map<NodeType, List<MutableInt>> nodeTypeIndex, final NodeType nodeType) {
            this.container = Collections.unmodifiableList(container);
            currentPosition = 0;
            this.nodeType = nodeType;
            this.nodeTypeIndex = Collections.unmodifiableMap(nodeTypeIndex);
        }

        @Override
        public boolean hasNext() {
            final boolean result;
            if (nodeType == NodeType.UNDEFINED) {
                result = (currentPosition + 1) < container.size();
            } else {
                result = (currentPosition + 1) < nodeTypeIndex.get(nodeType).size();
            }
            return result;
        }

        @Override
        public JDMNode next() {
            JDMNode result = null;
            if ((nodeType == NodeType.UNDEFINED)) {
                if(currentPosition < container.size()) {
                    result = container.get(currentPosition);
                    currentPosition++;
                }
            } else {
                final List<MutableInt> typedNodes = nodeTypeIndex.get(nodeType);
                if(currentPosition < typedNodes.size()){
                    result = container.get(typedNodes.get(currentPosition).intValue()-1);
                    currentPosition++;
                }
            }
            return result;
        }
    }

    private static class JDMNodeIterable implements Iterable<JDMNode> {
        private final NodeType type;
        private final List<JDMNode> container;
        private final Map<NodeType, List<MutableInt>> nodeTypeIndex;

        JDMNodeIterable(final List<JDMNode> container, final Map<NodeType, List<MutableInt>> nodeTypeIndex, final NodeType type) {
            this.type = type;
            this.container = Collections.unmodifiableList(container);
            this.nodeTypeIndex = Collections.unmodifiableMap(nodeTypeIndex);
        }

        @Override
        public Iterator<JDMNode> iterator() {
            return new JDMNodeIterator(container, nodeTypeIndex, type);
        }
    }
}
