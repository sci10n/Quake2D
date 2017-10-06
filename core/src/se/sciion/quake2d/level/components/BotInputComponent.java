package se.sciion.quake2d.level.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;

import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import static guru.nidi.graphviz.model.Factory.*;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.ai.behaviour.BehaviourTree;
import se.sciion.quake2d.level.system.Pathfinding;

public class BotInputComponent extends EntityComponent {

	private BehaviourTree behaviourTree;
	private boolean isDead = false;
	private Pathfinding pathfinding;
	private Vector2 targetPosition;
	private Array<Vector2> currentPath;

	public BotInputComponent(Pathfinding pathfinding) {
		this.pathfinding = pathfinding;
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

			batch.primitiveRenderer.begin();

			if (currentPath.size != 0) {
				for (int i = currentPath.size - 1; i >= 0; i--) {
					Vector2 p = currentPath.get(i);
					batch.primitiveRenderer.setColor(Color.RED);
					batch.primitiveRenderer.line(prev, p);
					prev = p;
				}
			}

			batch.primitiveRenderer.end();
		}
	}

	@Override
	public void tick(float delta) {
		HealthComponent healthComponent = getParent().getComponent(ComponentTypes.Health);
		if (healthComponent.health <= 0) isDead = true;

		// If we're dead then we likely can't think now do we :)
		if (behaviourTree != null && !isDead)
			behaviourTree.tick();
		else setTarget(null);

		PhysicsComponent spriteComponent = getParent().getComponent(ComponentTypes.Physics);
		if (spriteComponent == null)
			return;
		
		if(targetPosition == null)
			return;
		
		Body body = spriteComponent.getBody();
		Vector2 origin = body.getPosition();

		Array<Vector2> path = pathfinding.findPath(new Vector2((int) (origin.x), (int) (origin.y)), targetPosition);
		if (path.size > 1)
			path.pop(); // Current position;
		currentPath = path;
		
		if(currentPath.size <= 0)
			return;
		
		Vector2 closestPoint = currentPath.peek();
		if (closestPoint.cpy().sub(origin).len2() < 0.5f) {
			currentPath.pop();
		}
		body.setLinearVelocity(body.getLinearVelocity().scl(0.35f));

		if(body.getLinearVelocity().len() > 7.0f){
			body.getLinearVelocity().clamp(0, 7.0f);
		}
		Vector2 direction = closestPoint.cpy().add(0.5f, 0.5f).sub(origin).nor().scl(3.9f);
		Vector2 vel = body.getLinearVelocity();
		body.setLinearVelocity(vel.add(direction));
		body.setTransform(body.getPosition(), vel.angleRad());

	}
	
	public boolean fire(Vector2 heading) {
		PhysicsComponent physics = getParent().getComponent(ComponentTypes.Physics);
		if(physics == null) {
			return false;
		}
		
		WeaponComponent weapon = getParent().getComponent(ComponentTypes.Weapon);
		if(weapon != null){
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
	}

	public BehaviourTree getBehaviourTree() {
		return behaviourTree;
	}

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.BotInput;
	}

}
