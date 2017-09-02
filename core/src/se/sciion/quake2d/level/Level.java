package se.sciion.quake2d.level;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.graphics.RenderModel;

public abstract class Level {

	protected Array<Entity> entities;
	
	public Level(){
		entities = new Array<Entity>(true, 16);
	}
		
	public void tick(float delta){
		for(int i = 0; i < entities.size; i++){
			entities.get(i).tick(delta);
		}
	}
	
	public void render(RenderModel model){
		for(int i = 0; i < entities.size; i++){
			entities.get(i).render(model);
		}
	}
}
