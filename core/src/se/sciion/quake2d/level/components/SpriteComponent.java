package se.sciion.quake2d.level.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.enums.ComponentTypes;

public class SpriteComponent extends EntityComponent {
	private TextureRegion texture;
	private Vector2 textureOrigin;
	private Vector2 textureOffset;
	private Vector2 textureScale;
	private float textureRotation;

	public SpriteComponent(TextureRegion texture, Vector2 origin, Vector2 offset, Vector2 scale, float rotation) {
		this.texture = texture;
		textureOrigin = origin;
		textureOffset = offset;
		textureScale = scale;
		textureRotation = rotation;
	}

	@Override
	public void render(RenderModel batch) {
		if (batch.debugging) return; // No debugging graphics for this thing.
		PhysicsComponent physics = getParent().getComponent(ComponentTypes.Physics);
		if (physics == null)
			return;

		Body physicsBody = physics.getBody();

		Vector2 physicsPosition = physicsBody.getPosition();
		float physicsAngle = physicsBody.getAngle() * MathUtils.radiansToDegrees;

		Vector2 origin = textureOrigin;
		float angle = physicsAngle + textureRotation;
		Vector2 position = physicsPosition.cpy().add(textureOffset.cpy().rotate(angle));
		Vector2 scale = textureScale;

		batch.spriteRenderer.draw(texture, position.x, position.y, origin.x, origin.y,
		                          texture.getRegionWidth(), texture.getRegionHeight(),
		                          scale.x, scale.y, angle);
	}

	@Override
	public void tick(float deltaTime) {
	}

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.Sprite;
	}
}
