package se.sciion.quake2d.ai.behaviour;

public class Inverter extends DecoratorBehaviour{

	public Inverter(BehaviourNode behaviour) {
		super(behaviour);
	}

	@Override
	protected void onEnter() {
		status = BehaviourStatus.RUNNING;
	}
	
	@Override
	protected BehaviourStatus onUpdate() {
		status = child.tick();
		if(child.status == BehaviourStatus.SUCCESS) {
			status = BehaviourStatus.FAILURE;
		}
		else if(child.status == BehaviourStatus.FAILURE) {
			status = BehaviourStatus.SUCCESS;
		}
		
		return status;
	}
	
}
