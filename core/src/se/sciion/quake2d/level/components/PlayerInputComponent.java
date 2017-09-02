package se.sciion.quake2d.level.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import se.sciion.quake2d.graphics.RenderModel;

/**
 * Dispatches all events and logic based on player input
 * @author sciion
 *
 */
public class PlayerInputComponent extends EntityComponent{

	private OrthographicCamera camera;
	public PlayerInputComponent(OrthographicCamera camera) {
		this.camera = camera;
	}
	
	@Override
	public void render(RenderModel batch) {

	}

	@Override
	public void tick(float delta) {
		
		// Get dV for player based on Keyboard input
		Vector2 direction = new Vector2();
		float magnitude = 6;
		if(Gdx.input.isKeyPressed(Keys.W)){
			direction.y = 1;
		} 
		else if(Gdx.input.isKeyPressed(Keys.S)){
			direction.y = -1;
		}
		
		if(Gdx.input.isKeyPressed(Keys.A)){
			direction.x = -1;
		} 
		else if(Gdx.input.isKeyPressed(Keys.D)){
			direction.x = 1;
		} 
		
		//Normalize to get rid of pesky faster diagonal movement
		direction.nor().scl(magnitude);
		
		// Update sprite location
		PhysicsComponent spriteComponent = getParent().getComponent(ComponentTypes.Physics);
		if(spriteComponent == null)
			return;
		
		Body body = spriteComponent.getBody();
		body.setLinearVelocity(direction);
		
		// Get direction player is looking based on mouse position. Takes into account camera scaling, viewport and translation.
		Vector3 tmp = camera.unproject(new Vector3(Gdx.input.getX(),Gdx.input.getY(),0));
		Vector2 headingDirection = new Vector2(tmp.x, tmp.y);
		
		headingDirection.sub(body.getPosition());
		
		// Fixate rotation such that external forces doesn't count
		body.setFixedRotation(true);
		
		body.setTransform(body.getPosition(), headingDirection.angleRad());
	}

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.PlayerInput;
	}

}
