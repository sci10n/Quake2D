package se.sciion.quake2d.ai.behaviour.nodes;

import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.ai.behaviour.BehaviourNode;
import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.components.HealthComponent;

public class PickupHealth extends PickupConsumable{

	private int previousHealth;
	
	public PickupHealth(Level level, String tag) {
		super(level, tag);
		previousHealth = 0;
	}

	@Override
	protected boolean restored() {
		HealthComponent health = entityOwner.getComponent(ComponentTypes.Health);
		boolean restored = false;
		if(health != null){
			restored = health.getHealth() > previousHealth || health.getHealth() >= health.MAX_HEALTH;
		}
		
		previousHealth = health.getHealth();
		return restored;
	}

	@Override
	public BehaviourNode clone() {
		return new PickupHealth(level, tag);
	}
	
  @Override
	public BehaviourNode randomized() {
		return new PickupHealth(level, tag);
	}
}
