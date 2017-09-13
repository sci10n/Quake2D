package se.sciion.quake2d.level.items;

import se.sciion.quake2d.enums.ItemType;
import se.sciion.quake2d.graphics.RenderModel;

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
	public void tick(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(RenderModel model) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ItemType getType() {
		// TODO Auto-generated method stub
		return ItemType.Weapon;
	}	
}