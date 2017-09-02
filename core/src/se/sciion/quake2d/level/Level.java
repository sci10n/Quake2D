package se.sciion.quake2d.level;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

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
	
	public void render(SpriteBatch batch){
		for(int i = 0; i < entities.size; i++){
			entities.get(i).render(batch);
		}
	}
}
