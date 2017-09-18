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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.HardcodedLevel;
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.components.HealthComponent;
import se.sciion.quake2d.level.components.InventoryComponent;
import se.sciion.quake2d.level.components.LineOfSightComponent;
import se.sciion.quake2d.level.components.PhysicsComponent;
import se.sciion.quake2d.level.components.PickupComponent;
import se.sciion.quake2d.level.components.PlayerInputComponent;
import se.sciion.quake2d.level.components.WeaponComponent;
import se.sciion.quake2d.level.items.Items;
import se.sciion.quake2d.level.requests.RequestQueue;
import se.sciion.quake2d.level.system.BulletFactory;
import se.sciion.quake2d.level.system.Pathfinding;
import se.sciion.quake2d.level.system.PhysicsSystem;

public class LevelSandbox extends ApplicationAdapter{

	OrthographicCamera camera;
	
	private HardcodedLevel level;
	private AssetManager assets;
	
	// Request queue for inter-entity/system communication.
	@SuppressWarnings("rawtypes")
	private RequestQueue levelRequests;
	private PhysicsSystem physicsSystem;
	private RenderModel model;
	private Pathfinding pathfinding;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void create () {
		
		// Set up level object
		Gdx.graphics.setSystemCursor(SystemCursor.Crosshair);
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 30,30);
		
		model = new RenderModel();
		physicsSystem = new PhysicsSystem();
		// Bad generic!
		levelRequests = new RequestQueue();
		
		pathfinding = new Pathfinding(30,30);
		
		loadAssets();
		
		// Bag of entities
		Array<Entity> entities = new Array<Entity>();
		
		{
			Entity playerEntity = new Entity();
			// Player components
			PlayerInputComponent playerMovement = new PlayerInputComponent(camera, levelRequests,pathfinding);
			
			// Require polygonal shape for physics component
			CircleShape shape = new CircleShape();
			shape.setRadius(0.5f);
			PhysicsComponent playerPhysics = new PhysicsComponent(physicsSystem.createBody(10.0f, 10.0f,BodyType.DynamicBody,shape));
			WeaponComponent playerWeapon = new WeaponComponent(levelRequests);
			
			HealthComponent playerHealth = new HealthComponent(10);
			physicsSystem.getContactResolver().addCollisionCallback(playerHealth, playerEntity);
			
			playerEntity.addComponent(playerHealth);
			playerEntity.addComponent(playerPhysics);
			playerEntity.addComponent(playerMovement);
			playerEntity.addComponent(playerWeapon);
			playerEntity.addComponent(new InventoryComponent(Items.Shotgun));
			playerEntity.addComponent(new LineOfSightComponent(pathfinding));
			entities.add(playerEntity);
		}
		
		// Bot dummy
		{
			Entity botEntity = new Entity();
			
			CircleShape shape = new CircleShape();
			shape.setRadius(0.5f);
			PhysicsComponent botPhysics = new PhysicsComponent(physicsSystem.createBody(12.0f, 4.0f,BodyType.DynamicBody,shape));
			WeaponComponent botWeapon = new WeaponComponent(levelRequests);
			HealthComponent botHealth = new HealthComponent(10);
			physicsSystem.getContactResolver().addCollisionCallback(botHealth, botEntity);

			// This should be waay more complex
			BotInputComponent botInput = new BotInputComponent(pathfinding);
			botEntity.addComponent(botHealth);
			botEntity.addComponent(botPhysics);
			botEntity.addComponent(botWeapon);
			botEntity.addComponent(new InventoryComponent());
			botEntity.addComponent(botInput);
			entities.add(botEntity);
		}
		// Sniper weapon pickup
		{
			Entity sniperPickup = new Entity();
			PolygonShape boxShape = new PolygonShape();
			boxShape.setAsBox(0.5f, 0.5f);
			Vector2 origin = new Vector2(5, 15);

			PhysicsComponent pickupPhysics = new PhysicsComponent(physicsSystem.createBody(origin.x,origin.y, BodyType.KinematicBody, boxShape));
			sniperPickup.addComponent(pickupPhysics);
			PickupComponent pickup = new PickupComponent(levelRequests,Items.Sniper);
			physicsSystem.getContactResolver().addCollisionCallback(pickup, sniperPickup);
			sniperPickup.addComponent(pickup);
			entities.add(sniperPickup);
			pathfinding.addItemLocation(Items.Sniper, origin);

		}
		
		// Sniper weapon pickup
		{
			Entity shutgunPickup = new Entity();
			PolygonShape boxShape = new PolygonShape();
			boxShape.setAsBox(0.5f, 0.5f);
			
			Vector2 origin = new Vector2(5, 25);
			PhysicsComponent pickupPhysics = new PhysicsComponent(physicsSystem.createBody(origin.x, origin.y, BodyType.KinematicBody, boxShape));
			shutgunPickup.addComponent(pickupPhysics);
			PickupComponent pickup = new PickupComponent(levelRequests,Items.Shotgun);
			physicsSystem.getContactResolver().addCollisionCallback(pickup, shutgunPickup);
			shutgunPickup.addComponent(pickup);
			entities.add(shutgunPickup);
			
			pathfinding.addItemLocation(Items.Shotgun, origin);
		}
		
		// Static level entity
		{
			Entity leftWallEntity = new Entity();
			Entity rightWallEntity = new Entity();
			Entity downWallEntity = new Entity();
			Entity upWallEntity = new Entity();

			PolygonShape verticalBox = new PolygonShape();
			PolygonShape horizontalBox = new PolygonShape();

			verticalBox.setAsBox(0.5f, 15.0f);
			horizontalBox.setAsBox(15.0f, 0.5f);
			
			PhysicsComponent leftWall = new PhysicsComponent(physicsSystem.createBody(0, 15, BodyType.StaticBody, verticalBox));
			PhysicsComponent rightWall = new PhysicsComponent(physicsSystem.createBody(30, 15, BodyType.StaticBody, verticalBox));

			PhysicsComponent upWall = new PhysicsComponent(physicsSystem.createBody(15, 0, BodyType.StaticBody, horizontalBox));
			PhysicsComponent downWall = new PhysicsComponent(physicsSystem.createBody(15, 30, BodyType.StaticBody, horizontalBox));

			leftWallEntity.addComponent(leftWall);
			rightWallEntity.addComponent(rightWall);
			downWallEntity.addComponent(downWall);
			upWallEntity.addComponent(upWall);

			entities.add(leftWallEntity);
			entities.add(rightWallEntity);
			entities.add(upWallEntity);
			entities.add(downWallEntity);
			
			{
				Entity wall = new Entity();
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(5, 5);
				wall.addComponent(new PhysicsComponent(physicsSystem.createBody(15, 15, BodyType.StaticBody, shape)));
				entities.add(wall);
			}
			{
				Entity wall = new Entity();
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(1, 3);
				wall.addComponent(new PhysicsComponent(physicsSystem.createBody(15, 25, BodyType.StaticBody, shape)));
				entities.add(wall);
			}
			{
				Entity wall = new Entity();
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(1, 2);
				wall.addComponent(new PhysicsComponent(physicsSystem.createBody(25, 25, BodyType.StaticBody, shape)));
				entities.add(wall);
			}
			{
				Entity wall = new Entity();
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(1, 4);
				wall.addComponent(new PhysicsComponent(physicsSystem.createBody(5, 20, BodyType.StaticBody, shape)));
				entities.add(wall);
			}
			{
				Entity wall = new Entity();
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(4, 1);
				wall.addComponent(new PhysicsComponent(physicsSystem.createBody(6, 5, BodyType.StaticBody, shape)));
				entities.add(wall);
			}
			{
				Entity wall = new Entity();
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(4, 1);
				wall.addComponent(new PhysicsComponent(physicsSystem.createBody(24, 5, BodyType.StaticBody, shape)));
				entities.add(wall);
			}
		}
		
		level = new HardcodedLevel(entities);
		
		// Create bullets on CreateBullet requests
		levelRequests.subscribe(new BulletFactory(level,physicsSystem,levelRequests));
		levelRequests.subscribe(physicsSystem);
		
		pathfinding.update(physicsSystem);
	}
	
	public void loadAssets(){
		assets = new AssetManager();
		assets.setLoader(Texture.class, new TextureLoader(new InternalFileHandleResolver()));
//		assets.load("textures/Dummy.png", Texture.class);
		assets.finishLoading();
	}
	
	@Override
	public void resize(int width, int height) {
	}
	
	@Override
	public void render () {
		camera.update();
		
		level.tick(Gdx.graphics.getDeltaTime());
		physicsSystem.update(Gdx.graphics.getDeltaTime());
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
		model.setProjectionMatrix(camera.combined);
		
		//pathfinding.render(model);

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
