package org.jeuxdemots.model.api.lexical;

import org.apache.commons.lang3.tuple.Pair;
import org.jeuxdemots.model.api.graph.JDMNode;

import java.util.Collection;
import java.util.List;

public interface JDMLexicalEntry extends JDMNode {
    String getPosTag();

    List<String> getMorphologicalInformation();

    List<JDMLexicalSense> getSenses();

    List<Pair<JDMLexicalEntry, Double>> getSentiment();

    Collection<JDMLexicalRelation> getLexicalRelations();

    JDMPolarity getPolarity();

    String getDomain();
}
