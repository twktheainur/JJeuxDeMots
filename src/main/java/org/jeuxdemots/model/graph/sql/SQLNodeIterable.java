package org.jeuxdemots.model.graph.sql;

import org.jeuxdemots.model.api.graph.JDMNode;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class SQLNodeIterable implements Iterable<JDMNode> {

    @Override
    public Iterator<JDMNode> iterator() {
        return new JDMNodeIterator();
    }

    @Override
    public void forEach(final Consumer<? super JDMNode> action) {

    }

    @Override
    public Spliterator<JDMNode> spliterator() {
        return null;
    }

    private static class JDMNodeIterator implements Iterator<JDMNode> {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public JDMNode next() {
            return null;
        }
    }
}
