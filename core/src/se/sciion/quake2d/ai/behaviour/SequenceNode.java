package se.sciion.quake2d.ai.behaviour;

import java.util.Arrays;
import java.util.List;

// Process each child in order. Fails if one fails.
public class SequenceNode extends CompositeBehaviour{
	
	
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
	protected BehaviourStatus onUpdate() {
		status = children.get(currentChild).tick();
		if(status == BehaviourStatus.SUCCESS){
			++currentChild;
			System.out.println(status);

			if(currentChild < children.size()) {
				return onUpdate();
			}
		}
		
		return status;
	}

}
