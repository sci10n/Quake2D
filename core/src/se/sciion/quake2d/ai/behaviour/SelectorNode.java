package se.sciion.quake2d.ai.behaviour;

import java.util.Arrays;
import java.util.List;

/**
 * Continue processing until ail children are attempted or one succeeds.
 * 
 * @author sciion
 *
 */
public class SelectorNode extends CompositeNode {

	public SelectorNode() {
		super();
	}

	public SelectorNode(List<BehaviourNode> behaviours) {
		super(behaviours);
		currentChild = 0;
	}

	public SelectorNode(BehaviourNode... behaviourNodes) {
		super(Arrays.asList(behaviourNodes));
		currentChild = 0;

	}

	@Override
	protected void onEnter() {
		super.onEnter();
		status = BehaviourStatus.RUNNING;
		currentChild = 0;
	}

	@Override
	protected BehaviourStatus onUpdate() {
		if (currentChild < children.size() && !children.isEmpty()) {
			status = children.get(currentChild).tick();
			if (status == BehaviourStatus.FAILURE) {
				if (currentChild < children.size()) {
					++currentChild;
					return onUpdate();
				}
			}
		}
		return status;
	}

}
