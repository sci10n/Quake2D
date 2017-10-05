package se.sciion.quake2d.ai.behaviour;

import static guru.nidi.graphviz.model.Factory.node;

import guru.nidi.graphviz.attribute.RankDir;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;

public class SucceederNode extends DecoratorNode {

	private static int succeederId = 0;
	
	public SucceederNode(BehaviourNode behaviour) {
		super(behaviour);
	}

	@Override
	protected void onEnter() {
		status = BehaviourStatus.RUNNING;
	}
	
	@Override
	protected BehaviourStatus onUpdate() {
		status = child.tick();
		if(child.status == BehaviourStatus.SUCCESS) {
			status = BehaviourStatus.SUCCESS;
		} else if(child.status == BehaviourStatus.FAILURE) {
			status = BehaviourStatus.SUCCESS;
		}
		return status;
	}

	@Override
	public Node toDot() {
		Node node = node("Succeeder" + succeederId++).with(Shape.RECTANGLE).with(Label.of("Succeeder"));
		node = node.link(child.toDot());
		return node;
	}
	
}
