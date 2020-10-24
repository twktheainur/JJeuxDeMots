package org.jeuxdemots.model.api.graph;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;

public interface JDMNode {
    /**
     * The numerical id of the node as a mutable integer
     * @return numerical id of the node as a mutable integer
     */
    MutableInt getId();

    /**
     * The numerical value of the node id as an int
     * @return The numerical value of the node id as an int
     */
    int getIdInt();

    /**
     * The name associated to the node
     * @return name associated to the node
     */
    String getName();

    /**
     * Weight associated to the node.
     * @return Weight associated to the node.
     */
    MutableDouble getWeight();

    void incrementWeight(double value);

    void decrementWeight(double value);


    /**
     * Type of the node
     * @return Type of the node.
     */
    NodeType getNodeType();
}
