package se.sciion.quake2d.ai.behaviour;
import static guru.nidi.graphviz.model.Factory.graph;

import guru.nidi.graphviz.model.Graph;
import se.sciion.quake2d.ai.behaviour.visualizer.BehaviorListener;
import se.sciion.quake2d.level.Entity;

public class BehaviourTree implements BehaviorListener{
	
	// Node has changed state since last we checked;
	private boolean dirty = false;
	
    protected BehaviourNode root;

    // Wrapper for the BehaviourTree. It is usually
    // easier to using BehaviourTreeBuilder though.
    public BehaviourTree(BehaviourNode behaviour) {
        root = behaviour;
        root.addListener(this);
    }

    public BehaviourStatus tick() {
        return root.tick();
    }

    public Graph toDotGraph() {
        // Traverses the tree and produced a Graphviz traversable graph.
        return graph("BehaviourTree").directed().with(root.toDotNode());
    }

	@Override
	public void onStatusChanged(BehaviourNode node) {
		dirty = true;
	}
	
	public boolean isDirty(){
		boolean tmp = dirty;
		dirty = false;
		return tmp;
	}
	
	public void setParent(Entity parent){
		root.setParent(parent);
	}
	
}
