package se.sciion.quake2d.ai.behaviour.nodes;

import se.sciion.quake2d.ai.behaviour.BehaviourNode;
import se.sciion.quake2d.ai.behaviour.BehaviourStatus;

// Used for testing
public class NoOp extends BehaviourNode {

	private BehaviourStatus status;
	
	// Will always return status specified.
	public NoOp(BehaviourStatus status) {
		this.status = status;
	}
	
	@Override
	protected BehaviourStatus onUpdate() {
		return status;
	}

}
