package se.sciion.quake2d.ai.behaviour.nodes;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.components.DamageBoostComponent;

public class PickupDamageBoost extends PickupConsumable{

	public PickupDamageBoost(BotInputComponent input, Level level, String tag) {
		super(input, level, tag);
	}

	@Override
	protected boolean restored() {
		DamageBoostComponent boost = input.getParent().getComponent(ComponentTypes.Boost);
		boolean restored = false;
		if(boost != null){
			restored = boost.boost > 1.0f;
		}
		return restored;
	}

}
