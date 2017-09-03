package se.sciion.quake2d.level.components;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Entity;

/**
 * Physical body of entity.
 * @author sciion
 *
 */
public class PhysicsComponent extends EntityComponent{
	
	// Keep track of all stuff physics
	private Body body;
	
	public PhysicsComponent(Body body) {
		this.body = body;
	}
	
	@Override
	public void render(RenderModel batch) {
		//batch.primitiveRenderer.circle( body.getPosition().x,  body.getPosition().y, 0.5f, 32);
	}

	@Override
	public void tick(float delta) {
		
		// Update sprite location if such is attached to entity
		SpriteComponent sprite = getParent().getComponent(ComponentTypes.Sprite);
		if(sprite != null) {
			Sprite s = sprite.getSprite();
			float x = body.getPosition().x;
			float y = body.getPosition().y;
			s.setPosition(x - s.getWidth()/2.0f,y - s.getHeight()/2.0f);
		}
	}

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.Physics;
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
