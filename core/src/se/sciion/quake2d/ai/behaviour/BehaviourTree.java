package se.sciion.quake2d.ai.behaviour;
import static guru.nidi.graphviz.model.Factory.graph;
import guru.nidi.graphviz.model.Graph;
import se.sciion.quake2d.ai.behaviour.visualizer.BehaviorListener;
import se.sciion.quake2d.level.Entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class BehaviourTree implements BehaviorListener{
	
	// Node has changed state since last we checked;
	private boolean dirty = true;
	
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
	
	public void setOwner(Entity owner){
		root.setOwner(owner);
	}
	
	
	public void crossover(BehaviourTree tree, float chance){
		
		if(!MathUtils.randomBoolean(chance)){
			return;
		}
		
		Array<BehaviourNode> nodes1 = new Array<BehaviourNode>();
		root.flatten(nodes1);
		BehaviourNode swapNode1 = nodes1.random();
		
		Array<BehaviourNode> nodes2 = new Array<BehaviourNode>();
		tree.root.flatten(nodes2);
		BehaviourNode swapNode2 = nodes2.random();
	
		BehaviourNode parent1 = swapNode1.getParent();
		BehaviourNode parent2 = swapNode2.getParent();
		if(parent1 != null){
			CompositeNode parent = (CompositeNode) parent1;
			parent.replaceChild(swapNode1, swapNode2);
		}
		else {
			root = swapNode2;
			root.setOwner(swapNode1.entityOwner);
			root.setParent(null);
		}
		
		if(parent2 != null){
			CompositeNode parent = (CompositeNode) parent2;
			parent.replaceChild(swapNode2, swapNode1);
		}
		else {
			tree.root = swapNode1;
			tree.root.setOwner(swapNode2.entityOwner);
			tree.root.setParent(null);
		}
		
		dirty = true;
		tree.dirty = true;
	}
	
	
}
