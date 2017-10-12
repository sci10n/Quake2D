package se.sciion.quake2d.level.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TmxMapLoader.Parameters;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import se.sciion.quake2d.ai.behaviour.BehaviourTree;
import se.sciion.quake2d.ai.behaviour.InverterNode;
import se.sciion.quake2d.ai.behaviour.SelectorNode;
import se.sciion.quake2d.ai.behaviour.SequenceNode;
import se.sciion.quake2d.ai.behaviour.SucceederNode;
import se.sciion.quake2d.ai.behaviour.nodes.AttackNearest;
import se.sciion.quake2d.ai.behaviour.nodes.CheckArmor;
import se.sciion.quake2d.ai.behaviour.nodes.CheckEntityDistance;
import se.sciion.quake2d.ai.behaviour.nodes.CheckHealth;
import se.sciion.quake2d.ai.behaviour.nodes.CheckWeapon;
import se.sciion.quake2d.ai.behaviour.nodes.MoveToNearest;
import se.sciion.quake2d.ai.behaviour.nodes.PickupArmor;
import se.sciion.quake2d.ai.behaviour.nodes.PickupDamageBoost;
import se.sciion.quake2d.ai.behaviour.nodes.PickupHealth;
import se.sciion.quake2d.ai.behaviour.nodes.PickupWeapon;
import se.sciion.quake2d.ai.behaviour.visualizer.BehaviourTreeVisualizer;
import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.graphics.SheetRegion;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.components.DamageBoostComponent;
import se.sciion.quake2d.level.components.HealthComponent;
import se.sciion.quake2d.level.components.InventoryComponent;
import se.sciion.quake2d.level.components.PhysicsComponent;
import se.sciion.quake2d.level.components.PickupComponent;
import se.sciion.quake2d.level.components.PlayerInputComponent;
import se.sciion.quake2d.level.components.SheetComponent;
import se.sciion.quake2d.level.components.SpriteComponent;
import se.sciion.quake2d.level.components.WeaponComponent;
import se.sciion.quake2d.level.items.ArmorRestore;
import se.sciion.quake2d.level.items.DamageBoost;
import se.sciion.quake2d.level.items.HealthRestore;
import se.sciion.quake2d.level.items.Item;
import se.sciion.quake2d.level.items.Weapon;
import se.sciion.quake2d.sandbox.LevelSandbox;

public class Environment implements Disposable{

	private final float TIMEOUT = 120.0f;
	private float ellapsed = 0.0f;
	
	private boolean running = false;
	
	private TiledMap map;
	private TiledMapTileSet tileSet;
	private TiledMapTileLayer overlayTiledLayer;
	private OrthogonalTiledMapRenderer renderer;

	private Level level;
	private PhysicsSystem physicsSystem;
	private Pathfinding pathfinding;
	private OrthographicCamera camera;
	private String mapPath;
	
	private AssetManager assets;
	
	public Environment(String mapPath, Level level, PhysicsSystem physicsSystem, Pathfinding pathfinding, OrthographicCamera camera, AssetManager assets) {
		this.level = level;
		this.physicsSystem = physicsSystem;
		this.pathfinding = pathfinding;
		this.camera = camera;
		this.mapPath = mapPath;
		this.assets = assets;
		
	}
	
	public void start() {
		loadMap();
		running = true;
	}
	
	private void loadMap() {
		Weapon.tags.clear();
		TmxMapLoader loader = new TmxMapLoader(new InternalFileHandleResolver());
		// TmxMapLoader.Parameters
		Parameters params = new Parameters();		
		map = loader.load(mapPath, params);
		
		tileSet = map.getTileSets().getTileSet(0);
		renderer = new OrthogonalTiledMapRenderer(map,1.0f/64.0f);
		
		overlayTiledLayer = (TiledMapTileLayer) map.getLayers().get("Overlay");

		TextureAtlas spriteSheet = assets.get("images/spritesheet.atlas",TextureAtlas.class);
		TextureRegion amountRegion = new TextureRegion(assets.get("images/amount.png", Texture.class));
		TextureRegion bulletRegion = new TextureRegion(assets.get("images/bullet.png", Texture.class));
		TextureRegion muzzleRegion = new TextureRegion(assets.get("images/muzzle.png", Texture.class));
		
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
				entity.addComponent(physicsSystem.createComponent(x + w/2.0f, y + h/2.0f, BodyType.StaticBody, shape, false));
			}
			
		}
		
		Weapon wweapon = null;
		
		MapLayer pickupLayer = map.getLayers().get("Pickups");
		for(MapObject o: pickupLayer.getObjects()) {
			RectangleMapObject r = (RectangleMapObject) o;
			Rectangle rect = r.getRectangle();
			float x = rect.x / 64.0f;
			float y = rect.y / 64.0f;
			float w = rect.width/64.0f;
			float h = rect.height/ 64.0f;
			String type = r.getProperties().get("type", String.class);
			
			Entity entity = level.createEntity(o.getName());
			
			HealthComponent health = new HealthComponent(8, 0, amountRegion, level);
			physicsSystem.registerCallback(health, entity);
			entity.addComponent(health);
			
			if(type.equals("Consumable")) {
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(w/2.0f,h/2.0f);

				Vector2 origin = new Vector2(x + w/2.0f, y + h/2.0f);
				
				PhysicsComponent pickupPhysics = physicsSystem.createComponent(origin.x, origin.y, BodyType.DynamicBody,shape, false);
				entity.addComponent(pickupPhysics);

				Item c = null;
				if(o.getName().equals("armor")) {
					int amountArmor = o.getProperties().get("amount", Integer.class);
					c = new ArmorRestore(o.getName(), amountArmor);
					
					SpriteComponent armorSprite = new SpriteComponent(tileSet.getTile(132 + 1).getTextureRegion(),
                            new Vector2(0.0f, 0.0f), new Vector2(-0.4f, -0.4f),
                            new Vector2(1.0f / 75.0f, 1.0f / 75.0f), 0.0f);
					entity.addComponent(armorSprite);
				} 
				else if(o.getName().equals("health")) {
					int amountHealth = o.getProperties().get("amount", Integer.class);
					c = new HealthRestore(o.getName(), amountHealth);
					
					SpriteComponent healthSprite = new SpriteComponent(tileSet.getTile(131 + 1).getTextureRegion(),
                            new Vector2(0.0f, 0.0f), new Vector2(-0.4f, -0.4f),
                            new Vector2(1.0f / 75.0f, 1.0f / 75.0f), 0.0f);
					entity.addComponent(healthSprite);
				} 
				else if(o.getName().equals("damage")) {
					float damageMul = o.getProperties().get("amount", Float.class);
					c = new DamageBoost(o.getName(), damageMul);
					
					SpriteComponent damageSprite = new SpriteComponent(tileSet.getTile(187 + 1).getTextureRegion(),
                            new Vector2(0.0f, 0.0f), new Vector2(-0.4f, -0.4f),
                            new Vector2(1.0f / 75.0f, 1.0f / 75.0f), 0.0f);
					entity.addComponent(damageSprite);
				} 
				
				PickupComponent pickup = new PickupComponent(level, c);
				physicsSystem.registerCallback(pickup, entity);
				entity.addComponent(pickup);
			}
			else if(type.equals("Weapon")) {
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(w/2.0f,h/2.0f);

				Vector2 origin = new Vector2(x + w/2.0f, y + h/2.0f);
				
				PhysicsComponent pickupPhysics = physicsSystem.createComponent(origin.x, origin.y, BodyType.DynamicBody,shape, false);
				entity.addComponent(pickupPhysics);
				
				float cooldown = r.getProperties().get("cooldown", Float.class);
				int bullets = r.getProperties().get("bullets", Integer.class);
				int capacity = r.getProperties().get("capacity",Integer.class);
				float knockback = r.getProperties().get("knockback", Float.class);
				float spread = r.getProperties().get("spread",Float.class);
				float speed = r.getProperties().get("speed", Float.class);
				int baseDamage = r.getProperties().get("damage", Integer.class);
				
				Weapon.tags.add(o.getName());
				if (o.getName().equals("shotgun")) {
					SpriteComponent shotgunSprite = new SpriteComponent(tileSet.getTile(158 + 1).getTextureRegion(),
					                                                  new Vector2(0.0f, 0.0f), new Vector2(-0.4f, -0.4f),
					                                                  new Vector2(1.0f / 75.0f, 1.0f / 75.0f), 0.0f);
					entity.addComponent(shotgunSprite);
				} else if (o.getName().equals("rifle")) {
					SpriteComponent rifleSprite = new SpriteComponent(tileSet.getTile(186 + 1).getTextureRegion(),
					                                                  new Vector2(0.0f, 0.0f), new Vector2(-0.4f, -0.4f),
					                                                  new Vector2(1.0f / 75.0f, 1.0f / 75.0f), 0.0f);
					entity.addComponent(rifleSprite);
				} else if (o.getName().equals("sniper")) {
					SpriteComponent sniperSprite = new SpriteComponent(tileSet.getTile(159 + 1).getTextureRegion(),
					                                                  new Vector2(0.0f, 0.0f), new Vector2(-0.4f, -0.4f),
					                                                  new Vector2(1.0f / 75.0f, 1.0f / 75.0f), 0.0f);
					entity.addComponent(sniperSprite);
				}

				wweapon = new Weapon(o.getName(),cooldown, bullets, capacity, knockback, spread, speed, baseDamage);
				PickupComponent pickup = new PickupComponent(level, wweapon);
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

				float bodySize = 0.25f;
				CircleShape shape = new CircleShape();
				shape.setRadius(bodySize);
				PhysicsComponent playerPhysics = physicsSystem.createComponent(x + w/2.0f, y + h/2.0f, BodyType.DynamicBody, shape, false);
				WeaponComponent playerWeapon = new WeaponComponent(level,physicsSystem, bulletRegion, muzzleRegion);

				SheetComponent playerSpriteSheet = new SheetComponent("stand");

				float sheetScale = 1.0f / 64.0f;

				SheetRegion standRegion = new SheetRegion();
				standRegion.texture = spriteSheet.findRegion("soldier1_stand");
				standRegion.scale = new Vector2(sheetScale, sheetScale);
				standRegion.offset = new Vector2(-0.25f, -0.34f);
				standRegion.origin = new Vector2(0.0f, 0.0f);
				standRegion.rotation = 0.0f;

				SheetRegion machineRegion = new SheetRegion();
				machineRegion.texture = spriteSheet.findRegion("soldier1_machine");
				machineRegion.scale = new Vector2(sheetScale, sheetScale);
				machineRegion.offset = new Vector2(-0.25f, -0.34f);
				machineRegion.origin = new Vector2(0.0f, 0.0f);
				machineRegion.rotation = 0.0f;

				SheetRegion silencerRegion = new SheetRegion();
				silencerRegion.texture = spriteSheet.findRegion("soldier1_silencer");
				silencerRegion.scale = new Vector2(sheetScale, sheetScale);
				silencerRegion.offset = new Vector2(-0.25f, -0.34f);
				silencerRegion.origin = new Vector2(0.0f, 0.0f);
				silencerRegion.rotation = 0.0f;

				SheetRegion gunRegion = new SheetRegion();
				gunRegion.texture = spriteSheet.findRegion("soldier1_gun");
				gunRegion.scale = new Vector2(sheetScale, sheetScale);
				gunRegion.offset = new Vector2(-0.25f, -0.34f);
				gunRegion.origin = new Vector2(0.0f, 0.0f);
				gunRegion.rotation = 0.0f;

				playerSpriteSheet.addRegion(standRegion, "stand");
				playerSpriteSheet.addRegion(machineRegion, "machine");
				playerSpriteSheet.addRegion(silencerRegion, "silencer");
				playerSpriteSheet.addRegion(gunRegion, "gun");

				HealthComponent playerHealth = new HealthComponent(o.getProperties().get("health", Integer.class),10, amountRegion, level);

				physicsSystem.registerCallback(playerHealth, player);
				
				player.addComponent(playerHealth);
				player.addComponent(playerPhysics);
				player.addComponent(playerMovement);
				player.addComponent(playerWeapon);
				player.addComponent(new InventoryComponent());
				player.addComponent(playerSpriteSheet);
				player.addComponent(new DamageBoostComponent());


			}
			else if(type.equals("BotSpawn")) {
				Entity entity = level.createEntity("player");

				float bodySize = 0.25f;
				CircleShape shape = new CircleShape();
				shape.setRadius(bodySize);

				PhysicsComponent physics = physicsSystem.createComponent(x + w/2.0f, y + h/2.0f, BodyType.DynamicBody, shape, false);
				WeaponComponent weapon = new WeaponComponent(level,physicsSystem, bulletRegion, muzzleRegion);
				HealthComponent health = new HealthComponent(o.getProperties().get("health", Integer.class),10, amountRegion, level);

				physicsSystem.registerCallback(health, entity);

				SheetComponent robotSpriteSheet = new SheetComponent("gun");

				float sheetScale = 1.0f / 64.0f;

				SheetRegion standRegion = new SheetRegion();
				standRegion.texture = spriteSheet.findRegion("robot1_stand");
				standRegion.scale = new Vector2(sheetScale, sheetScale);
				standRegion.offset = new Vector2(-0.25f, -0.34f);
				standRegion.origin = new Vector2(0.0f, 0.0f);
				standRegion.rotation = 0.0f;

				SheetRegion machineRegion = new SheetRegion();
				machineRegion.texture = spriteSheet.findRegion("robot1_machine");
				machineRegion.scale = new Vector2(sheetScale, sheetScale);
				machineRegion.offset = new Vector2(-0.25f, -0.34f);
				machineRegion.origin = new Vector2(0.0f, 0.0f);
				machineRegion.rotation = 0.0f;

				SheetRegion silencerRegion = new SheetRegion();
				silencerRegion.texture = spriteSheet.findRegion("robot1_silencer");
				silencerRegion.scale = new Vector2(sheetScale, sheetScale);
				silencerRegion.offset = new Vector2(-0.25f, -0.34f);
				silencerRegion.origin = new Vector2(0.0f, 0.0f);
				silencerRegion.rotation = 0.0f;

				SheetRegion gunRegion = new SheetRegion();
				gunRegion.texture = spriteSheet.findRegion("robot1_gun");
				gunRegion.scale = new Vector2(sheetScale, sheetScale);
				gunRegion.offset = new Vector2(-0.25f, -0.34f);
				gunRegion.origin = new Vector2(0.0f, 0.0f);
				gunRegion.rotation = 0.0f;

				robotSpriteSheet.addRegion(standRegion, "stand");
				robotSpriteSheet.addRegion(machineRegion, "machine");
				robotSpriteSheet.addRegion(silencerRegion, "silencer");
				robotSpriteSheet.addRegion(gunRegion, "gun");

				BotInputComponent botInput = new BotInputComponent(pathfinding, physicsSystem);

				entity.addComponent(health);
				entity.addComponent(physics);
				entity.addComponent(weapon);
				entity.addComponent(new InventoryComponent());
				entity.addComponent(botInput);
				entity.addComponent(robotSpriteSheet);
				entity.addComponent(new DamageBoostComponent());
				
				CheckArmor checkArmor = new CheckArmor(0.25f);
				CheckHealth checkHealth = new CheckHealth(0.50f);
				PickupHealth pickupHealth = new PickupHealth(level, "health");
				PickupArmor pickupArmor = new PickupArmor(level, "armor");
				PickupDamageBoost pickupBoost = new PickupDamageBoost(level, "damage");
				
				PickupWeapon pickupWeaponShotgun = new PickupWeapon("shotgun",level,pathfinding);
				PickupWeapon pickupWeaponRifle = new PickupWeapon("rifle",level,pathfinding);

				AttackNearest attackPlayer = new AttackNearest("player", level, physicsSystem);
				MoveToNearest moveToPlayer = new MoveToNearest("player",level ,pathfinding,physicsSystem, 0.0f, 5.0f);
				
				CheckEntityDistance distanceCheck = new CheckEntityDistance("player", 15, level);
				CheckEntityDistance otherDistanceCheck = new CheckEntityDistance("player", 5, level);
				CheckWeapon rifleCheck = new CheckWeapon("rifle");
				CheckWeapon shotgunCheck = new CheckWeapon("shotgun");
				
				SequenceNode s1 = new SequenceNode(new InverterNode(checkHealth), pickupHealth);
				SequenceNode s4 = new SequenceNode(new InverterNode(checkArmor), pickupArmor);
				SequenceNode s2 = new SequenceNode(new SucceederNode(new SelectorNode(new SequenceNode(otherDistanceCheck, new InverterNode(shotgunCheck), pickupWeaponShotgun), new SequenceNode(distanceCheck, new InverterNode(rifleCheck), pickupWeaponRifle))),  new SucceederNode(pickupBoost), moveToPlayer, attackPlayer);
				SelectorNode s3 = new SelectorNode(s1, s4, s2);
				
//				TreePool pool = new TreePool();
				BehaviourTree tree = new BehaviourTree(s3);
//				tree.randomize(pool.getPrototypes(level, physicsSystem, pathfinding));

				botInput.setBehaviourTree(tree);
				BehaviourTreeVisualizer.getInstance().setDebugBot(botInput);
			}
		}
	}

	private void inspectBehaviourTree() {
		boolean isPaused = BehaviourTreeVisualizer.getInstance().isPaused();
		// Choose a bot's behaviour tree to inspect.
		if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
			Vector3 screenMousePosition = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0.0f);
			Vector3 mousePosition = camera.unproject(screenMousePosition);
			Array<PhysicsComponent> components = physicsSystem.queryComponentAt(mousePosition.x, mousePosition.y);

			if(components.size > 0){
				PhysicsComponent component = components.first();
				if (component != null) {
					if(component.getParent() != null){
						BotInputComponent newDebugBot = component.getParent().getComponent(ComponentTypes.BotInput);
						if(newDebugBot != null) BehaviourTreeVisualizer.getInstance().setDebugBot(newDebugBot);
					}
				}
			}
		// Here we instead just swap between the trees.
		} else if (Gdx.input.isKeyJustPressed(Keys.TAB)) {
			BotInputComponent currentBot = BehaviourTreeVisualizer.getInstance().getDebugBot();
			for (Entity e : level.getEntities("player")) {
				BotInputComponent bot = e.getComponent(ComponentTypes.BotInput);
				if (bot != null && bot != currentBot) {
					BehaviourTreeVisualizer.getInstance().setDebugBot(bot);
				}
			}
		}
	}

	public void render(RenderModel model) {
		inspectBehaviourTree();

		renderer.setView(camera);
		int[] layers = {0, 1, 2};
		renderer.render(layers);
		
		model.begin();
		level.render(model);
		model.end();
		
		renderer.getBatch().begin();
		renderer.renderTileLayer(overlayTiledLayer);
		renderer.getBatch().end();
	}
	
	public void tick(float delta) {
		ellapsed += delta;
		if(ellapsed >= TIMEOUT) {
			running = false;
		}
		
		int alivePlayers = 0;
		for(Entity e: level.getEntities("player")){
			HealthComponent c = e.getComponent(ComponentTypes.Health);
			if(c != null && !c.isDead()){
				alivePlayers++;
			}
		}
		if(alivePlayers <= 1 && !LevelSandbox.DEBUG) {
			running = false;
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void stop() {
		running = false;
	}

	@Override
	public void dispose() {
		map.dispose();
		renderer.dispose();
		BehaviourTreeVisualizer.getInstance().setDebugBot(null);
	}
	
}
