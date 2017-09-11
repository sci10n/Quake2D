package se.sciion.quake2d.ai.behaviour;

import java.util.List;

// Process each child in order. Fails if one fails.
public class SequenceNode extends CompositeBehaviour{
	
	
	public SequenceNode() {
		super();
	}
	
	public SequenceNode(List<BehaviourNode> behaviours) {
		super(behaviours);
	}

	@Override
	protected BehaviourStatus onUpdate() {
		switch(children.get(currentChild).onUpdate() ){
		case FAILURE:
			return BehaviourStatus.FAILURE; // We are done. No more evaluation.
		case SUCCESS:
			++currentChild;
			return onUpdate();	// Continue with the next child.
		case RUNNING:
			return BehaviourStatus.RUNNING;	// Still processing.
		case UNDEFINED:
			return BehaviourStatus.UNDEFINED;	// ???
		}
		return BehaviourStatus.UNDEFINED;
	}

}
