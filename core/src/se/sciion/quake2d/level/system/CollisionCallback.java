package se.sciion.quake2d.level.system;

import se.sciion.quake2d.level.Entity;

public interface CollisionCallback {
	
	public abstract void process(Entity target);
	
}
