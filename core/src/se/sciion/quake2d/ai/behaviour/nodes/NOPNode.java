package se.sciion.quake2d.ai.behaviour.nodes;

import static guru.nidi.graphviz.model.Factory.node;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;
import se.sciion.quake2d.ai.behaviour.BehaviourNode;
import se.sciion.quake2d.ai.behaviour.BehaviourStatus;
import se.sciion.quake2d.enums.ComponentTypes;

public class NOPNode extends BehaviourNode {

	public NOPNode() {
	}

	@Override
	protected void onEnter() {
		setStatus(BehaviourStatus.RUNNING);
	}

	@Override
	protected BehaviourStatus onUpdate() {
		setStatus(BehaviourStatus.SUCCESS);
		return getStatus();
	}

	@Override
	public Node toDotNode() {
		return node("nop" + getNext())
		           .with(Shape.RECTANGLE)
		           .with(Style.FILLED, Color.rgb(getColor()).fill(), Color.BLACK.radial())
		           .with(Label.of("NOP"));
	}

	@Override
	public void mutate() {
	}

	@Override
	public BehaviourNode clone() {
		return new NOPNode();
	}
	
	@Override
	public BehaviourNode randomized() {
		// TODO Auto-generated method stub
		return new NOPNode();
	}
}

