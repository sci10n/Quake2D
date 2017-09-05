package se.sciion.quake2d.ai.behaviour;

import java.util.List;

public abstract class CompositeBehaviour extends BehaviourNode {
    protected List<BehaviourNode> children; // Sibling children.

    // Behaviour that will somehow do stuff to other behaviours.
    public CompositeBehaviour(List<BehaviourNode> behaviours)  {
        children = behaviours;
    }
}
