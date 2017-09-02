package se.sciion.quake2d.level.system;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
public class PhysicsSystem extends System{

	
	private World world;
	Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

	public PhysicsSystem() {
		world = new World(new Vector2(0,0), true);
	}

	@Override
	public void render(Matrix4 combined) {
		debugRenderer.render(world, combined);
	}
	
	@Override
	public void update(float delta) {
		world.step(delta, 10, 10);
	}
	
	public Body createBody(float x,float y, BodyType type, Shape shape){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;
		bodyDef.position.set(x, y);
		
		Body body = world.createBody(bodyDef);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0;
		fixtureDef.friction = 0;
		fixtureDef.restitution = 0;
		
		Fixture fixture = body.createFixture(fixtureDef);
		
		return body;
	}
	
	
}
