package se.sciion.quake2d.level.items;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.components.HealthComponent;

public class ArmorRestore extends Item{

	public final int amount;
	public ArmorRestore(String tag, int amount) {
		super(tag);
		this.amount = amount;
	}

	@Override
	public boolean accepted(Entity e) {
		
		HealthComponent health = e.getComponent(ComponentTypes.Health);
		if(health != null && health.getArmor() < health.MAX_ARMOR) {
			health.addArmor(amount);
			return true;
		}
		return false;
	}

	@Override
	public String getPickUpSound() {
		return "armor";
	}

}
