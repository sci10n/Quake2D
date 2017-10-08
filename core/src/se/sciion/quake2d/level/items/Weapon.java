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
	public final int baseDamage;
	/**
	 * 
	 * @param cooldown
	 * @param bullets
	 * @param capacity - not used
	 * @param knockback
	 * @param spread
	 * @param speed
	 */
	public Weapon(String tag, float cooldown, int bullets, int capacity, float knockback, float spread, float speed, int damage) {
		super(tag);
		this.cooldown = cooldown;
		this.bullets = bullets;
		this.capacity = capacity;
		this.knockback = knockback;
		this.spread = spread;
		this.speed = speed;
		this.baseDamage = damage;
	}

	@Override
	public boolean accepted(Entity e) {
		
		InventoryComponent inventory = e.getComponent(ComponentTypes.Inventory);
		if(inventory != null) {
			inventory.addItem(this);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null)
			return false;
		if(obj instanceof Weapon){
			return 
					((Weapon) obj).bullets == bullets && 
					((Weapon) obj).capacity == capacity && 
					((Weapon) obj).cooldown == cooldown && 
					((Weapon) obj).knockback == knockback && 
					((Weapon) obj).speed == speed && 
					((Weapon) obj).spread == spread;
		}
		return super.equals(obj);
	}
}