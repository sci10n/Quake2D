package se.sciion.quake2d.sandbox;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.HardcodedLevel;
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.components.HealthComponent;
import se.sciion.quake2d.level.components.PhysicsComponent;
import se.sciion.quake2d.level.components.PlayerInputComponent;
import se.sciion.quake2d.level.components.WeaponComponent;
import se.sciion.quake2d.level.items.Weapons;
import se.sciion.quake2d.level.requests.RequestQueue;
import se.sciion.quake2d.level.system.BulletFactory;
import se.sciion.quake2d.level.system.EntityContactResolver;
import se.sciion.quake2d.level.system.PhysicsSystem;

public class LevelSandbox extends ApplicationAdapter{

	OrthographicCamera camera;
	
	private HardcodedLevel level;
	private AssetManager assets;
	
	// Request queue for inter-entity/system communication.
	private RequestQueue levelRequests;

	private PhysicsSystem physicsSystem;
	private EntityContactResolver contactResolver;

	
	private RenderModel model;
	@Override
	public void create () {
		
		// Set up level object
		Gdx.graphics.setSystemCursor(SystemCursor.Crosshair);
		
		// Bad generic!
		levelRequests = new RequestQueue();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth() / 32.0f, Gdx.graphics.getHeight() / 32.0f);
		model = new RenderModel();
		
		physicsSystem = new PhysicsSystem();
		contactResolver = new EntityContactResolver(physicsSystem);

		loadAssets();
		
		// Bag of entities
		Array<Entity> entities = new Array<Entity>();
		
		{
			Entity playerEntity = new Entity();
			// Player components
			PlayerInputComponent playerMovement = new PlayerInputComponent(camera, levelRequests);
			
			// Require polygonal shape for physics component
			CircleShape shape = new CircleShape();
			shape.setRadius(0.5f);
			PhysicsComponent playerPhysics = new PhysicsComponent(physicsSystem.createBody(10.0f, 10.0f,BodyType.DynamicBody,shape));
			WeaponComponent playerWeapon = new WeaponComponent(Weapons.Shotgun, levelRequests);
			
			HealthComponent playerHealth = new HealthComponent(10);
			contactResolver.addCollisionCallback(playerHealth, playerEntity);
			
			playerEntity.addComponent(playerHealth);
			playerEntity.addComponent(playerPhysics);
			playerEntity.addComponent(playerMovement);
			playerEntity.addComponent(playerWeapon);
			entities.add(playerEntity);
		}
		
		// Bot dummy
		{
			Entity botEntity = new Entity();
			
			CircleShape shape = new CircleShape();
			shape.setRadius(0.5f);
			PhysicsComponent botPhysics = new PhysicsComponent(physicsSystem.createBody(12.0f, 4.0f,BodyType.DynamicBody,shape));
			WeaponComponent botWeapon = new WeaponComponent(Weapons.Shotgun, levelRequests);
			HealthComponent botHealth = new HealthComponent(10);
			contactResolver.addCollisionCallback(botHealth, botEntity);

			// This should be waay more complex
			BotInputComponent botInput = new BotInputComponent();
			botEntity.addComponent(botHealth);
			botEntity.addComponent(botPhysics);
			botEntity.addComponent(botWeapon);
			botEntity.addComponent(botInput);
			entities.add(botEntity);
		}
		
		// Static level entity
		{
			Entity obstacleEntity = new Entity();
			Entity obstacle2Entity = new Entity();

			PolygonShape boxShape = new PolygonShape();
			boxShape.setAsBox(2f, 4f);
			
			PhysicsComponent obstaclePhysics = new PhysicsComponent(physicsSystem.createBody(2, 2, BodyType.StaticBody, boxShape));
			PhysicsComponent obstacle2Physics = new PhysicsComponent(physicsSystem.createBody(7, 7, BodyType.StaticBody, boxShape));
			
			obstacleEntity.addComponent(obstaclePhysics);
			obstacleEntity.addComponent(obstacle2Physics);
			
			entities.add(obstacleEntity);
			entities.add(obstacle2Entity);
		}
		
		level = new HardcodedLevel(entities);
		
		// Create bullets on CreateBullet requests
		levelRequests.subscribe(new BulletFactory(level,physicsSystem,levelRequests,contactResolver));
		levelRequests.subscribe(physicsSystem);
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
		
		// Currently only Box2D debug renderer
		physicsSystem.render(camera.combined);

	}
	
	@Override
	public void dispose () {

	}
}
