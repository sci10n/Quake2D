package se.sciion.quake2d.ai.behaviour.nodes;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.components.HealthComponent;

public class PickupHealth extends PickupConsumable{

	private int previousHealth;
	
	public PickupHealth(BotInputComponent input, Level level, String tag) {
		super(level, tag);
		previousHealth = 0;
	}

	@Override
	protected boolean restored() {
		HealthComponent health = parent.getComponent(ComponentTypes.Health);
		boolean restored = false;
		if(health != null){
			restored = health.getHealth() > previousHealth;
		}
		
		previousHealth = health.getHealth();
		return restored;
	}

}
