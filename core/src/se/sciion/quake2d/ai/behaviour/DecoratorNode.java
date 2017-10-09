package se.sciion.quake2d.ai.behaviour;

import se.sciion.quake2d.level.Entity;

public abstract class DecoratorNode extends BehaviourNode {
    protected BehaviourNode child; // Behaviour to be decorated.

    // Assign a node or sub-tree that is to be modified.
    public DecoratorNode(BehaviourNode behaviour) {
        child = behaviour;
    }
    
    @Override
    public void setParent(Entity parent) {
    	this.parent = parent;
    	child.setParent(parent);
    }
}
