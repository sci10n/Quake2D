package se.sciion.quake2d.level.items;

import se.sciion.quake2d.level.Entity;

public abstract class Item {

	private final String tag;
	
	public Item(String tag) {
		this.tag = tag;
	}
	
	public abstract boolean accepted(Entity e);
	
	public String getTag(){
		return tag;
	}
}
