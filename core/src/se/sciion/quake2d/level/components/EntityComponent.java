package se.sciion.quake2d.level.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import se.sciion.quake2d.level.Entity;

public abstract class EntityComponent {

	private Entity parent;
	
	public abstract void render(SpriteBatch batch);
	public abstract void tick(float delta);
	public abstract ComponentTypes getType();
	
	public void setParent(Entity parent){
		this.parent = parent;
	}
	
	public Entity getParent(){
		return parent;
	}
}
