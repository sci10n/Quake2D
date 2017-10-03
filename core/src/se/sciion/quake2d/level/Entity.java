package se.sciion.quake2d.level;

import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.components.EntityComponent;

public class Entity {

	private Array<EntityComponent> components; 
	private boolean active;
	
	public Entity(){
		components = new Array<EntityComponent>(false, 5);
		active = true;
	}
	
	public void render(RenderModel batch){
		if(!active)
			return;
		
		for(int i = 0; i < components.size; i++){
			components.get(i).render(batch);
		}
	}
	
	public void tick(float delta){
		if(!active)
			return;
		
		for(int i = 0; i < components.size; i++){
			components.get(i).tick(delta);
		}
	}
	
	public void addComponent(EntityComponent c){
		if(!components.contains(c, true)){
			components.add(c);
			c.setParent(this);
		}
		
	}
	
	public void removeComponent(EntityComponent c){
		if(components.contains(c, true)){
			components.removeValue(c,true);
			c.setParent(null);
		}
	}
	
	public void removeComponent(ComponentTypes type){
		Array<EntityComponent> toRemove = new Array<EntityComponent>();
		for(int i = 0; i < components.size; i++){
			if(components.get(i).getType() == type)
				toRemove.add(components.get(i));
		}
		for(EntityComponent i : toRemove){
			removeComponent(i);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends EntityComponent> T  getComponent(ComponentTypes type){
		
		for(int i = 0; i < components.size; i++){
			if(components.get(i).getType() == type){
				try{
					return (T) components.get(i);
				}
				catch(ClassCastException e){
					return null;
				}
			}
		}
		return null;
	}
	
	public void setActive(boolean active){
		this.active = active;
	}
	
	public boolean isActive(){
		return active;
	}

	public void cleanup() {
		if(active)
			return;
		for(EntityComponent ec : components) {
			ec.cleanup();
			removeComponent(ec);
		}
		
	}
}
