package se.sciion.quake2d.level.items;

import se.sciion.quake2d.enums.ItemType;
import se.sciion.quake2d.graphics.RenderModel;

public abstract class Item {

	public abstract void tick(float delta);
	public abstract ItemType getType();
}
