package se.sciion.quake2d.ai.behaviour;
import static guru.nidi.graphviz.model.Factory.*;

import guru.nidi.graphviz.model.Graph;

public class BehaviourTree {
    protected BehaviourNode root;

    // Wrapper for the BehaviourTree. It is usually
    // easier to using BehaviourTreeBuilder though.
    public BehaviourTree(BehaviourNode behaviour) {
        root = behaviour;
    }

    public BehaviourStatus tick() {
        return root.tick();
    }
    
    public Graph toDot() {
    	Graph g = graph("BehaviorTree").directed().with(root.toDot());
    	return g;
    }
}
