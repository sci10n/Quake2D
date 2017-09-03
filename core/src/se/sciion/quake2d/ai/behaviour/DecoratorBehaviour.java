package se.sciion.quake2d.ai.behaviour;

public abstract class DecoratorBehaviour extends BehaviourNode {
    protected BehaviourNode child; // Behaviour to be decorated.

    // Assign a node or sub-tree that is to be modified.
    public DecoratorBehaviour(BehaviourNode behaviour) {
        child = behaviour;
    }
}
