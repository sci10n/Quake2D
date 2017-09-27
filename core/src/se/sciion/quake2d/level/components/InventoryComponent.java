package se.sciion.quake2d.level.components;

import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.items.Item;

public class InventoryComponent extends EntityComponent {

	private Array<Item> items;
	
	public InventoryComponent(Item ...items) {
		this.items = Array.with(items);
	}
	
	@Override
	public void render(RenderModel batch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tick(float delta) {
		
	}
	
	public void addItem(Item item) {
		for(Item i : items) {
			if(i.getType() == item.getType()) {
				items.removeValue(i,true);
				items.add(item);
				return;
			}
		}
		items.add(item);
	}
	
	public void removeItem(Item item) {
		if(items.contains(item, true)) {
			items.removeValue(item,true);
		}
	}
	
	public boolean containsItem(Item item) {
		return items.contains(item, false);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Item> T getItem(Class<T> type) {
		for(Item i: items) {
			if(type.isInstance(i)) {
				return (T) i;
			}
		}
		return null;
	}
	
	@Override
	public ComponentTypes getType() {
		return ComponentTypes.Inventory;
	}

}
