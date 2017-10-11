package se.sciion.quake2d.level.system;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import net.dermetfan.utils.Pair;
import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.components.DamageBoostComponent;
import se.sciion.quake2d.level.components.HealthComponent;
import se.sciion.quake2d.level.components.PhysicsComponent;
import se.sciion.quake2d.level.items.DamageBoost;
import se.sciion.quake2d.sandbox.LevelSandbox;

/**
 * Deals with keeping track of physics world and bodies, no body destruction or
 * cleanup implemented yet
 * 
 * @author sciion
 *
 */
public class PhysicsSystem{

	
	private class Hitscan {
		public Vector2 origin;
		public Vector2 target;
		public Vector2 interpolation;
		public float alpha;
		public float ellapsed;
		public Entity responsible;
	}
	
	private Vector2 p1,p2;
	private ShapeRenderer renderer;
	
	public Array<Hitscan> hitscans;

	private Array<PhysicsComponent> components;

	private class LineOfSightCallback implements RayCastCallback {

		public Vector2 target;
		private float minFraction = Float.MAX_VALUE;

		@Override
		public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
			if(fraction < minFraction){
				minFraction = fraction;
				target = point.cpy();
			}
			
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

	public class FindBodyCallback implements QueryCallback {
		public Array<Body> bodies = new Array<Body>();
		@Override
		public boolean reportFixture(Fixture fixture) {
			if(!bodies.contains(fixture.getBody(), true))
				bodies.add(fixture.getBody());
			return true;
		}
	}


	private EntityContactResolver contactResolver;
	private Box2DDebugRenderer debugRenderer;

	private Array<Body> removalList;
	private OverlapCallback solidcallback;

	private World world;

	public PhysicsSystem() {
		world = new World(new Vector2(0, 0), true);
		removalList = new Array<Body>();
		contactResolver = new EntityContactResolver();
		world.setContactListener(contactResolver);
		debugRenderer = new Box2DDebugRenderer();
		solidcallback = new OverlapCallback();
		components = new Array<PhysicsComponent>();
		renderer = new ShapeRenderer();
		hitscans = new Array<Hitscan>();
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
		hitscans.clear();
		components.clear();
	}

	public Array<PhysicsComponent> queryComponentAt(float x, float y) {
		Array<PhysicsComponent> components = new Array<PhysicsComponent>();
		FindBodyCallback findBodies = new FindBodyCallback();
		world.QueryAABB(findBodies, x-0.5f, y-0.5f, x+0.5f, y+0.5f);
		for (Body b : findBodies.bodies) {
			Entity e = (Entity) b.getUserData();
			if(e != null){
				components.add(e.getComponent(ComponentTypes.Physics));
			}
		}

		return components;
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
		bodyDef.angularDamping = 2.0f;
		bodyDef.linearDamping  = 1.5f;

		Body body = world.createBody(bodyDef);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1;
		fixtureDef.friction = 0.2f;
		fixtureDef.restitution = 0;

		body.createFixture(fixtureDef);

		return body;
	}

	public Body createSensor(float x, float y, BodyType type, Shape shape){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;
		bodyDef.position.set(x, y);
		bodyDef.angularDamping = 2.0f;
		bodyDef.linearDamping  = 1.5f;

		Body body = world.createBody(bodyDef);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1;
		fixtureDef.friction = 0.2f;
		fixtureDef.restitution = 0;
		fixtureDef.isSensor = true;
		body.createFixture(fixtureDef);

		return body;
	}
	
	
	public PhysicsComponent createComponent(float x, float y, BodyType type, Shape shape, boolean sensor) {
		Body body;

		if(sensor){
			 body = createSensor(x, y, type, shape);
		}
		else {
			body = createBody(x, y, type, shape);
		}
		PhysicsComponent component = new PhysicsComponent(body,this);
		components.add(component);
		return component;
	}


	// Check if two point are within each others line of sight
	public boolean lineOfSight(Vector2 origin, Vector2 target) {
		if (origin.cpy().sub(target).len2() < 0.01f) {
			return false;
		}
		
		LineOfSightCallback callback = new LineOfSightCallback();
		callback.target = target.cpy();
		world.rayCast(callback, origin, target);
		p1 = origin.cpy();
		p2 = callback.target.cpy();
		
		if(callback.target == null){
			return false;
		}
		
		return target.cpy().sub(callback.target).len2() <= 1.0f;
	}

	public Vector2 getLineOfSightHit() {
		return p1;
	}

	public void registerCallback(CollisionCallback callback, Entity e) {
		contactResolver.addCollisionCallback(callback, e);
	}
	
	public void removeBody(Body body) {
		removalList.add(body);
	}
	
	public void render(Matrix4 combined) {
		if(LevelSandbox.DEBUG)
			debugRenderer.render(world, combined);
		
		renderer.setProjectionMatrix(combined);
		renderer.begin(ShapeType.Filled);
		renderer.setColor(Color.GOLD);
		for(Hitscan p: hitscans){			
			renderer.rectLine(p.origin.cpy().lerp(p.target, p.ellapsed * 0.5f), p.interpolation, 0.08f);
		}
		renderer.end();
	}

	public void update(float delta) {
		world.step(delta, 10, 10);
		
		for(Hitscan p: hitscans){
			p.ellapsed = MathUtils.clamp(p.ellapsed + p.alpha * delta,0.0f,1.0f);
			p.interpolation = p.origin.cpy().lerp(p.target, p.ellapsed);
			if(p.ellapsed >= 1.0f){
				System.out.println("Hitscan " + p.interpolation);
				for(PhysicsComponent phys :queryComponentAt(p.interpolation.x, p.interpolation.y)){
					System.out.println(phys);
					if(phys != null && phys.getParent() != null && phys.getParent() != p.responsible){
						HealthComponent hlth = phys.getParent().getComponent(ComponentTypes.Health);
						if(hlth != null){
							float boostScl = 1.0f;
							DamageBoostComponent boost = p.responsible.getComponent(ComponentTypes.Boost);
							if(boost != null){
								boostScl = boost.boost;
							}
							hlth.remove(1.0f * boostScl, p.responsible);
						}
					}
				};
				
				hitscans.removeValue(p, true);
			}
		}
	}

	public void clear(){
		world.clearForces();
		world.getBodies(removalList);
		cleanup();
	}

	public Vector2 hitScan(Vector2 origin, Vector2 heading,float speed, Entity responsible) {
		LineOfSightCallback callback = new LineOfSightCallback();
		callback.target = null;
		world.rayCast(callback, origin, origin.cpy().add(heading.cpy().scl(30.0f)));
		if(callback.target != null){
			Hitscan h = new Hitscan();
			h.origin = origin.cpy();
			h.target = callback.target.cpy();
			h.alpha = 1.0f / h.origin.dst(h.target) * speed;
			h.interpolation = origin.cpy();
			h.responsible = responsible;
			h.ellapsed = 0.0f;
			hitscans.add(h);
			return callback.target.cpy();
		}

		return new Vector2(0.0f, 0.0f);
	}

}
