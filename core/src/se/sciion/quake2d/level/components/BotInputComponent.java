package se.sciion.quake2d.level.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;

public class BotInputComponent extends EntityComponent {

	@Override
	public void render(RenderModel batch) {

	}

	@Override
	public void tick(float delta) {
		// Update sprite location
		PhysicsComponent spriteComponent = getParent().getComponent(ComponentTypes.Physics);
		if (spriteComponent == null)
			return;

		Body body = spriteComponent.getBody();

		float len = body.getLinearVelocity().len();
		body.setLinearVelocity(body.getLinearVelocity().nor().scl(0.49f * len));
	}

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.BotInput;
	}

}
