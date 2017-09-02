package se.sciion.quake2d.level.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import se.sciion.quake2d.graphics.RenderModel;

public class SpriteComponent extends EntityComponent{

	private Sprite sprite;
	
	public SpriteComponent(float x, float y,float width, float height, Texture texture) {
		sprite = new Sprite();
		sprite.setPosition(x, y);
		sprite.setSize(width, height);
		sprite.setRegion(texture);
	}
	
	@Override
	public void render(RenderModel batch) {
		//sprite.draw(batch.spriteRenderer);
	}

	@Override
	public void tick(float delta) {
		
	}

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.Sprite;
	}
	
	public Sprite getSprite(){
		return sprite;
	}

}
