package se.sciion.quake2d.level.components;

import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.items.Item;
import se.sciion.quake2d.level.system.CollisionCallback;
import se.sciion.quake2d.level.system.SoundSystem;

public class PickupComponent extends EntityComponent implements CollisionCallback {

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
			Item item = items.get(i);
			if(item.accepted(target)){
				SoundSystem.getInstance()
				           .playSound(item.getPickUpSound());
				removalList.add(item);
			}
		}

		for(Item i : removalList){
			items.removeValue(i,false);
		}
		
		if(items.size == 0) {
			parent.setActive(false);
		}

	}

}
