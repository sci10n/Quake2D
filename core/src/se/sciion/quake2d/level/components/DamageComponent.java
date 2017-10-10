package se.sciion.quake2d.level.components;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Entity;

/**
 * Add to all entities which should cause damage on contact.
 * @author sciion
 *
 */
public class DamageComponent extends EntityComponent{
	
	private int damage;
	private Entity responsible;
	
	public DamageComponent(int damage, Entity responsible) {
		this.damage = damage;
		this.responsible = responsible;
	}
	
	@Override
	public void render(RenderModel batch) {
		
	}

	@Override
	public void tick(float delta) {
		
	}
	
	public Entity getResponsible() {
		return responsible;
	}
	
	public int getDamage(){
		return damage;
	}
	
	@Override
	public ComponentTypes getType() {
		return ComponentTypes.Damage;
	}

}
