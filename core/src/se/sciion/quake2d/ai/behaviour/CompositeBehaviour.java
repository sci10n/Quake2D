package se.sciion.quake2d.ai.behaviour;

import java.util.ArrayList;
import java.util.List;

public abstract class CompositeBehaviour extends BehaviourNode {
    protected List<BehaviourNode> children; // Sibling children.
    protected int currentChild;	// Current index
    
    // Behaviour that will somehow do stuff to other behaviours.
    public CompositeBehaviour(List<BehaviourNode> behaviours)  {
        children = behaviours;
        currentChild = 0;
    }
    
    // Default constructor if we want to dynamically add behaviours.
    public CompositeBehaviour() {
    	children = new ArrayList<BehaviourNode>();
    	currentChild = 0;
    }
    
    public void addChild(BehaviourNode node) {
    	if(currentChild == 0)	// Prevent modifying during execution.
    		children.add(node);
    }
    
    public void removeChild(BehaviourNode node){
    	if(currentChild == 0)	// Prevent modifying during execution.
    		children.remove(node);
    }
}
