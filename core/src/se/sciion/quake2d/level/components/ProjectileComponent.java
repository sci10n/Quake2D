package se.sciion.quake2d.level.components;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.requests.DestroyBody;
import se.sciion.quake2d.level.requests.RequestQueue;
import se.sciion.quake2d.level.system.CollisionCallback;
import se.sciion.quake2d.level.system.PhysicsSystem;

public class ProjectileComponent extends EntityComponent implements CollisionCallback{

	private RequestQueue<DestroyBody> queue;
	public ProjectileComponent(RequestQueue<DestroyBody> queue) {
		this.queue = queue;
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
		getParent().setActive(false);
		PhysicsComponent physics = getParent().getComponent(ComponentTypes.Physics);
		
		// Make sure we don't remove when we collide with other projectiles
		ProjectileComponent projectile = target.getComponent(ComponentTypes.Projectile);
		if(physics != null && projectile == null){
			queue.send(new DestroyBody(physics.getBody()));

		}
	}

}
