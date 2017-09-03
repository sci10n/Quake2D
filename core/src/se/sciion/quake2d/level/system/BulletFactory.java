package se.sciion.quake2d.level.system;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import se.sciion.quake2d.enums.RequestType;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.components.DamageComponent;
import se.sciion.quake2d.level.components.PhysicsComponent;
import se.sciion.quake2d.level.components.ProjectileComponent;
import se.sciion.quake2d.level.requests.CreateBullet;
import se.sciion.quake2d.level.requests.DestroyBody;
import se.sciion.quake2d.level.requests.RequestQueue;
import se.sciion.quake2d.level.requests.Subscriber;

/**
 * Listens for bullet requests and create entities 
 * @author sciion
 *
 */
public class BulletFactory implements Subscriber<CreateBullet>{

	private Level level;
	private PhysicsSystem physics;
	private EntityContactResolver resolver;
	private RequestQueue<DestroyBody> requests;
	
	// Pooling of bullets could be usefull
	
	public BulletFactory(Level level, PhysicsSystem physics, RequestQueue<DestroyBody> requests, EntityContactResolver resolver) {
		this.level = level;
		this.physics = physics;
		this.resolver = resolver;
		this.requests = requests;
	}

	@Override
	public boolean process(CreateBullet r) {
		Entity bullet = new Entity();
		
		CircleShape circle = new CircleShape();
		circle.setRadius(0.02f);
		PhysicsComponent bulletPhysics = new PhysicsComponent(physics.createBody(r.getOrigin().x,r.getOrigin().y,BodyType.DynamicBody,circle));
		bulletPhysics.getBody().setLinearVelocity(r.getDirection().cpy().scl(r.getSpeed()));
		bulletPhysics.getBody().setBullet(true);
		
		bullet.addComponent(bulletPhysics);
		
		ProjectileComponent bulletPhysicsResolver = new ProjectileComponent(requests);
		resolver.addCollisionCallback(bulletPhysicsResolver, bullet);
		bullet.addComponent(bulletPhysicsResolver);
		DamageComponent bulletDamage = new DamageComponent(r.getDamage());
		bullet.addComponent(bulletDamage);
		
		level.getEntities().add(bullet);
		return true;
	}

	@Override
	public RequestType getType() {
		return RequestType.CreateBullet;
	}

}
