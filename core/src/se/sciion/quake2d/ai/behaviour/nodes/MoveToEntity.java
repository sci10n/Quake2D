package se.sciion.quake2d.ai.behaviour.nodes;

import com.badlogic.gdx.math.Vector2;

import se.sciion.quake2d.ai.behaviour.BehaviourNode;
import se.sciion.quake2d.ai.behaviour.BehaviourStatus;
import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.components.PhysicsComponent;
import se.sciion.quake2d.level.system.Pathfinding;
import se.sciion.quake2d.level.system.PhysicsSystem;

public class MoveToEntity extends BehaviourNode {

	private Entity target;
	private BotInputComponent input;
	private float minDistance;
	private PhysicsSystem physics;

	public MoveToEntity(Entity target, PhysicsSystem physics, BotInputComponent input,
			float minDistance) {
		super();
		this.target = target;
		this.input = input;
		this.minDistance = minDistance;
		this.physics = physics;

	}

	@Override
	protected void onEnter() {
		status = BehaviourStatus.RUNNING;
		super.onEnter();
		System.out.println("MoveToEntity enter");

	}

	@Override
	protected BehaviourStatus onUpdate() {
		PhysicsComponent physics = input.getParent().getComponent(ComponentTypes.Physics);
		if (physics == null) {
			status = BehaviourStatus.FAILURE;
			return status;
		}

		Vector2 fromLoc = physics.getBody().getPosition();

		PhysicsComponent targetPhysics = target.getComponent(ComponentTypes.Physics);

		if (targetPhysics == null ) {
			status = BehaviourStatus.SUCCESS;
			input.setTarget(null);
			System.out.println("MoveToEntity: " + status);

			return status;
		}

		Vector2 targetLoc = targetPhysics.getBody().getPosition();
		
		input.setTarget(targetLoc);

		float distance = fromLoc.cpy().sub(targetLoc).len();

		if (distance > minDistance) {
			status = BehaviourStatus.RUNNING;

		} else if (distance <= minDistance && this.physics.lineOfSight(fromLoc, targetLoc)) {
			status = BehaviourStatus.SUCCESS;
			input.setTarget(null);
			System.out.println("MoveToEntity: " + status);

		}

		return status;
	}

}
