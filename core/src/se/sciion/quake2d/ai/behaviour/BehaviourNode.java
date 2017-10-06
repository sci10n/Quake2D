package se.sciion.quake2d.ai.behaviour;

import guru.nidi.graphviz.model.Node;

public abstract class BehaviourNode{
    // Since we haven't traversed this branch of the tree yet, we
    // set the current status of these nodes to become undefined.
    protected BehaviourStatus status = BehaviourStatus.UNDEFINED;
   
    // When we traverse some behaviour in the tree, and it hasn't
    // been running yet, we enter this behaviour. If we have been
    // running, but just recently left it, the we left behaviour.
    // If otherwise, we just update the behaviour for traversing.

    protected void onEnter() {
    }

    protected abstract BehaviourStatus onUpdate();

    protected void onLeave() {
    }

    public BehaviourStatus tick() {
        if (status != BehaviourStatus.RUNNING) onEnter();
        status = onUpdate(); // Run and update behaviour.
        if (status != BehaviourStatus.RUNNING) onLeave();
        return status;
    }

    public abstract Node toDotNode();
    
     
    public String getColor() {
    	switch (status) {
    	case RUNNING: return "f3b61f";
    	case SUCCESS: return "7dd181";
    	case FAILURE: return "e26d5a";
    	case UNDEFINED: return "a2abb5";
    	default: return "ffffff";
    	}
    }
}
