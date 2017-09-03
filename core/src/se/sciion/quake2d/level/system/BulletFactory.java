package se.sciion.quake2d.level.system;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import se.sciion.quake2d.enums.RequestType;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.Subscriber;
import se.sciion.quake2d.level.components.PhysicsComponent;
import se.sciion.quake2d.level.events.CreateBullet;

public class BulletFactory implements Subscriber<CreateBullet> {

	private Level level;
	private PhysicsSystem physics;
	
	public BulletFactory(Level level, PhysicsSystem physics) {
		this.level = level;
		this.physics = physics;
	}

	@Override
	public boolean process(CreateBullet r) {
		System.out.println("Create Bullet!");
		Entity bullet = new Entity();
		
		CircleShape circle = new CircleShape();
		circle.setRadius(0.05f);
		PhysicsComponent bulletPhysics = new PhysicsComponent(physics.createBody(r.getOrigin().x,r.getOrigin().y,BodyType.DynamicBody,circle));
		bulletPhysics.getBody().setLinearVelocity(r.getDirection().cpy().scl(r.getSpeed()));
		bulletPhysics.getBody().setBullet(true);
		
		level.getEntities().add(bullet);
		return true;
	}

	@Override
	public RequestType getType() {
		return RequestType.CreateBullet;
	}

}
