package se.sciion.quake2d.level.components;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import se.sciion.quake2d.ai.behaviour.BehaviourTree;
import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.system.Pathfinding;
import se.sciion.quake2d.level.system.PhysicsSystem;

public class BotInputComponent extends EntityComponent {

	private BehaviourTree behaviourTree;
	private boolean isDead = false;

	private Pathfinding pathfinding;
	private Vector2 targetPosition;
	private Array<Vector2> currentPath;

	private Vector2 lineOfSightHit;
	private boolean hasTargetLos = false;

	private PhysicsSystem physicsSystem;

	private Vector2 previousPos;

	public BotInputComponent(Pathfinding pathfinding, PhysicsSystem physicsSystem) {
		this.pathfinding = pathfinding;
		this.physicsSystem = physicsSystem;
		currentPath = new Array<Vector2>();
	}

	@Override
	public void render(RenderModel batch) {
		if (batch.debugging) {
			PhysicsComponent spriteComponent = getParent().getComponent(ComponentTypes.Physics);
			if (spriteComponent == null)
				return;

			Body body = spriteComponent.getBody();
			Vector2 origin = body.getPosition();
			Vector2 prev = origin;

			if (targetPosition != null) {
				batch.primitiveRenderer.begin(ShapeType.Filled);
				batch.primitiveRenderer.setColor(Color.WHITE);
				batch.primitiveRenderer.x(targetPosition, 0.03f);
				batch.primitiveRenderer.end();

			batch.primitiveRenderer.begin(ShapeType.Line);
			if (currentPath.size != 0 && !hasTargetLos) {
				for (int i = currentPath.size - 1; i >= 0; i--) {
					Vector2 p = currentPath.get(i);
					batch.primitiveRenderer.setColor(Color.GOLD);
					batch.primitiveRenderer.line(prev, p);
					prev = p;
				}
				batch.primitiveRenderer.end();

				if (hasTargetLos && lineOfSightHit != null) {
					batch.primitiveRenderer.begin(ShapeType.Filled);
					batch.primitiveRenderer.setColor(Color.WHITE);
					batch.primitiveRenderer.rectLine(lineOfSightHit, targetPosition, 0.1f);
					batch.primitiveRenderer.end();
				}
			}
		}
	}

	@Override
	public void tick(float delta) {
		HealthComponent healthComponent = getParent().getComponent(ComponentTypes.Health);
		if (healthComponent.getHealth() <= 0)
			isDead = true;

		// If we're dead then we likely can't think now do we :)
		if (behaviourTree != null && !isDead)
			behaviourTree.tick();
		else
			setTarget(null);

		PhysicsComponent spriteComponent = getParent().getComponent(ComponentTypes.Physics);
		if (spriteComponent == null)
			return;

		if (targetPosition == null)
			return;

		Body body = spriteComponent.getBody();
		Vector2 origin = body.getPosition();

		Vector2 closestPoint = targetPosition;

		// Only pathfind if we cant walk straight ahead
		if (!physicsSystem.lineOfSight(origin, targetPosition) || Vector2.dst(origin.x, origin.y,targetPosition.x,targetPosition.y) > 10.0f ) {
			hasTargetLos = false; // We need to pathfind here.

			Array<Vector2> path = pathfinding.findPath(origin, targetPosition, parent);
			if (path.size > 1)
				path.pop(); // Current position;
			currentPath = path;
			if (currentPath.size <= 0)
				return;

			closestPoint = currentPath.peek();
			if (closestPoint.cpy().sub(origin).len2() < 0.1f) {
				currentPath.pop();
			}

		} else {
			lineOfSightHit = physicsSystem.getLineOfSightHit();
			hasTargetLos = true;
		}

		previousPos = origin;

		body.setLinearVelocity(body.getLinearVelocity().scl(0.55f));

		if (body.getLinearVelocity().len() > 7.0f) {
			body.getLinearVelocity().clamp(0, 7.0f);
		}
		Vector2 direction = closestPoint.cpy().sub(origin).nor().scl(2.3f);

		Vector2 vel = body.getLinearVelocity();
		body.setLinearVelocity(vel.add(direction));
		body.setTransform(body.getPosition(), vel.angleRad());

	}

	public boolean fire(Vector2 heading) {
		PhysicsComponent physics = getParent().getComponent(ComponentTypes.Physics);
		if (physics == null) {
			return false;
		}

		WeaponComponent weapon = getParent().getComponent(ComponentTypes.Weapon);
		physics.getBody().setTransform(physics.getBody().getPosition(), heading.angleRad());
		if (weapon != null) {
			return weapon.fire(heading, physics.getBody().getPosition());

		}
		return false;
	}

	public Array<Vector2> getCurrentPath() {
		return currentPath;
	}

	public void setTarget(Vector2 v) {
		targetPosition = v;
	}

	public void setBehaviourTree(BehaviourTree behaviourTree) {
		this.behaviourTree = behaviourTree;
		this.behaviourTree.setParent(parent);
	}

	public BehaviourTree getBehaviourTree() {
		return behaviourTree;
	}

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.BotInput;
	}

}
