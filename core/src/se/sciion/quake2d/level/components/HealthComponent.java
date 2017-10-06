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

	
	public final int MAX_HEALTH;
	public int health;
	
	public HealthComponent(int health) {
		super();
		this.health = health;
		MAX_HEALTH = health;
	}

	@Override
	public void render(RenderModel batch) {
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
			health -= damage.getDamage();
			if(health <= 0){
				health = 0;
			}
		}
	}

}
