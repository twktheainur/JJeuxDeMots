package org.jeuxdemots.model.api.lexical;

import org.jeuxdemots.model.api.graph.JDMNode;
import org.jeuxdemots.model.api.graph.JDMRelation;
import org.jeuxdemots.model.api.graph.JDMRelationType;
import org.jeuxdemots.model.api.graph.JeuxDeMots;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface JDMLexicalAspect extends JeuxDeMots {
    static Map<JDMRelationType, List<JDMRelation>> relationListToRelationMap(final Collection<JDMRelation> relations) {
        return (relations.isEmpty())
                ? Collections.emptyMap()
                : relations
                .stream()
                .filter(Objects::nonNull)
                .filter(jdmRelation -> jdmRelation.getType() != null)
                .collect(Collectors.groupingBy(JDMRelation::getType));
    }

    void forEachLexicalEntry(Consumer<JDMLexicalEntry> consumer);

    Optional<JDMLexicalEntry> getLexicalEntry(int id);

    Optional<JDMLexicalSense> getLexicalSense(int id);

    Optional<JDMLexicalEntry> getLexicalEntry(JDMNode node);

    Optional<JDMLexicalSense> getLexicalSense(JDMNode node);

    Optional<JDMLexicalEntry> getLexicalEntry(String name);


}
