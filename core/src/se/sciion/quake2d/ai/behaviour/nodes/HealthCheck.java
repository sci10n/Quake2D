package se.sciion.quake2d.ai.behaviour.nodes;

import org.omg.PortableInterceptor.SUCCESSFUL;

import se.sciion.quake2d.ai.behaviour.BehaviourNode;
import se.sciion.quake2d.ai.behaviour.BehaviourStatus;
import se.sciion.quake2d.level.components.HealthComponent;

public class HealthCheck extends BehaviourNode{

	private HealthComponent health;
	private float ratio;
	
	public HealthCheck(HealthComponent health, float ratio) {
		this.health = health;
		this.ratio = ratio;
	}
	
	@Override
	protected void onEnter() {
		status = BehaviourStatus.RUNNING;
	}
	
	@Override
	protected BehaviourStatus onUpdate() {
		
		if(health != null && (health.health/(float)health.MAX_HEALTH) >= ratio) {
			status = BehaviourStatus.SUCCESS;
		}
		else {
			status = BehaviourStatus.FAILURE;
			System.out.println("Not ok " + (health.health/(float)health.MAX_HEALTH));
		}
		return status;
	}

}
