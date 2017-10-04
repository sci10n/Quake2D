package se.sciion.quake2d.ai.behaviour;

import java.util.Arrays;
import java.util.List;

// Process each child in order. Fails if one fails.
public class SequenceNode extends CompositeNode{
	
	
	public SequenceNode() {
		super();
	}
	
	public SequenceNode(List<BehaviourNode> behaviours) {
		super(behaviours);
		currentChild = 0;

	}
	public SequenceNode(BehaviourNode ...behaviourNodes){
		super(Arrays.asList(behaviourNodes));
		currentChild = 0;

	}
	
	@Override
	protected void onEnter() {
		currentChild = 0;
		status = BehaviourStatus.RUNNING;
		super.onEnter();
	}
	
	@Override
	protected BehaviourStatus onUpdate() {
		if (currentChild < children.size() && !children.isEmpty()) {
			status = children.get(currentChild).tick();
			if(status == BehaviourStatus.SUCCESS){
				++currentChild;
				return onUpdate();
			}
		}
		return status;
	}

}
