package se.sciion.quake2d.level.components;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;

public class ArmorComponent extends EntityComponent{

	
	public int armor;
	public final int MAX_ARMOR;
	public ArmorComponent(int armor) {
		this.MAX_ARMOR = armor;
		armor = 0;
	}
	
	@Override
	public void render(RenderModel batch) {
		
	}

	@Override
	public void tick(float delta) {
		
	}

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.Armor;
	}

}
