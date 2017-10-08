package se.sciion.quake2d.level.components;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;

public class DamageBoostComponent extends EntityComponent{

	
	public float boost;
	
	public DamageBoostComponent() {
		// TODO Auto-generated constructor stub
		boost = 1;
	}
	
	@Override
	public void render(RenderModel batch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tick(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ComponentTypes getType() {
		// TODO Auto-generated method stub
		return ComponentTypes.Boost;
	}

}
