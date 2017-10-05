package se.sciion.quake2d.ai.behaviour;

import static guru.nidi.graphviz.model.Factory.node;

import java.util.Arrays;
import java.util.List;

import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.RankDir;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;

// Process each child in order. Fails if one fails.
public class SequenceNode extends CompositeNode{
	
	private static int sequenceId = 0;
	
	public SequenceNode() {
		super();
	}
	
	public SequenceNode(List<BehaviourNode> behaviours) {
		super(behaviours);
		currentChild = 0;

	}
	public SequenceNode(BehaviourNode ...behaviourNodes){
		super(Arrays.asList(behaviourNodes));
		currentChild = 0;

	}
	
	@Override
	protected void onEnter() {
		currentChild = 0;
		status = BehaviourStatus.RUNNING;
		super.onEnter();
	}
	
	@Override
	protected BehaviourStatus onUpdate() {
		if (currentChild < children.size() && !children.isEmpty()) {
			status = children.get(currentChild).tick();
			if(status == BehaviourStatus.SUCCESS){
				++currentChild;
				return onUpdate();
			}
		}
		return status;
	}

	public Node toDot() {
		Node node = node("Sequence" + sequenceId++).with(Shape.RECTANGLE).with(Label.of("Sequence"));
		for(BehaviourNode c: children) {
			node = node.link(c.toDot());
		}
		
		return node;
	}

}
