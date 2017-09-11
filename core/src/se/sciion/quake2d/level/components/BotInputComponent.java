package se.sciion.quake2d.level.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.system.Pathfinding;

public class BotInputComponent extends EntityComponent {

	private Pathfinding pathfinding;
	private Vector2 targetPosition;
	private Array<Vector2> currentPath;

	public BotInputComponent(Pathfinding pathfinding) {
		this.pathfinding = pathfinding;
		currentPath = new Array<Vector2>();
		targetPosition = new Vector2();
	}

	@Override
	public void render(RenderModel batch) {
		PhysicsComponent spriteComponent = getParent().getComponent(ComponentTypes.Physics);
		if (spriteComponent == null)
			return;
		Body body = spriteComponent.getBody();
		Vector2 origin = body.getPosition();
		Vector2 prev = origin;
		
		if(currentPath.size != 0){
			for (int i = currentPath.size - 1; i >= 0; i--) {
				Vector2 p = currentPath.get(i);
				batch.primitiveRenderer.setColor(Color.WHITE);
				batch.primitiveRenderer.line(prev, p);
				prev = p;
			}
		}


	}

	@Override
	public void tick(float delta) {

		if (currentPath.size < 2) {
			targetPosition = new Vector2(MathUtils.random(32), MathUtils.random(32));
			while (!pathfinding.reachable(targetPosition))
				targetPosition = new Vector2(MathUtils.random(32), MathUtils.random(32));
		}

		// Update sprite location
		PhysicsComponent spriteComponent = getParent().getComponent(ComponentTypes.Physics);
		if (spriteComponent == null)
			return;

		Body body = spriteComponent.getBody();
		Vector2 origin = body.getPosition();

		Array<Vector2> path = pathfinding.findPath(new Vector2((int) (origin.x), (int) (origin.y)), targetPosition);
		path.pop(); // Current position;
		currentPath = path;
		if(currentPath.size < 2) {
			return;
		} 
		
		Vector2 closestPoint = currentPath.pop();
		Vector2 direction = closestPoint.cpy().add(0.5f, 0.5f).sub(origin).nor();
		body.setLinearVelocity(direction);
		body.setLinearVelocity(body.getLinearVelocity().scl(0.49f));

	}

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.BotInput;
	}

}
