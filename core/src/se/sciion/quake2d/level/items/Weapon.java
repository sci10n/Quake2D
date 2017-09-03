package se.sciion.quake2d.level.items;

public class Weapon {
	public final float cooldown;
	public final int bullets;
	public final int capacity;
	public final float nockback;
	public final float spread;
	public final float speed;
	
	/**
	 * 
	 * @param cooldown
	 * @param bullets
	 * @param capacity - not used
	 * @param nockback
	 * @param spread
	 * @param speed
	 */
	public Weapon(float cooldown, int bullets, int capacity, float nockback, float spread, float speed) {
		super();
		this.cooldown = cooldown;
		this.bullets = bullets;
		this.capacity = capacity;
		this.nockback = nockback;
		this.spread = spread;
		this.speed = speed;
	}	
}