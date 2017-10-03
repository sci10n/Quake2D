package se.sciion.quake2d.level.components;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Entity;

public abstract class EntityComponent{

	protected Entity parent;
	
	public abstract void render(RenderModel batch);
	public abstract void tick(float delta);
	public abstract ComponentTypes getType();
	
	/**
	 * Use when you need to release objects from other systems or memory managment
	 */
	public void cleanup() { }
	
	public void setParent(Entity parent){
		this.parent = parent;
	}
	
	public Entity getParent(){
		return parent;
	}
}
