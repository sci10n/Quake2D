package se.sciion.quake2d.level;

import java.util.HashMap;

import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.graphics.RenderModel;

public class Level {

	protected Array<Entity> entities;
	protected HashMap<String,Entity> complexEntities;
	
	private Array<Entity> removalList;
	
	
	public Level(){
		entities = new Array<Entity>(true, 16);
		complexEntities = new HashMap<String,Entity>();
		removalList = new Array<Entity>(true,16);
	}
		
	public void tick(float delta){
		for(int i = 0; i < entities.size; i++){
			entities.get(i).tick(delta);
			if(!entities.get(i).isActive()){
				removalList.add(entities.get(i));
			}
		}
		
		// Removal and cleanup of inactive entities
		for(int i = 0; i < removalList.size; i++){
			removalList.get(i).cleanup();
			entities.removeValue(removalList.get(i), true);
			if(complexEntities.containsValue(removalList.get(i))){
				complexEntities.remove(removalList.get(i));
			}
		}
		removalList.clear();
	}
	

	public void addEntity(Entity e, String id){
		complexEntities.put(id, e);
	}
	
	/**
	 * Only works for complex entities
	 * @param id
	 * @return
	 */
	public Entity getEntity(String id) {
		return complexEntities.get(id);
	}
	
	public Entity createEntity() {
		Entity e = new Entity();
		entities.add(e);
		return e;
	}
	
	public Entity createEntity(String id){
		Entity e = new Entity();
		complexEntities.put(id, e);
		entities.add(e);
		return e;
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
