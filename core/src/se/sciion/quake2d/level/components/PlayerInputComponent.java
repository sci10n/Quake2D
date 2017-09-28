package se.sciion.quake2d.level.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.system.Pathfinding;

/**
 * Dispatches all events and logic based on player input
 * @author sciion
 *
 */
public class PlayerInputComponent extends EntityComponent{

	private OrthographicCamera camera;
	private Pathfinding pathfinding;

	public PlayerInputComponent(OrthographicCamera camera, Pathfinding pathfinding) {
		this.camera = camera;
		this.pathfinding = pathfinding;
	}
	
	@Override
	public void render(RenderModel batch) {
		
	}

	@Override
	public void tick(float delta) {
		
		// Update sprite location
		PhysicsComponent physicsComponent = getParent().getComponent(ComponentTypes.Physics);
		if(physicsComponent == null)
			return;
		
		Body body = physicsComponent.getBody();
		
		Vector2 vel = body.getLinearVelocity();
		
		if(Gdx.input.isKeyPressed(Keys.W) && vel.y < 7.0f){
			body.setLinearVelocity(vel.add(0, 3.9f));
		} 
		else if(Gdx.input.isKeyPressed(Keys.S) && vel.y > -7.0f){
			body.setLinearVelocity(vel.add(0, -3.9f));
		}
		
		if(Gdx.input.isKeyPressed(Keys.A) && vel.x > -7.0f){
			body.setLinearVelocity(vel.add(-3.9f,0.0f));
		} 
		else if(Gdx.input.isKeyPressed(Keys.D) && vel.x < 7.0f){
			body.setLinearVelocity(vel.add(3.9f,0.0f));
		}
		
		float len = body.getLinearVelocity().len();
		body.setLinearVelocity(body.getLinearVelocity().nor().scl(0.79f * len));
		
		// Get direction player is looking based on mouse position. Takes into account camera scaling, viewport and translation.
		Vector3 tmp = camera.unproject(new Vector3(Gdx.input.getX(),Gdx.input.getY(),0));
		Vector2 headingDirection = new Vector2(tmp.x, tmp.y);
		
		headingDirection.sub(body.getPosition()).nor();
		
		// Fixate rotation such that external forces doesn't count
		//body.setFixedRotation(true);
		
		body.setTransform(body.getPosition(), headingDirection.angleRad());
		//camera.position.set(body.getPosition(), 0);
		//camera.update();
		
		// Test bullet creation. Should be moved to separate component like Weapon or similar
		if(Gdx.input.isKeyJustPressed(Keys.SPACE)){
			WeaponComponent weapon = getParent().getComponent(ComponentTypes.Weapon);
			if(weapon != null){
				weapon.fire(headingDirection, body.getPosition());
			}
		}
		
		// Set to weight pathfinding
		//pathfinding.setPlayerPosition(body.getPosition());
	}

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.PlayerInput;
	}

}
