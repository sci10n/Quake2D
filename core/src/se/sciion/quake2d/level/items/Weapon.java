package se.sciion.quake2d.level.items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.components.InventoryComponent;

public abstract class Weapon extends Item {
	public static Array<String> tags;
	
	static {
		tags = new Array<String>();
	}
	
	public final float cooldown;
	public final int bullets;
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
	public Weapon(String tag, float cooldown, int bullets, float knockback, float spread, float speed, int damage) {
		super(tag);
		this.cooldown = cooldown;
		this.bullets = bullets;
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
	
	
	public abstract void tick(float delta);
	public abstract void render(RenderModel model);
	public abstract boolean fire(Vector2 origin, Vector2 direction, Entity owner);
	
	@Override
	public String getPickUpSound() {
		return "weapon";
	}
}