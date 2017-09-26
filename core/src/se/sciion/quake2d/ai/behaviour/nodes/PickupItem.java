package se.sciion.quake2d.ai.behaviour.nodes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.ai.behaviour.BehaviourNode;
import se.sciion.quake2d.ai.behaviour.BehaviourStatus;
import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.components.InventoryComponent;
import se.sciion.quake2d.level.components.PhysicsComponent;
import se.sciion.quake2d.level.items.Item;
import se.sciion.quake2d.level.system.Pathfinding;

public class PickupItem extends BehaviourNode{

	private Pathfinding pathfinder;
	private BotInputComponent input;
	private Item item;
	
	public PickupItem(Item item, Pathfinding pathfinding, BotInputComponent component) {
		this.pathfinder = pathfinding;
		this.input = component;
		this.item = item;
	}
	
	@Override
	protected void onEnter() {
		status = BehaviourStatus.RUNNING;
		super.onEnter();
	}
	
	@Override
	protected BehaviourStatus onUpdate() {

		PhysicsComponent physics = input.getParent().getComponent(ComponentTypes.Physics);
		if(physics == null){
			status = BehaviourStatus.FAILURE;
			return status;
		}
		
		Vector2 fromLoc = physics.getBody().getPosition();
		Vector2 itemLoc = pathfinder.getItemLocation(item);
		
		InventoryComponent inventory = input.getParent().getComponent(ComponentTypes.Inventory);
		if (inventory.getItem(item.getClass()) != null && pathfinder.getItemLocation(item) == null){
			
			status = BehaviourStatus.SUCCESS;
		}
		else if(inventory.getItem(item.getClass()) == null&& pathfinder.getItemLocation(item) == null){
			status = BehaviourStatus.FAILURE;
		}
		else{
			status = BehaviourStatus.RUNNING;
		}
		input.setTarget(itemLoc);
		System.out.println(status);

		return status;
	}

}
