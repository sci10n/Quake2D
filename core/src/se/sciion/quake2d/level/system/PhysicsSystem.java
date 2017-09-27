package se.sciion.quake2d.level.system;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.enums.RequestType;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.requests.DestroyBody;
import se.sciion.quake2d.level.requests.Subscriber;

/**
 * Deals with keeping track of physics world and bodies, no body destruction or
 * cleanup implemented yet
 * 
 * @author sciion
 *
 */
public class PhysicsSystem implements Subscriber<DestroyBody> {

	private class EntityRayCast implements RayCastCallback {

		public Entity target;

		public EntityRayCast() {
			target = null;
		}

		@Override
		public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
			try {
				target = (Entity) fixture.getBody().getUserData();
			}catch(NullPointerException e1){
				
			}catch(ClassCastException e2){
				
			}
			return 0;
		}
	}

	private class LineOfSightCallback implements RayCastCallback {

		public boolean lineOfSight = false;
		public Vector2 target;
		@Override
		public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
			target = point;
			return 0;
		}
		
	}
	
	private class OverlapCallback implements QueryCallback{
		
		public boolean solid = false;
		
		@Override
		public boolean reportFixture(Fixture fixture) {
			if(fixture.getBody().getType() == BodyType.StaticBody){
				solid = true;
				return false;
			}
			return true;
		}
		
	}
	
	private EntityRayCast rayCastCallback;
	private EntityContactResolver contactResolver;
	private OverlapCallback solidcallback;
	
	private World world;
	private Array<Body> removalList;
	
	private Box2DDebugRenderer debugRenderer;

	public PhysicsSystem() {
		world = new World(new Vector2(0, 0), true);
		rayCastCallback = new EntityRayCast();
		removalList = new Array<Body>();
		contactResolver = new EntityContactResolver();
		world.setContactListener(contactResolver);
		debugRenderer = new Box2DDebugRenderer();
		solidcallback = new OverlapCallback();
	}

	public EntityContactResolver getContactResolver() {
		return contactResolver;
	}

	public void render(Matrix4 combined) {
		debugRenderer.render(world, combined);
	}

	public void update(float delta) {
		world.step(delta, 10, 10);
		if (!world.isLocked()) {
			for (int i = 0; i < removalList.size; i++) {
				if(world.getBodyCount() > 0){					
					world.destroyBody(removalList.get(i));
				}
			}
			removalList.clear();
		}
	}
	
	public boolean containsSolidObject(float x, float y, float w, float h){
		solidcallback.solid = false;
		world.QueryAABB(solidcallback, x - w/2.0f, y - h/2.0f, x + w/2.0f, y + h/2.0f);
		return solidcallback.solid;
	}
	
	// Use for raycasting against entities
	public Entity rayCast(Vector2 origin, Vector2 direction) {
		// 300 units should be enough for this project.
		world.rayCast(rayCastCallback, origin, origin.cpy().add(direction).scl(300));
		return rayCastCallback.target;
	}
	
	// Check if two point are within each others line of sight
	public boolean lineOfSight(Vector2 origin, Vector2 target){
		if(origin.cpy().sub(target).len2() <= 0){
			return false;
		}
		LineOfSightCallback callback = new LineOfSightCallback();
		callback.lineOfSight = false;
		callback.target = target;
		world.rayCast(callback, origin, target);
		float dist = callback.target.cpy().sub(target).len();
		return  dist < 0.5f;
	}
	
	// Create body on logical coordinates and register it with the world.
	public Body createBody(float x, float y, BodyType type, Shape shape) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;
		bodyDef.position.set(x, y);

		Body body = world.createBody(bodyDef);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1;
		fixtureDef.friction = 0.2f;
		fixtureDef.restitution = 0;

		body.createFixture(fixtureDef);

		return body;
	}

	// Stuff related to RequestQueue
	@Override
	public RequestType getType() {
		return RequestType.DestroyBody;
	}

	@Override
	public boolean process(DestroyBody t) {
		
		// Make sure we don't remove body already removed.
		if(t.getBodyRef().getUserData().equals("REMOVAL"))
			return true;
		
		if(!removalList.contains(t.getBodyRef(), true)){
			t.getBodyRef().setUserData("REMOVAL");
			removalList.add(t.getBodyRef());
		}
		return true;
	}

}
