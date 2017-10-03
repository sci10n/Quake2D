package se.sciion.quake2d.level.system;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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

import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.components.PhysicsComponent;

/**
 * Deals with keeping track of physics world and bodies, no body destruction or
 * cleanup implemented yet
 * 
 * @author sciion
 *
 */
public class PhysicsSystem {

	private Vector2 p1,p2;
	private class EntityRayCast implements RayCastCallback {

		public Entity target;

		public EntityRayCast() {
			target = null;
		}

		@Override
		public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
			try {
				target = (Entity) fixture.getBody().getUserData();
			} catch (NullPointerException e1) {

			} catch (ClassCastException e2) {

			}
			return 0;
		}
	}

	private class LineOfSightCallback implements RayCastCallback {

		public Vector2 target;

		@Override
		public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
			target = point;
			return 1;
		}

	}

	private class OverlapCallback implements QueryCallback {

		public boolean solid = false;

		@Override
		public boolean reportFixture(Fixture fixture) {
			if (fixture.getBody().getType() == BodyType.StaticBody) {
				solid = true;
				return false;
			}
			return true;
		}

	}

	private EntityContactResolver contactResolver;
	private Box2DDebugRenderer debugRenderer;
	private EntityRayCast rayCastCallback;

	private Array<Body> removalList;
	private OverlapCallback solidcallback;

	private World world;

	public PhysicsSystem() {
		world = new World(new Vector2(0, 0), true);
		rayCastCallback = new EntityRayCast();
		removalList = new Array<Body>();
		contactResolver = new EntityContactResolver();
		world.setContactListener(contactResolver);
		debugRenderer = new Box2DDebugRenderer();
		solidcallback = new OverlapCallback();
	}

	public void cleanup() {
		if(removalList.size <= 0){
			return;
		}
		
		world.clearForces();
		if (!world.isLocked()) {
			for (int i = 0; i < removalList.size; i++) {
				if (world.getBodyCount() > 0) {
					world.destroyBody(removalList.get(i));
				}
			}
			removalList.clear();
		}
	}

	public boolean containsSolidObject(float x, float y, float w, float h) {
		solidcallback.solid = false;
		world.QueryAABB(solidcallback, x - w / 2.0f, y - h / 2.0f, x + w / 2.0f, y + h / 2.0f);
		return solidcallback.solid;
	}

	// Create body on logical coordinates and register it with the world.
	private Body createBody(float x, float y, BodyType type, Shape shape) {
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

	public PhysicsComponent createComponent(float x, float y, BodyType type, Shape shape) {
		
		Body body = createBody(x, y, type, shape);
		PhysicsComponent component = new PhysicsComponent(body,this);
		return component;
	}

	// Check if two point are within each others line of sight
	public boolean lineOfSight(Vector2 origin, Vector2 target) {
		if (origin.cpy().sub(target).len2() < 0) {
			return false;
		}
		LineOfSightCallback callback = new LineOfSightCallback();
		callback.target = target;
		world.rayCast(callback, origin, target);
		p1 = origin;
		p2 = callback.target;
		float dist = callback.target.cpy().sub(target).len();
		return dist <= 0.5f;
	}

	// Use for raycasting against entities
	public Entity rayCast(Vector2 origin, Vector2 direction) {
		// 300 units should be enough for this project.
		world.rayCast(rayCastCallback, origin, origin.cpy().add(direction).scl(300));
		return rayCastCallback.target;
	}

	public void registerCallback(CollisionCallback callback, Entity e) {
		contactResolver.addCollisionCallback(callback, e);
	}
	
	public void removeBody(Body body) {
		removalList.add(body);
	}
	
	public void render(Matrix4 combined) {
		debugRenderer.render(world, combined);
		
		ShapeRenderer sr = new ShapeRenderer();
		sr.setProjectionMatrix(combined);
		
		if(p1 != null && p2 != null) {
			sr.begin(ShapeType.Line);
			sr.line(p1, p2);
			sr.end();
		}
	}

	public void update(float delta) {
		world.step(delta, 10, 10);
	}

}
