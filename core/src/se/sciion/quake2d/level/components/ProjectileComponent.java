package se.sciion.quake2d.level.components;

import com.badlogic.gdx.math.Vector2;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.system.CollisionCallback;

public class ProjectileComponent extends EntityComponent implements CollisionCallback{

	private Vector2 direction;
	public ProjectileComponent(Vector2 direction) {
		this.direction = direction;
	}

	@Override
	public void render(RenderModel batch) {
		
	}

	@Override
	public void tick(float delta) {
		
	}
		

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.Projectile;
	}

	@Override
	public void process(Entity target) {
		PhysicsComponent physics = getParent().getComponent(ComponentTypes.Physics);
		if(physics != null){
			physics.getBody().applyForceToCenter(direction.cpy(), true);
		}
		// Make sure we don't remove when we collide with other projectiles
		ProjectileComponent projectile = target.getComponent(ComponentTypes.Projectile);
		if(projectile == null){
			getParent().setActive(false);
		}
	}

}
