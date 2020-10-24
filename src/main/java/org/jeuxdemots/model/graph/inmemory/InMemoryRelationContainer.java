package org.jeuxdemots.model.graph.inmemory;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jeuxdemots.model.api.graph.JDMNode;
import org.jeuxdemots.model.api.graph.JDMRelation;
import org.jeuxdemots.model.api.graph.JDMRelationType;
import org.jeuxdemots.model.api.graph.RelationContainer;
import org.jeuxdemots.model.graph.DefaultJDMRelation;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("ConstantConditions")
public class InMemoryRelationContainer implements RelationContainer {

    private final HashMap<MutableInt,JDMRelationType> relationTypeInventory;
    private final HashMap<String,JDMRelationType> relationTypeNameIndex;
    private final JDMRelationType[] relationTypes;
    private final MutableInt[] sourceNodes;
    private final MutableInt[] targetNodes;
    private final MutableDouble[] relationWeights;

    private final List<List<MutableInt>> outgoingRelationIndex;
    private final List<List<MutableInt>> incomingRelationIndex;

    InMemoryRelationContainer(final Integer numberOfRelations, final Integer numberOfNodes) {
        relationTypes = new JDMRelationType[numberOfRelations];
        sourceNodes = new MutableInt[numberOfRelations];
        targetNodes = new MutableInt[numberOfRelations];
        relationWeights = new MutableDouble[numberOfRelations];
        relationTypeInventory = new HashMap<>();
        relationTypeNameIndex = new HashMap<>();

        outgoingRelationIndex = new ArrayList<>(numberOfNodes);
        incomingRelationIndex = new ArrayList<>(numberOfNodes);
        for(int i=0;i<numberOfNodes;i++){
            outgoingRelationIndex.add(new ArrayList<>());
            incomingRelationIndex.add(new ArrayList<>());
        }
    }

    @Override
    public int size() {
        return relationTypes.length;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(final Object o) {
        final JDMRelation node = (JDMRelation) o;
        final MutableInt id = node.getId();
        final int idVal = id.getValue();
        return (idVal < size()) && (relationTypes[idVal-1] != null);
    }

    @Override
    public Iterator<JDMRelation> iterator() {
        return new JDMRelationIterator(this);
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
    public boolean add(final JDMRelation jdmRelation) {
        final MutableInt id = jdmRelation.getId();
        final int idVal = id.getValue();
        if (((idVal - 1) < size()) && ((jdmRelation.getSourceId().intValue() - 1) < sourceNodes.length) && ((jdmRelation.getTargetId().intValue() - 1) < targetNodes.length) &&
                (jdmRelation.getSourceId().intValue() > 0) && (jdmRelation.getTargetId().intValue() > 0)) {
            relationTypes[idVal-1] = jdmRelation.getType();
            sourceNodes[idVal-1] = jdmRelation.getSourceId();
            targetNodes[idVal-1] = jdmRelation.getTargetId();
            relationWeights[idVal-1] = jdmRelation.getWeight();
            incomingRelationIndex.get(targetNodes[idVal-1].intValue()-1).add(id);
            outgoingRelationIndex.get(sourceNodes[idVal-1].intValue()-1).add(id);
        }
        return ((idVal - 1) < size()) && ((jdmRelation.getSourceId().intValue() - 1) < sourceNodes.length) && ((jdmRelation.getTargetId().intValue() - 1) < targetNodes.length);
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
    public boolean addAll(final Collection<? extends JDMRelation> c) {
        boolean result = true;
        for (final JDMRelation relation : c) {
            if (result) {
                result = add(relation);
            }
        }
        return result;
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends JDMRelation> c) {
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
    public JDMRelation get(final int index) {
        JDMRelation result = null;
        if ((index < size()) && (sourceNodes[index] != null) && (targetNodes[index] != null)) {
            result = new DefaultJDMRelation(new MutableInt(index+1), sourceNodes[index], targetNodes[index], relationTypes[index], relationWeights[index]);
        }
        return result;
    }

    @Override
    public JDMRelation set(final int index, final JDMRelation element) {
        JDMRelation relation = null;
        if (add(element)) {
            relation = element;
        }
        return relation;
    }

    @Override
    public void add(final int index, final JDMRelation element) {
        add(element);
    }

    @Override
    public JDMRelation remove(final int index) {
        return null;
    }

    @Override
    public int indexOf(final Object o) {
        int result = -1;
        final int nodeId = ((JDMNode) o).getId().intValue();
        if (nodeId < size()) {
            result = nodeId;
        }
        return result;
    }

    @Override
    public int lastIndexOf(final Object o) {
        return indexOf(o);
    }

    @Override
    public ListIterator<JDMRelation> listIterator() {
        return null;
    }

    @Override
    public ListIterator<JDMRelation> listIterator(final int index) {
        return null;
    }

    @Override
    public List<JDMRelation> subList(final int fromIndex, final int toIndex) {
        return Collections.emptyList();
    }

    @Override
    public List<JDMRelation> outgoingRelations(final JDMNode node) {
        final List<MutableInt> relations = outgoingRelationIndex.get(node.getId().intValue()-1);
        final Stream<MutableInt> stream = relations.stream();
        return stream.map((MutableInt index) -> get(index.intValue())).collect(Collectors.toList());
    }

    @Override
    public List<JDMRelation> incomingRelations(final JDMNode node) {
        final List<MutableInt> relations = incomingRelationIndex.get(node.getId().intValue()-1);
        final Stream<MutableInt> stream = relations.stream();
        return stream.map((MutableInt index) -> get(index.intValue())).collect(Collectors.toList());
    }

    @Override
    public JDMRelationType findType(final int id) {
        return relationTypeInventory.get(new MutableInt(id));
    }

    @Override
    public JDMRelationType findType(final String name) {
        return relationTypeNameIndex.get(name);
    }



    @Override
    public void addRelationType(final JDMRelationType relationType) {
        relationTypeInventory.put(relationType.getId(), relationType);
        relationTypeNameIndex.put(relationType.getName(),relationType);
    }

    private static class JDMRelationIterator implements Iterator<JDMRelation> {
        int currentPosition;
        final List<JDMRelation> container;

        JDMRelationIterator(final List<JDMRelation> container) {
            this.container = Collections.unmodifiableList(container);
            currentPosition = 0;
        }

        @Override
        public boolean hasNext() {
            return currentPosition < container.size();
        }

        @Override
        public JDMRelation next() {
            JDMRelation result = null;
            if ((currentPosition + 1) < container.size()) {
                currentPosition++;
                result = container.get(currentPosition);
            }
            return result;
        }
    }
}
