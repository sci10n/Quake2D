package se.sciion.quake2d.ai.behaviour;


public abstract class DecoratorNode extends CompositeNode {

	public DecoratorNode(){
		super();
	}
	
    // Assign a node or sub-tree that is to be modified.
    public DecoratorNode(BehaviourNode behaviour) {
    	super(behaviour);
    }
    
}
