package se.sciion.quake2d.level.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.requests.CreateBullet;
import se.sciion.quake2d.level.requests.RequestQueue;

/**
 * Dispatches all events and logic based on player input
 * @author sciion
 *
 */
public class PlayerInputComponent extends EntityComponent{

	private OrthographicCamera camera;
	private RequestQueue levelRequests;
	
	public PlayerInputComponent(OrthographicCamera camera, RequestQueue levelRequests) {
		this.camera = camera;
		this.levelRequests = levelRequests;
	}
	
	@Override
	public void render(RenderModel batch) {

	}

	@Override
	public void tick(float delta) {
		
		// Update sprite location
		PhysicsComponent spriteComponent = getParent().getComponent(ComponentTypes.Physics);
		if(spriteComponent == null)
			return;
		
		Body body = spriteComponent.getBody();
		
		Vector2 vel = body.getLinearVelocity();
		
		if(Gdx.input.isKeyPressed(Keys.W) && vel.y < 4.0f){
			body.setLinearVelocity(vel.add(0, 1.9f));
		} 
		else if(Gdx.input.isKeyPressed(Keys.S) && vel.y > -4.0f){
			body.setLinearVelocity(vel.add(0, -1.9f));
		}
		
		if(Gdx.input.isKeyPressed(Keys.A) && vel.x > -4.0f){
			body.setLinearVelocity(vel.add(-1.9f,0.0f));
		} 
		else if(Gdx.input.isKeyPressed(Keys.D) && vel.x < 4.0f){
			body.setLinearVelocity(vel.add(1.9f,0.0f));
		}
		
		float len = body.getLinearVelocity().len();
		body.setLinearVelocity(body.getLinearVelocity().nor().scl(0.89f * len));
		
		// Get direction player is looking based on mouse position. Takes into account camera scaling, viewport and translation.
		Vector3 tmp = camera.unproject(new Vector3(Gdx.input.getX(),Gdx.input.getY(),0));
		Vector2 headingDirection = new Vector2(tmp.x, tmp.y);
		
		headingDirection.sub(body.getPosition()).nor();
		
		// Fixate rotation such that external forces doesn't count
		body.setFixedRotation(true);
		
		body.setTransform(body.getPosition(), headingDirection.angleRad());
		
		
		// Test bullet creation. Should be moved to separate component like Weapon or similar
		if(Gdx.input.isKeyJustPressed(Keys.SPACE)){
			WeaponComponent weapon = getParent().getComponent(ComponentTypes.Weapon);
			if(weapon != null){
				weapon.fire(headingDirection, body.getPosition());
			}
		}
	}

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.PlayerInput;
	}

}
