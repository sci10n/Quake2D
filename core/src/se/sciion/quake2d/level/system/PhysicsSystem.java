package se.sciion.quake2d.level.system;

import com.badlogic.gdx.math.Matrix4;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

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
			} catch (NullPointerException | ClassCastException e2) {

			}
			return 0;
		}
	}

	private EntityRayCast rayCastCallback;
	private World world;

	private Array<Body> removalList;

	Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

	public PhysicsSystem() {
		world = new World(new Vector2(0, 0), true);
		rayCastCallback = new EntityRayCast();
		removalList = new Array<Body>();
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

	// Use for raycasting against entities
	public Entity rayCast(Vector2 origin, Vector2 direction) {
		// 300 units should be enough for this project.
		world.rayCast(rayCastCallback, origin, origin.cpy().add(direction).scl(300));
		return rayCastCallback.target;
	}

	public Body createBody(float x, float y, BodyType type, Shape shape) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;
		bodyDef.position.set(x, y);

		Body body = world.createBody(bodyDef);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0;
		fixtureDef.friction = 0;
		fixtureDef.restitution = 0;

		body.createFixture(fixtureDef);

		return body;
	}

	public void scheduleRemoval(Body body) {
		removalList.add(body);
	}

	@Override
	public RequestType getType() {
		return RequestType.DestroyBody;
	}

	@Override
	public boolean process(DestroyBody t) {
		System.out.println(t);
		if(t.getBodyRef().getUserData().equals("REMOVAL"))
			return true;
		
		if(!removalList.contains(t.getBodyRef(), true)){
			t.getBodyRef().setUserData("REMOVAL");
			removalList.add(t.getBodyRef());
		}
		return true;
	}

	public World getWorld() {
		return world;
	}

}
