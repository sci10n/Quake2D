package se.sciion.quake2d.ai.behaviour.nodes;

import static guru.nidi.graphviz.model.Factory.node;

import org.omg.PortableInterceptor.SUCCESSFUL;

import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;
import se.sciion.quake2d.ai.behaviour.BehaviourNode;
import se.sciion.quake2d.ai.behaviour.BehaviourStatus;
import se.sciion.quake2d.level.components.HealthComponent;

public class HealthCheck extends BehaviourNode{
	private static int healthId = 0;
	
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
		}
		return status;
	}
	
	@Override
	public Node toDot() {
		Node node = node("Health" + healthId++).with(Label.of("Health >" + ratio + "?"));

		return node;
	}

}
