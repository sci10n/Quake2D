package se.sciion.quake2d.level.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.items.Consumable;

import se.sciion.quake2d.level.system.CollisionCallback;

/**
 * Add to entities which should take damage.
 * @author sciion
 *
 */
public class HealthComponent extends EntityComponent implements CollisionCallback{

	
	private final int MAX_HEALTH;
	private final int MAX_ARMOR;
	
	private int armor;
	private int health;
	
	public HealthComponent(int health, int maxArmor) {
		super();
		this.health = health;
		MAX_HEALTH = health;
		this.armor = 0;
		this.MAX_ARMOR = maxArmor;
	}

	@Override
	public void render(RenderModel batch) {
		
		PhysicsComponent physics = getParent().getComponent(ComponentTypes.Physics);
		if(physics != null && health != 0){
			float part = 180 / MAX_HEALTH;
			Vector2 origin = physics.getBody().getPosition();
			batch.primitiveRenderer.setColor(Color.RED);
			batch.primitiveRenderer.arc(origin.x,origin.y, 1.5f, 0, part * health,health);
		}
	}
	
	public void remove(int amount){
		
		armor -= amount;
		if(armor < 0){
			health += armor;
			armor = 0;
		}
	}
	
	public boolean fullHealth(){
		return health == MAX_HEALTH;
	}
	
	public int getHealth(){
		return health + armor;
	}
	
	public float ratioHealth(){
		return health / (float)(MAX_HEALTH);
	}
	
	public float ratioArmor(){
		return armor / (float)(MAX_ARMOR);
	}
	
	public void addHealth(int amount) {
		health = MathUtils.clamp(health + amount, 0, MAX_HEALTH);
	}
	
	public void addArmor(int amount) {
		armor = MathUtils.clamp(armor + amount, 0, MAX_ARMOR);
	}
	
	@Override
	public void tick(float delta) {
	}

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.Health;
	}

	@Override
	public void process(Entity target) {

		DamageComponent damage = target.getComponent(ComponentTypes.Damage);
		if(damage != null){
			remove(damage.getDamage());
		}
	}

}
