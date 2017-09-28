package se.sciion.quake2d.ai.behaviour.nodes;

import com.badlogic.gdx.math.Vector2;

import se.sciion.quake2d.ai.behaviour.BehaviourNode;
import se.sciion.quake2d.ai.behaviour.BehaviourStatus;
import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.components.PhysicsComponent;

public class Attack extends BehaviourNode{
	
	private Entity target;
	private BotInputComponent input;
	
	public Attack(Entity target, BotInputComponent input) {
		this.target = target;
		this.input = input;
	}
	
	@Override
	protected void onEnter() {
		super.onEnter();
		status = BehaviourStatus.RUNNING;
	}
	
	@Override
	protected BehaviourStatus onUpdate() {
		
		PhysicsComponent targetPhysics = target.getComponent(ComponentTypes.Physics);
		PhysicsComponent physics = input.getParent().getComponent(ComponentTypes.Physics);
		if(targetPhysics == null || physics == null) {
			status = BehaviourStatus.FAILURE;
			return status;
		}
		

		Vector2 direction = targetPhysics.getBody().getPosition().cpy().sub(physics.getBody().getPosition());
		if(input.fire(direction.nor())) {
			status = BehaviourStatus.SUCCESS;
		}
		else {
			status = BehaviourStatus.FAILURE;
		}
		
		return status;
	}

}
