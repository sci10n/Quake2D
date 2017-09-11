package se.sciion.quake2d.ai.behaviour;

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
}
