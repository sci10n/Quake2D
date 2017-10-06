package se.sciion.quake2d.ai.behaviour;

import java.util.ArrayList;
import java.util.List;

import se.sciion.quake2d.ai.behaviour.visualizer.BehaviorListener;

public abstract class CompositeNode extends BehaviourNode implements BehaviorListener {
    protected List<BehaviourNode> children; // Sibling children.
    protected int currentChild;	// Current index

    // Behaviour that will somehow do stuff to other behaviours.
    public CompositeNode(List<BehaviourNode> behaviours)  {
        children = behaviours;
        currentChild = 0;
        
        for(BehaviourNode n: children){
        	n.addListener(this);
        }
    }

    // Default constructor if we want to dynamically add behaviours.
    public CompositeNode() {
        children = new ArrayList<BehaviourNode>();
        currentChild = 0;
    }

    public void addChild(BehaviourNode node) {
        if(currentChild == 0)	// Prevent modifying during execution.
        {
        	node.addListener(this);
            children.add(node);
        }
    }

    public void removeChild(BehaviourNode node){
        if(currentChild == 0)	// Prevent modifying during execution.
        {
        	node.removeListener(this);
            children.remove(node);
        }
    }
    
    @Override
    public void onStatusChanged(BehaviourNode node) {
    	noitfyListeners();
    }
}
