package se.sciion.quake2d.ai.behaviour.nodes;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.components.HealthComponent;

public class PickupArmor extends PickupConsumable {

	private int previousArmor;
	
	public PickupArmor(Level level, String tag) {
		super(level, tag);
		previousArmor = 0;
	}

	@Override
	protected boolean restored() {
		HealthComponent health = entityOwner.getComponent(ComponentTypes.Health);
		boolean restored = false;
		if(health != null){
			restored = health.getArmor() > previousArmor || health.getArmor() == health.MAX_ARMOR;
		}
		
		previousArmor = health.getArmor();
		return restored;
	}

}
