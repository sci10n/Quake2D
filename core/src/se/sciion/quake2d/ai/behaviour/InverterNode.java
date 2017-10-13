package se.sciion.quake2d.ai.behaviour;

import static guru.nidi.graphviz.model.Factory.node;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class InverterNode extends DecoratorNode {

	public InverterNode() {
		super();
	}

	public InverterNode(BehaviourNode behaviour) {
		super(behaviour);
	}

	@Override
	protected void onEnter() {
		setStatus(BehaviourStatus.RUNNING);
	}

	@Override
	protected BehaviourStatus onUpdate() {
		BehaviourNode child = children.first();
		setStatus(child.tick());
		if (child.getStatus() == BehaviourStatus.SUCCESS) {
			setStatus(BehaviourStatus.FAILURE);
		} else if (child.getStatus() == BehaviourStatus.FAILURE) {
			setStatus(BehaviourStatus.SUCCESS);
		}

		return getStatus();
	}

	@Override
	public Node toDotNode() {
		BehaviourNode child = children.first();
		if (child == null) {
			return node("inverter" + getNext())
					.with(Shape.DIAMOND)
					.with(Style.FILLED, Color.rgb(getColor()).fill(),
							Color.BLACK.radial()).with(Label.of("Invert"));
		}

		return node("inverter" + getNext())
				.with(Shape.DIAMOND)
				.with(Style.FILLED, Color.rgb(getColor()).fill(),
						Color.BLACK.radial()).with(Label.of("Invert"))
				.link(child.toDotNode());
	}

	@Override
	public void mutate() {
		if (children.size == 0) {
			children.add(Trees.prototypes.random().clone());
		}
	}

	@Override
	public BehaviourNode clone() {
		InverterNode node = null;
		if (children.size > 0) {
			node = new InverterNode(children.first().clone());
		} else {
			node = new InverterNode();
		}
		return node;
	}

	@Override
	public BehaviourNode randomized() {
		return new InverterNode(Trees.prototypes.random().randomized());
	}

}
