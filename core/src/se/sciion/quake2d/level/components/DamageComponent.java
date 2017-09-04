package se.sciion.quake2d.level.components;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;

/**
 * Add to all entities which should cause damage on contact.
 * @author sciion
 *
 */
public class DamageComponent extends EntityComponent{
	
	private int damage;
	
	public DamageComponent(int damage) {
		this.damage = damage;
	}
	
	@Override
	public void render(RenderModel batch) {
		
	}

	@Override
	public void tick(float delta) {
		
	}
	
	
	public int getDamage(){
		return damage;
	}
	
	@Override
	public ComponentTypes getType() {
		return ComponentTypes.Damage;
	}

}
