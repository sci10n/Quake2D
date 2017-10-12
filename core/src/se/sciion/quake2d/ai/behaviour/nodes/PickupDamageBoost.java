package se.sciion.quake2d.ai.behaviour.nodes;

import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.ai.behaviour.BehaviourNode;
import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.components.DamageBoostComponent;

public class PickupDamageBoost extends PickupConsumable{

	public PickupDamageBoost(Level level, String tag) {
		super(level, tag);
	}

	@Override
	protected boolean restored() {
		DamageBoostComponent boost = entityOwner.getComponent(ComponentTypes.Boost);
		boolean restored = false;
		if(boost != null){
			restored = boost.boost > 1.0f;
		}
		return restored;
	}

	@Override
	public BehaviourNode clone() {
		return new PickupDamageBoost(level, tag);
	}

	@Override
	public BehaviourNode randomized() {
		// TODO Auto-generated method stub
		return new PickupDamageBoost(level, tag);
	}
}
