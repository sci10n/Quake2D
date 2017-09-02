package se.sciion.quake2d.sandbox;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.HardcodedLevel;
import se.sciion.quake2d.level.components.PhysicsComponent;
import se.sciion.quake2d.level.components.PlayerInputComponent;
import se.sciion.quake2d.level.components.SpriteComponent;
import se.sciion.quake2d.level.system.PhysicsSystem;

public class LevelSandbox extends ApplicationAdapter{

	OrthographicCamera camera;
	
	private HardcodedLevel level;
	private AssetManager assets;
	
	private PhysicsSystem physicsSystem;
	private RenderModel model;
	@Override
	public void create () {
		
		// Set up level object
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth() / 32.0f, Gdx.graphics.getHeight() / 32.0f);
		model = new RenderModel();
		physicsSystem = new PhysicsSystem();
		
		loadAssets();
		
		// Bag of entities
		Array<Entity> entities = new Array<Entity>();
		
		Entity playerEntity = new Entity();
		
		// Player components
		PlayerInputComponent playerMovement = new PlayerInputComponent(camera);
		
		// Require polygonal shape for physics component
		CircleShape shape = new CircleShape();
		shape.setRadius(0.5f);
		PhysicsComponent playerPhysics = new PhysicsComponent(physicsSystem.createBody(10.0f, 10.0f,BodyType.DynamicBody,shape));
		
		playerEntity.addComponent(playerPhysics);
		playerEntity.addComponent(playerMovement);
		entities.add(playerEntity);
		
		// Static level entity
		Entity obstacleEntity = new Entity();
		
		PolygonShape boxShape = new PolygonShape();
		boxShape.setAsBox(2f, 4f);
		PhysicsComponent obstaclePhysics = new PhysicsComponent(physicsSystem.createBody(2, 2, BodyType.StaticBody, boxShape));
		
		obstacleEntity.addComponent(obstaclePhysics);
		entities.add(obstacleEntity);
		
		// Static level entity #2
		Entity obstacle2Entity = new Entity();

		PhysicsComponent obstacle2Physics = new PhysicsComponent(physicsSystem.createBody(7, 7, BodyType.StaticBody, boxShape));
		
		obstacleEntity.addComponent(obstacle2Physics);
		entities.add(obstacle2Entity);
		
		level = new HardcodedLevel(entities);
	}
	
	public void loadAssets(){
		assets = new AssetManager();
		assets.setLoader(Texture.class, new TextureLoader(new InternalFileHandleResolver()));
		assets.load("textures/Dummy.png", Texture.class);
		assets.finishLoading();
	}
	
	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false, width / 32.0f, height / 32.0f);
		super.resize(width, height);
	}
	
	@Override
	public void render () {
		
		camera.update();
		
		level.tick(Gdx.graphics.getDeltaTime());
		physicsSystem.update(Gdx.graphics.getDeltaTime());
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
		model.setProjectionMatrix(camera.combined);
		model.begin();
		level.render(model);
		model.end();
		physicsSystem.render(camera.combined);

	}
	
	@Override
	public void dispose () {

	}
}
