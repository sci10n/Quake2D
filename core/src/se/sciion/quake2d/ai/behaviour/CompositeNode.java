package se.sciion.quake2d.ai.behaviour;

import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.level.Entity;

public abstract class CompositeNode extends BehaviourNode {
	protected Array<BehaviourNode> children; // Sibling children.
	protected int currentChild; // Current index

	public CompositeNode(){
		children = new Array<BehaviourNode>();
		currentChild = 0;
	}
	
	public CompositeNode(Array<BehaviourNode> nodes){
		children = nodes;
		for(BehaviourNode n : children) {
			n.setParent(this);
		}
		currentChild = 0;
	}
	
	public CompositeNode(BehaviourNode... nodes) {
		children = new Array<BehaviourNode>(nodes);
		for (BehaviourNode n : children) {
			n.setParent(this);
		}
		currentChild = 0;
	}

	@Override
	public void mutate(float chance) {
		for (BehaviourNode n : children) {
			n.mutate(chance);
		}
	}

	public void addChild(BehaviourNode node) {
		if (currentChild == 0) // Prevent modifying during execution.
		{
			children.add(node);
			node.setParent(this);
		}
	}

	public void removeChild(BehaviourNode node) {
		if (currentChild == 0) // Prevent modifying during execution.
			children.removeValue(node, true);
	}
	
	public void replaceChild(BehaviourNode child, BehaviourNode replacement){
		int indexOf = children.indexOf(child, true);
		children.set(indexOf, replacement);
		replacement.setParent(this);
		replacement.setOwner(entityOwner);
	}
	
	@Override
	public void flatten(Array<BehaviourNode> nodes) {
		nodes.add(this);
		for (BehaviourNode n : children) {
			n.flatten(nodes);
		}
	}
	
	@Override
	public void setOwner(Entity parent) {
		this.entityOwner = parent;
		for (BehaviourNode n : children) {
			n.setOwner(parent);
		}
	}
	
}
