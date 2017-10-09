package se.sciion.quake2d.ai.behaviour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.sciion.quake2d.level.Entity;

public abstract class CompositeNode extends BehaviourNode {
    protected List<BehaviourNode> children; // Sibling children.
    protected int currentChild;	// Current index
    
    public CompositeNode(BehaviourNode ... nodes){
    	children = Arrays.asList(nodes);
    	currentChild = 0;
    }

    
    // Default constructor if we want to dynamically add behaviours.
    public CompositeNode() {
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
    
    @Override
    public void setParent(Entity parent) {
    	this.parent = parent;
    	for(BehaviourNode n: children){
    		n.setParent(parent);
    	}
    }
}
