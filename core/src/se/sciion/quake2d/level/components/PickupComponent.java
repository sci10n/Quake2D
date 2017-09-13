package se.sciion.quake2d.level.components;

import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.items.Item;
import se.sciion.quake2d.level.requests.DestroyBody;
import se.sciion.quake2d.level.requests.RequestQueue;
import se.sciion.quake2d.level.system.CollisionCallback;

public class PickupComponent extends EntityComponent implements CollisionCallback{

	private Array<Item> items;
	private RequestQueue<DestroyBody> request;
	public PickupComponent(RequestQueue<DestroyBody> requests, Item ... items) {
		this.items = Array.with(items);
		this.request = requests;
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
		InventoryComponent inventory = target.getComponent(ComponentTypes.Inventory);
		if(inventory == null)
			return;
		
		for(int i = 0; i < items.size; i++) {
			inventory.addItem(items.get(i));
		}
		items.clear();
		
		PhysicsComponent physics = getParent().getComponent(ComponentTypes.Physics);
		if(physics == null)
			return;
		
		request.send(new DestroyBody(physics.getBody()));
		getParent().setActive(false);

	}

}
