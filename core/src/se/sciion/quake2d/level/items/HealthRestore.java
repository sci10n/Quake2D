package se.sciion.quake2d.level.items;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.components.HealthComponent;

public class HealthRestore extends Item{

	public final int amount;
	public HealthRestore(String tag, int amount) {
		super(tag);
		this.amount = amount;
	}

	@Override
	public boolean accepted(Entity e) {
		
		HealthComponent health = e.getComponent(ComponentTypes.Health);
		if(health != null && health.getHealth() < health.MAX_HEALTH) {
			health.addHealth(amount);
			return true;
		}
		return false;
	}

}
