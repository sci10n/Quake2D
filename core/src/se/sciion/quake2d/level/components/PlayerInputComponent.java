package se.sciion.quake2d.level.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class PlayerInputComponent extends EntityComponent{

	
	public PlayerInputComponent() {
		
	}
	
	@Override
	public void render(SpriteBatch batch) {
		
	}

	@Override
	public void tick(float delta) {
		
		Vector2 direction = new Vector2();
		float magnitude = 6 * delta;
		
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
		
		direction.nor().scl(magnitude);
		
		SpriteComponent spriteComponent = getParent().getComponent(ComponentTypes.Sprite);
		if(spriteComponent == null)
			return;
		Sprite sprite = spriteComponent.getSprite();
		sprite.setPosition(sprite.getX() + direction.x, sprite.getY() + direction.y);
	}

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.PlayerInput;
	}

}
