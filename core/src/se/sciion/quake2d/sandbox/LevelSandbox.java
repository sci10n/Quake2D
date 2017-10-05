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
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TmxMapLoader.Parameters;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import se.sciion.quake2d.ai.behaviour.BehaviourTree;
import se.sciion.quake2d.ai.behaviour.InverterNode;
import se.sciion.quake2d.ai.behaviour.SelectorNode;
import se.sciion.quake2d.ai.behaviour.SequenceNode;
import se.sciion.quake2d.ai.behaviour.nodes.Attack;
import se.sciion.quake2d.ai.behaviour.nodes.HealthCheck;
import se.sciion.quake2d.ai.behaviour.nodes.MoveToNearest;
import se.sciion.quake2d.ai.behaviour.nodes.PickupItem;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.components.HealthComponent;
import se.sciion.quake2d.level.components.InventoryComponent;
import se.sciion.quake2d.level.components.PhysicsComponent;
import se.sciion.quake2d.level.components.PickupComponent;
import se.sciion.quake2d.level.components.PlayerInputComponent;
import se.sciion.quake2d.level.components.WeaponComponent;
import se.sciion.quake2d.level.items.Consumable;
import se.sciion.quake2d.level.items.Weapon;
import se.sciion.quake2d.level.system.Pathfinding;
import se.sciion.quake2d.level.system.PhysicsSystem;


public class LevelSandbox extends ApplicationAdapter {

	private AssetManager assets;

	OrthographicCamera camera;

	private TiledMap map;
	private TiledMapTileSet tileSet;
	private OrthogonalTiledMapRenderer renderer;
	
	private Level level;
	private RenderModel model;
	
	private Pathfinding pathfinding;
	private PhysicsSystem physicsSystem;

	private BehaviourTree tree;	

	
	@Override
	public void create() {
		
		Gdx.graphics.setWindowedMode((int) (800 * Gdx.graphics.getDensity()), (int) (600 * Gdx.graphics.getDensity()));
		// Set up level object
		Gdx.graphics.setSystemCursor(SystemCursor.Crosshair);

		level = new Level();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 30, 30);

		model = new RenderModel();
		physicsSystem = new PhysicsSystem();

		pathfinding = new Pathfinding(30, 30);

		loadAssets();

		pathfinding.update(physicsSystem);
	}

	@Override
	public void dispose() {

	}

	public void loadAssets() {
		assets = new AssetManager();
		assets.setLoader(Texture.class, new TextureLoader(new InternalFileHandleResolver()));
		assets.finishLoading();
		loadMap();
	}

	private void loadMap() {
		TmxMapLoader loader = new TmxMapLoader(new InternalFileHandleResolver());
		// TmxMapLoader.Parameters
		Parameters params = new Parameters();
		params.textureMinFilter = TextureFilter.Nearest;
		params.textureMagFilter = TextureFilter.Nearest;
		
		map = loader.load("levels/level_test.tmx", params);
		
		tileSet = map.getTileSets().getTileSet(0);
		renderer = new OrthogonalTiledMapRenderer(map,1.0f/64.0f);
		MapLayer structuralLayer = map.getLayers().get("Structures");
		for(MapObject o: structuralLayer.getObjects()) {
			RectangleMapObject r = (RectangleMapObject) o;
			Rectangle rect = r.getRectangle();
			float x = rect.x / 64.0f;
			float y = rect.y / 64.0f;
			float w = rect.width/64.0f;
			float h = rect.height/ 64.0f;
			String type = r.getProperties().get("type", String.class);
			
			if(type.equals("Static")) {
				Entity entity = level.createEntity();
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(w/2.0f,h/2.0f);
				entity.addComponent(physicsSystem.createComponent(x + w/2.0f, y + h/2.0f, BodyType.StaticBody, shape));
			}
			
		}
		
		MapLayer pickupLayer = map.getLayers().get("Pickups");
		for(MapObject o: pickupLayer.getObjects()) {
			RectangleMapObject r = (RectangleMapObject) o;
			Rectangle rect = r.getRectangle();
			float x = rect.x / 64.0f;
			float y = rect.y / 64.0f;
			float w = rect.width/64.0f;
			float h = rect.height/ 64.0f;
			String type = r.getProperties().get("type", String.class);
			
			if(type.equals("Consumable")) {
				Entity entity = level.createEntity(o.getName());
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(w/2.0f,h/2.0f);

				Vector2 origin = new Vector2(x + w/2.0f, y + h/2.0f);
				
				PhysicsComponent pickupPhysics = physicsSystem.createComponent(origin.x, origin.y, BodyType.DynamicBody,shape);
				entity.addComponent(pickupPhysics);
				
				int healthAmount = r.getProperties().get("amount", Integer.class);
				PickupComponent pickup = new PickupComponent(new Consumable("health",healthAmount, 0));
				physicsSystem.registerCallback(pickup, entity);
				entity.addComponent(pickup);
			}
			else if(type.equals("Weapon")) {
				Entity entity = level.createEntity(o.getName());
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(w/2.0f,h/2.0f);

				Vector2 origin = new Vector2(x + w/2.0f, y + h/2.0f);
				
				PhysicsComponent pickupPhysics = physicsSystem.createComponent(origin.x, origin.y, BodyType.DynamicBody,shape);
				entity.addComponent(pickupPhysics);
				
				float cooldown = r.getProperties().get("cooldown", Float.class);
				int bullets = r.getProperties().get("bullets", Integer.class);
				int capacity = r.getProperties().get("capacity",Integer.class);
				float knockback = r.getProperties().get("knockback", Float.class);
				float spread = r.getProperties().get("spread",Float.class);
				float speed = r.getProperties().get("speed", Float.class);
				Weapon wweapon = new Weapon(o.getName(),cooldown, bullets, capacity,knockback, spread, speed);
				PickupComponent pickup = new PickupComponent(wweapon);
				physicsSystem.registerCallback(pickup, entity);
				entity.addComponent(pickup);
			}
		}
		
		MapLayer spawnLayer = map.getLayers().get("Spawns");
		for(MapObject o: spawnLayer.getObjects()) {
			RectangleMapObject r = (RectangleMapObject) o;
			Rectangle rect = r.getRectangle();
			float x = rect.x / 64.0f;
			float y = rect.y / 64.0f;
			float w = rect.width/64.0f;
			float h = rect.height/ 64.0f;
			String type = r.getProperties().get("type", String.class);
			
			if(type.equals("PlayerSpawn")) {
				Entity player = level.createEntity("player");
				// Player components
				PlayerInputComponent playerMovement = new PlayerInputComponent(camera, pathfinding);

				CircleShape shape = new CircleShape();
				shape.setRadius(0.5f);
				PhysicsComponent playerPhysics = physicsSystem.createComponent(x + w/2.0f, y + h/2.0f, BodyType.DynamicBody, shape);
				WeaponComponent playerWeapon = new WeaponComponent(level,physicsSystem);

				HealthComponent playerHealth = new HealthComponent(o.getProperties().get("health", Integer.class), 20);
				physicsSystem.registerCallback(playerHealth, player);
				
				player.addComponent(playerHealth);
				player.addComponent(playerPhysics);
				player.addComponent(playerMovement);
				player.addComponent(playerWeapon);
				player.addComponent(new InventoryComponent());
			}
			else if(type.equals("BotSpawn")) {
				Entity entity = level.createEntity("bot");

				CircleShape shape = new CircleShape();
				shape.setRadius(0.5f);
				PhysicsComponent physics = physicsSystem.createComponent(x + w/2.0f, y + h/2.0f, BodyType.DynamicBody, shape);
				WeaponComponent weapon = new WeaponComponent(level,physicsSystem);
				HealthComponent health = new HealthComponent(o.getProperties().get("health", Integer.class),20);
				physicsSystem.registerCallback(health, entity);

				BotInputComponent botInput = new BotInputComponent(pathfinding);
				entity.addComponent(health);
				entity.addComponent(physics);
				entity.addComponent(weapon);
				entity.addComponent(new InventoryComponent());
				entity.addComponent(botInput);
				
				HealthCheck healthCheck = new HealthCheck(health, 0.5f);
				MoveToNearest pickupHealth = new MoveToNearest("health", level, pathfinding,physicsSystem, botInput, 1.0f);
				PickupItem pickupWeapon= new PickupItem("shotgun",level,pathfinding, botInput);
				Attack attackPlayer = new Attack(level.getEntities("player").first(), botInput);
				MoveToNearest moveToPlayer = new MoveToNearest("player",level ,pathfinding,physicsSystem, botInput, 10.0f);
				
				SequenceNode s1 = new SequenceNode(new InverterNode(healthCheck), pickupHealth);
				SequenceNode s2 = new SequenceNode(pickupWeapon, moveToPlayer, attackPlayer);
				SelectorNode s3 = new SelectorNode(s1,s2);
				
				tree = new BehaviourTree(s3);
			}
		}
		
	}
	
	@Override
	public void render() {
		camera.update();
		tree.tick();
		level.tick(Gdx.graphics.getDeltaTime());
		physicsSystem.update(Gdx.graphics.getDeltaTime());
		physicsSystem.cleanup();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
		model.setProjectionMatrix(camera.combined);

		//Matrix4 projection = camera.combined.cpy().scl(1.0f/64.0f);
		renderer.setView(camera);
		renderer.render();
		//pathfinding.render(model);

		model.begin();
		level.render(model);
		model.end();

		// Currently only Box2D debug renderer
		physicsSystem.render(camera.combined);
		
//		cooldown += Gdx.graphics.getDeltaTime();
//		if(cooldown >= 1.0f) {
//			try {
//				Graphviz.fromGraph(tree.toDot()).render(Format.PNG).toFile(new File("test/test.png"));
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			cooldown = 0;
//		}
//		
	}
	
	@Override
	public void resize(int width, int height) {
	}
}
