package se.sciion.quake2d.level.items;

import com.badlogic.gdx.math.MathUtils;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.components.HealthComponent;

public class Consumable extends Item{

	public final int HealthRestore;
	public final int ArmorRestore;
	
	public Consumable(String tag, int health, int armor) {
		super(tag);
		HealthRestore = health;
		ArmorRestore = armor;
	}

	@Override
	public boolean accepted(Entity e) {
		
		HealthComponent health = e.getComponent(ComponentTypes.Health);
		if(health != null) {
			if(health.health < health.MAX_HEALTH) {
				health.health = MathUtils.clamp(health.health + HealthRestore, 0, health.MAX_HEALTH);
				return true;
			}
		}
		return false;
	}
}
