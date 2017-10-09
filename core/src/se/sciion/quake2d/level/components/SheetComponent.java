package se.sciion.quake2d.level.components;

import java.util.HashMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.SheetRegion;

public class SheetComponent extends EntityComponent {
	private HashMap<String, SheetRegion> regions;
	private String currentRegion;
	private Vector2 regionScale;

	public SheetComponent(String currentRegion) {
		this.regions = new HashMap<String, SheetRegion>();
		this.currentRegion = currentRegion;
	}

	@Override
	public void render(RenderModel batch) {
		if (batch.debugging) return; // No debug render for this thing.
		PhysicsComponent physics = getParent().getComponent(ComponentTypes.Physics);
		if (physics == null)
			return;

		Body physicsBody = physics.getBody();

		Vector2 physicsPosition = physicsBody.getPosition();
		float physicsAngle = physicsBody.getAngle() * MathUtils.radiansToDegrees;

		SheetRegion region = regions.get(getCurrentRegion());
		if (region == null)
			return;

		Vector2 origin = region.origin;
		float angle = physicsAngle + region.rotation;
		Vector2 position = physicsPosition.cpy().add(region.offset.cpy().rotate(angle));
		Vector2 scale = region.scale;

		TextureRegion textureRegion = region.texture;
		batch.spriteRenderer.draw(textureRegion, position.x, position.y, origin.x, origin.y,
		                          textureRegion.getRegionWidth(), textureRegion.getRegionHeight(),
		                          scale.x, scale.y, angle);
	}

	@Override
	public void tick(float deltaTime) {
	}

	public void addRegion(SheetRegion region, String id) {
		if(!regions.containsKey(id)) {
			regions.put(id, region);
		}
	}

	public void setCurrentRegion(String id) {
		currentRegion = id;
	}

	public String getCurrentRegion() {
		return currentRegion;
	}

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.Sheet;
	}
}
