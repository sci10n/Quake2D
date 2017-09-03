package se.sciion.quake2d.ai.behaviour;

public class BehaviourTree {
    protected BehaviourNode root;

    public BehaviourStatus tick() {
        return root.tick();
    }
}
