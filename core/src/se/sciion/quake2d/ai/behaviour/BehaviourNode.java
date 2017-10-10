package se.sciion.quake2d.ai.behaviour;

import com.badlogic.gdx.utils.Array;

import guru.nidi.graphviz.model.Node;
import se.sciion.quake2d.ai.behaviour.visualizer.BehaviorListener;
import se.sciion.quake2d.level.Entity;

public abstract class BehaviourNode{
	protected static int nodeId = 0;
	
	protected static int getNext(){
		return nodeId++;
	}
	
	// Array to keep track of listeners
	private Array<BehaviorListener> listeners;
	// Since we haven't traversed this branch of the tree yet, we
    // set the current status of these nodes to become undefined.
    private BehaviourStatus status = BehaviourStatus.UNDEFINED;
    
    // Parent entity
	protected Entity entityOwner;

	private BehaviourNode parent;
	
    // When we traverse some behaviour in the tree, and it hasn't
    // been running yet, we enter this behaviour. If we have been
    // running, but just recently left it, the we left behaviour.
    // If otherwise, we just update the behaviour for traversing.

	public BehaviourNode() {
		listeners = new Array<BehaviorListener>();
	}
    
    protected void onEnter() {
    }

    protected abstract BehaviourStatus onUpdate();

    protected void onLeave() {
    }
    
    protected void noitfyListeners(){
    	for(BehaviorListener l: listeners){
    		l.onStatusChanged(this);
    	}
    }
    
    protected void setStatus(BehaviourStatus status) {
    	this.status = status;
    	noitfyListeners();
    }
    
    protected BehaviourStatus getStatus(){
    	return this.status;
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
    
    public void flatten(Array<BehaviourNode> nodes){
    	nodes.add(this);
    }
    
    public void addListener(BehaviorListener l){
    	listeners.add(l);
    }
    
    public void removeListener(BehaviorListener l){
    	listeners.removeValue(l, true);
    }
    
    public void setOwner(Entity parent) {
		this.entityOwner = parent;
	}

	public BehaviourNode getParent() {
		return parent;
	}

	public void setParent(BehaviourNode parent) {
		this.parent = parent;
	}
}
