package se.sciion.quake2d.level.items;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.components.InventoryComponent;

public class Weapon extends Item {
	public final float cooldown;
	public final int bullets;
	public final int capacity;
	public final float knockback;
	public final float spread;
	public final float speed;
	
	/**
	 * 
	 * @param cooldown
	 * @param bullets
	 * @param capacity - not used
	 * @param knockback
	 * @param spread
	 * @param speed
	 */
	public Weapon(float cooldown, int bullets, int capacity, float knockback, float spread, float speed) {
		super();
		this.cooldown = cooldown;
		this.bullets = bullets;
		this.capacity = capacity;
		this.knockback = knockback;
		this.spread = spread;
		this.speed = speed;
	}

	@Override
	public boolean accepted(Entity e) {
		
		InventoryComponent inventory = e.getComponent(ComponentTypes.Inventory);
		if(inventory != null) {
			if(inventory.containsItem(this)){
				return false;
			}
			inventory.addItem(this);
			return true;
		}
		return false;
	}
	
	
}