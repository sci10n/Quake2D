package se.sciion.quake2d.level.items;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.components.HealthComponent;

public class Consumable extends Item{

	public final int HealthRestore;
	public final int ArmorRestore;
	
	public Consumable(String tag, int health, int armor) {
		super(tag);
		HealthRestore = health;
		ArmorRestore = armor;
	}

	@Override
	public boolean accepted(Entity e) {
		
		boolean accepted = false;
		HealthComponent health = e.getComponent(ComponentTypes.Health);
		if(health != null) {
			if(ArmorRestore > 0){
				health.addArmor(ArmorRestore);
				accepted = true;
			}
			
			if(HealthRestore > 0){
				if(health.fullHealth()){
					return accepted;
				} else{
					health.addHealth(HealthRestore);
					accepted = true;
				}
			}
		}
		
		return accepted;
	}
}
