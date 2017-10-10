package se.sciion.quake2d.level;

import java.util.HashMap;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.graphics.RenderModel;

public class Level {

	protected Array<Entity> entities;
	protected HashMap<String,Array<Entity>> complexEntities;
	
	private Array<String> tags;
	private Array<Entity> removalList;
	
	
	public Level(){
		entities = new Array<Entity>(true, 16);
		complexEntities = new HashMap<String,Array<Entity>>();
		removalList = new Array<Entity>(true,16);
		tags = new Array<String>();
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
			entities.removeValue(removalList.get(i), false);
			for(Array<Entity> e: complexEntities.values()) {
				if(e.contains(removalList.get(i), false)) {
					e.removeValue(removalList.get(i), false);
				}
			}
		}
		removalList.clear();
	}
	

	private void addComplexEntity(Entity e, String id){
		if(!complexEntities.containsKey(id)) {
			complexEntities.put(id, new Array<Entity>());
		}
		complexEntities.get(id).add(e);

	}
	
	/**
	 * Only works for complex entities
	 * @param id
	 * @return
	 */
	public Array<Entity> getEntities(String id) {
		if(complexEntities.containsKey(id))
			return complexEntities.get(id);
		else 
			return new Array<Entity>();
	}
	
	public Entity createEntity() {
		Entity e = new Entity();
		entities.add(e);
		return e;
	}
	
	public Entity createEntity(String id){
		Entity e = new Entity();
		addComplexEntity(e, id);
		tags.add(id);
		entities.add(e);
		return e;
	}
	
	public void render(RenderModel model){
		for(int i = 0; i < entities.size; i++){
			entities.get(i).render(model);
		}
	}

	public void debugRender(RenderModel model) {
		model.debugging = true;
		for(int i = 0; i < entities.size; i++){
			entities.get(i).render(model);
		}
		model.debugging = false;
	}
	
	public Array<Entity> getEntities() {
		return entities;
	}

	public Array<String> getTags() {
		return tags;
	}

}
