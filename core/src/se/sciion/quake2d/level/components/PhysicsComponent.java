package se.sciion.quake2d.level.components;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.system.PhysicsSystem;

/**
 * Physical body of entity.
 * @author sciion
 *
 */
public class PhysicsComponent extends EntityComponent{
	
	// Keep track of all stuff physics
	private Body body;
	
	private PhysicsSystem system;
	
	public PhysicsComponent(Body body, PhysicsSystem system) {
		this.body = body;
		this.system = system;
	}
	
	@Override
	public void render(RenderModel batch) {

	}

	@Override
	public void tick(float delta) {
		
	}

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.Physics;
	}
	
	@Override
	public void cleanup() {
		system.removeBody(body);
		super.cleanup();
	}
	
	// Override to add parent as userData for body
	@Override
	public void setParent(Entity parent) {
		super.setParent(parent);
		body.setUserData(parent);
	}
	
	public Body getBody() {
		return body;
	}

}
