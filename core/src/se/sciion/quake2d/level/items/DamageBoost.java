package se.sciion.quake2d.level.items;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.components.DamageBoostComponent;

public class DamageBoost extends Item{

	public final float boost;
	public DamageBoost(String tag, float boost) {
		super(tag);
		this.boost = boost;
	}

	@Override
	public boolean accepted(Entity e) {
		
		DamageBoostComponent c = e.getComponent(ComponentTypes.Boost);
		if(c != null){
			c.boost = boost;
			return true;
		}
		
		return false;
	}

}
