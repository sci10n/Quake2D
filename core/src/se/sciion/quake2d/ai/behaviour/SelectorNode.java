package se.sciion.quake2d.ai.behaviour;

import java.util.Arrays;
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
	public SelectorNode(BehaviourNode ...behaviourNodes){
		super(Arrays.asList(behaviourNodes));
		currentChild = 0;

	}
	@Override
	protected BehaviourStatus onUpdate() {
		status = children.get(currentChild).tick();
		if(status == BehaviourStatus.FAILURE){
			if(currentChild < children.size()) {
				++currentChild;
				return onUpdate();
			}
		}
		return status;
	}

}
