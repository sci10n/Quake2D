package se.sciion.quake2d.level.components;

import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.items.Item;
import se.sciion.quake2d.level.system.CollisionCallback;

public class PickupComponent extends EntityComponent implements CollisionCallback{

	private Array<Item> items;
	private Array<Item> removalList;
	public PickupComponent(Item ... items) {
		this.items = Array.with(items);
		removalList = new Array<Item>();
		
	}
	
	@Override
	public void render(RenderModel batch) {
		
	}

	@Override
	public void tick(float delta) {
		
	}

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.Pickup;
	}

	@Override
	public void process(Entity target) {
		
		for(int i = 0; i < items.size; i++) {
			if(items.get(i).accepted(target)){
				removalList.add(i);
			}
		}
		for(int i : removalList){
			items.removeIndex(i);
		}
		
		parent.setActive(false);

	}

}
