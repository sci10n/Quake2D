package se.sciion.quake2d.level;

import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.graphics.RenderModel;

public abstract class Level {

	protected Array<Entity> entities;

	private Array<Entity> removalList;
	public Level(){
		entities = new Array<Entity>(true, 16);
		removalList = new Array<Entity>(true,16);
	}
		
	public void tick(float delta){
		for(int i = 0; i < entities.size; i++){
			entities.get(i).tick(delta);
			if(!entities.get(i).isActive()){
				removalList.add(entities.get(i));
			}
		}
		
		for(int i = 0; i < removalList.size; i++){
			removalList.get(i).clear();
			entities.removeValue(removalList.get(i), true);
			
		}
	}
	
	public void render(RenderModel model){
		for(int i = 0; i < entities.size; i++){
			entities.get(i).render(model);
		}
	}
	
	public Array<Entity> getEntities() {
		return entities;
	}

}
