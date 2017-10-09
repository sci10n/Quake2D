package se.sciion.quake2d.ai.behaviour;

import static guru.nidi.graphviz.model.Factory.node;

import org.apache.bcel.generic.RET;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;

public class ParallelNode extends CompositeNode {

	private int threshold;

	public ParallelNode(int threshold, BehaviourNode... nodes) {
		super(nodes);
		this.threshold = threshold;
	}

	@Override
	protected void onEnter() {
		setStatus(BehaviourStatus.RUNNING);
		super.onEnter();
	}

	@Override
	protected BehaviourStatus onUpdate() {
		int numSuccess = 0;
		int numFailures = 0;

		for (BehaviourNode n : children) {
			BehaviourStatus status = n.tick();
			if (status == BehaviourStatus.SUCCESS) {
				numSuccess++;
			} else if (status == BehaviourStatus.FAILURE) {
				numFailures++;
			}
		}

		if (numSuccess >= threshold) {
			setStatus(BehaviourStatus.SUCCESS);
		} else if (numFailures > children.size() - numSuccess) {
			setStatus(BehaviourStatus.FAILURE);
		}
		return getStatus();
	}

	@Override
	public Node toDotNode() {
		Node sequence = node("Parallel" + getNext())
				.with(Shape.RECTANGLE)
				.with(Style.FILLED, Color.rgb(getColor()).fill(),
						Color.BLACK.radial()).with(Label.of("Parallel"))
				.with(Rank.SAME);

		for (BehaviourNode child : children)
			sequence = sequence.link(child.toDotNode());

		return sequence;
	}
}
