package se.sciion.quake2d.level.system;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import se.sciion.quake2d.level.Entity;

public class EntityContactResolver implements ContactListener{

	
	private ObjectMap<Entity,Array<CollisionCallback>> collisionCallbacks;

	public EntityContactResolver(PhysicsSystem system) {
		collisionCallbacks = new ObjectMap<Entity,Array<CollisionCallback>>();
		system.getWorld().setContactListener(this);
	}
	
	public void addCollisionCallback(CollisionCallback callback, Entity e){
		if(!collisionCallbacks.containsKey(e)){
			collisionCallbacks.put(e, new Array<CollisionCallback>());
		}
		
		if(!collisionCallbacks.get(e).contains(callback, true)){
			collisionCallbacks.get(e).add(callback);
		}
	}
	
	public void removeCollisionCallback(CollisionCallback callback, Entity e){
		if(!collisionCallbacks.containsKey(e))
			return;
		
		collisionCallbacks.get(e).removeValue(callback, true);
	}
	
	@Override
	public void beginContact(Contact contact) {
		Entity e1 = null;
		Entity e2 = null;
		
		try{
			e1 = (Entity) contact.getFixtureA().getBody().getUserData();
			e2 = (Entity) contact.getFixtureB().getBody().getUserData();
		}
		catch(NullPointerException ex1){
			
		}
		catch(ClassCastException ex2){
			
		}
		if(e1 == null || e2 == null)
			return;
		
		if(collisionCallbacks.containsKey(e1))
			for(CollisionCallback c : collisionCallbacks.get(e1)){
				c.process(e2);
			}
		if(collisionCallbacks.containsKey(e2))
			for(CollisionCallback c : collisionCallbacks.get(e2)){
				c.process(e1);
			}
	}

	@Override
	public void endContact(Contact contact) {
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}

}
