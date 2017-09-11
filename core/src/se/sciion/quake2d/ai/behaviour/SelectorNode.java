package se.sciion.quake2d.ai.behaviour;

import java.util.List;

/**
 * COntinue processing until ail children are attempted or one succeeds.
 * @author sciion
 *
 */
public class SelectorNode extends CompositeBehaviour{

	public SelectorNode() {
		super();
	}
	
	public SelectorNode(List<BehaviourNode> behaviours) {
		super(behaviours);
		currentChild = 0;
	}

	@Override
	protected BehaviourStatus onUpdate() {
		switch(children.get(currentChild).onUpdate() ){
		case FAILURE:
			if(currentChild < children.size()) {
				++currentChild;
				return onUpdate();
			}
			return BehaviourStatus.FAILURE;
		case SUCCESS:
			return BehaviourStatus.SUCCESS;
		case RUNNING:
			return BehaviourStatus.RUNNING;
		case UNDEFINED:
			return BehaviourStatus.UNDEFINED;
		}
		return BehaviourStatus.UNDEFINED;
	}

}
