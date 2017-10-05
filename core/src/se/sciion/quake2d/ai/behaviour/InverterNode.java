package se.sciion.quake2d.ai.behaviour;

import static guru.nidi.graphviz.model.Factory.*;

import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;

public class InverterNode extends DecoratorNode {
	private static int nodeID = 0;
	
	public InverterNode(BehaviourNode behaviour) {
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
			status = BehaviourStatus.FAILURE;
		}
		else if(child.status == BehaviourStatus.FAILURE) {
			status = BehaviourStatus.SUCCESS;
		}
		
		return status;
	}

	@Override
	public Node toDot() {
		Node node = node("Inverter" + nodeID++).with(Shape.RECTANGLE).with(Label.of("Inverter"));
		node = node.link(child.toDot());
		return node;
	}
	
}
