package se.sciion.quake2d.sandbox;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import se.sciion.quake2d.ai.behaviour.BehaviourTree;
import se.sciion.quake2d.ai.behaviour.InverterNode;
import se.sciion.quake2d.ai.behaviour.ParallelNode;
import se.sciion.quake2d.ai.behaviour.SelectorNode;
import se.sciion.quake2d.ai.behaviour.SequenceNode;
import se.sciion.quake2d.ai.behaviour.SucceederNode;
import se.sciion.quake2d.ai.behaviour.nodes.AttackNearest;
import se.sciion.quake2d.ai.behaviour.nodes.CheckEntityDistance;
import se.sciion.quake2d.ai.behaviour.nodes.CheckHealth;
import se.sciion.quake2d.ai.behaviour.nodes.MoveToNearest;
import se.sciion.quake2d.ai.behaviour.nodes.PickupArmor;
import se.sciion.quake2d.ai.behaviour.nodes.PickupDamageBoost;
import se.sciion.quake2d.ai.behaviour.nodes.PickupHealth;
import se.sciion.quake2d.ai.behaviour.nodes.PickupWeapon;
import se.sciion.quake2d.ai.behaviour.visualizer.BTVisualizer;
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
import se.sciion.quake2d.level.system.HealthListener;
import se.sciion.quake2d.level.system.Pathfinding;
import se.sciion.quake2d.level.system.SoundSystem;
import se.sciion.quake2d.level.system.PhysicsSystem;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
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
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;


public class LevelSandbox extends ApplicationAdapter implements HealthListener {

	private AssetManager assets;

	OrthographicCamera camera;

	private TiledMap map;
	private TiledMapTileSet tileSet;
	private TextureAtlas spriteSheet;
	private TextureRegion bulletTexture;
	private TextureRegion amountTexture;
	private TextureRegion muzzleTexture;
	private TiledMapTileLayer overlayTiledLayer;

	private OrthogonalTiledMapRenderer renderer;
	
	private Level level;
	private RenderModel model;
	
	private int playersAlive = 0;
    private boolean debugging = false;
    private boolean gameEnded = false;
	private Pathfinding pathfinding;
	private BTVisualizer visualizer;

	private SoundSystem soundSystem;
	private PhysicsSystem physicsSystem;
	
	private final Array<String> levels;
	
	private int width;
	private int height;
	
	public LevelSandbox(String ... levels) {
		this.levels = new Array<String>(levels);
	}
	
	@Override
	public void create() {
		width = (int)(2*600 * Gdx.graphics.getDensity());
		height = (int)(2*600 * Gdx.graphics.getDensity());
		
		Gdx.graphics.setTitle("Quake 2D");
		Gdx.graphics.setWindowedMode(width, height);

		setup();
		loadAssets();
		loadMap(levels.random());
	}

	public void setup() {
		level = new Level();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 30, 30);

		model = new RenderModel();
		physicsSystem = new PhysicsSystem();
		pathfinding = new Pathfinding(30, 30, level);

		visualizer = new BTVisualizer(width, camera, physicsSystem);

		// Reset the game.
		gameEnded = false;
		playersAlive = 0;

		pathfinding.update(physicsSystem);
	}
	
	@Override
	public void dispose() {
		assets.dispose();
		map.dispose();
		soundSystem.dispose();
		model.spriteRenderer.dispose();
		physicsSystem.cleanup();
		visualizer.setRunning(false);
	}

	public void loadAssets() {
		assets = new AssetManager();
		assets.setLoader(Texture.class, new TextureLoader(new InternalFileHandleResolver()));
		spriteSheet = new TextureAtlas(Gdx.files.internal("images/spritesheet.atlas"));

		bulletTexture = new TextureRegion(new Texture(Gdx.files.internal("images/bullet.png")));
		amountTexture = new TextureRegion(new Texture(Gdx.files.internal("images/amount.png")));
		muzzleTexture = new TextureRegion(new Texture(Gdx.files.internal("images/muzzle.png")));

		soundSystem = SoundSystem.getInstance();
		soundSystem.addSound("armor", Gdx.audio.newSound(Gdx.files.internal("audio/armor.wav")));
		soundSystem.addSound("damage", Gdx.audio.newSound(Gdx.files.internal("audio/damage.wav")));
		soundSystem.addSound("fight", Gdx.audio.newSound(Gdx.files.internal("audio/fight.wav")));
		soundSystem.addSound("health", Gdx.audio.newSound(Gdx.files.internal("audio/health.wav")));
		soundSystem.addSound("hit", Gdx.audio.newSound(Gdx.files.internal("audio/hit.wav")));
		soundSystem.addSound("impressive", Gdx.audio.newSound(Gdx.files.internal("audio/impressive.wav")));
		soundSystem.addSound("move1", Gdx.audio.newSound(Gdx.files.internal("audio/move1.wav")));
		soundSystem.addSound("move2", Gdx.audio.newSound(Gdx.files.internal("audio/move2.wav")));
		soundSystem.addSound("rifle", Gdx.audio.newSound(Gdx.files.internal("audio/rifle.wav")));
		soundSystem.addSound("shotgun", Gdx.audio.newSound(Gdx.files.internal("audio/shotgun.wav")));
		soundSystem.addSound("sniper", Gdx.audio.newSound(Gdx.files.internal("audio/sniper.wav")));
		soundSystem.addSound("weapon", Gdx.audio.newSound(Gdx.files.internal("audio/weapon.wav")));
		soundSystem.addMusic("music", Gdx.audio.newMusic(Gdx.files.internal("audio/music.ogg")));
		soundSystem.loopMusic("music");

		assets.finishLoading();
	}

	private void loadMap(String mapPath) {
		TmxMapLoader loader = new TmxMapLoader(new InternalFileHandleResolver());
		// TmxMapLoader.Parameters
		Parameters params = new Parameters();		
		map = loader.load(mapPath, params);
		
		tileSet = map.getTileSets().getTileSet(0);
		renderer = new OrthogonalTiledMapRenderer(map,1.0f/64.0f);

		overlayTiledLayer = (TiledMapTileLayer) map.getLayers().get("Overlay");

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
				
				PickupComponent pickup = new PickupComponent(c);
				physicsSystem.registerCallback(pickup, entity);
				entity.addComponent(pickup);
			}
			else if(type.equals("Weapon")) {
				Entity entity = level.createEntity(o.getName());
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

				Weapon wweapon = new Weapon(o.getName(),cooldown, bullets, capacity, knockback, spread, speed, baseDamage);
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
				playersAlive += 1;
				Entity player = level.createEntity("player");
				// Player components
				PlayerInputComponent playerMovement = new PlayerInputComponent(camera, pathfinding);

				float bodySize = 0.25f;
				CircleShape shape = new CircleShape();
				shape.setRadius(bodySize);
				PhysicsComponent playerPhysics = physicsSystem.createComponent(x + w/2.0f, y + h/2.0f, BodyType.DynamicBody, shape, false);
				WeaponComponent playerWeapon = new WeaponComponent(level,physicsSystem, bulletTexture, muzzleTexture);

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

				HealthComponent playerHealth = new HealthComponent(o.getProperties().get("health", Integer.class),10, amountTexture);
				playerHealth.addListener(this);

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
				playersAlive += 1;
				Entity entity = level.createEntity("player");

				float bodySize = 0.25f;
				CircleShape shape = new CircleShape();
				shape.setRadius(bodySize);

				PhysicsComponent physics = physicsSystem.createComponent(x + w/2.0f, y + h/2.0f, BodyType.DynamicBody, shape, false);
				WeaponComponent weapon = new WeaponComponent(level,physicsSystem, bulletTexture, muzzleTexture);
				HealthComponent health = new HealthComponent(o.getProperties().get("health", Integer.class),10, amountTexture);
				health.addListener(this);

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
				
				CheckHealth checkHealth = new CheckHealth(0.25f);
				PickupHealth pickupHealth = new PickupHealth(level, "health");
				PickupArmor pickupArmor = new PickupArmor(level, "armor");
				PickupDamageBoost pickupBoost = new PickupDamageBoost(level, "damage");
				
				PickupWeapon pickupWeaponShotgun = new PickupWeapon("shotgun",level,pathfinding);
				PickupWeapon pickupWeaponRifle = new PickupWeapon("rifle",level,pathfinding);

				AttackNearest attackPlayer = new AttackNearest("player", level);
				MoveToNearest moveToPlayer = new MoveToNearest("player",level ,pathfinding,physicsSystem, 10.0f);
				
				CheckEntityDistance distanceCheck = new CheckEntityDistance("player", 15, level);
				
				SequenceNode s1 = new SequenceNode(new InverterNode(checkHealth), pickupHealth);
				SequenceNode s2 = new SequenceNode(new ParallelNode(1,new SequenceNode(distanceCheck, pickupWeaponShotgun), new SequenceNode(new InverterNode(distanceCheck), pickupWeaponRifle)),  new SucceederNode(pickupArmor), new SucceederNode(pickupBoost), moveToPlayer, attackPlayer);
				SelectorNode s3 = new SelectorNode(s1,s2);
				
				BehaviourTree tree = new BehaviourTree(s3);
				botInput.setBehaviourTree(tree);
			}
		}

		// Let's rock and roll baby! :)
		soundSystem.playSound("fight");
	}
	
	public void restart() {
		// Rewards a player or bot that won.
		soundSystem.playSound("impressive");
		visualizer.setRunning(false);
		setup();
		loadMap(levels.random());
	}
	
	@Override
	public void render() {
		if (Gdx.input.isKeyJustPressed(Keys.O))
			debugging = !debugging;
        if (Gdx.input.isKeyJustPressed(Keys.M))
            soundSystem.toggleMute();

		camera.update();

		if (!visualizer.pause()) {
			level.tick(Gdx.graphics.getDeltaTime());
			physicsSystem.update(Gdx.graphics.getDeltaTime());
			physicsSystem.cleanup();
			pathfinding.update(physicsSystem);
		}

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
		model.setProjectionMatrix(camera.combined);

		renderer.setView(camera);
		int[] layers = {0, 1, 2};
		renderer.render(layers);

		model.begin();
		level.render(model);
		model.end();

		renderer.getBatch().begin();
		renderer.renderTileLayer(overlayTiledLayer);
		renderer.getBatch().end();

		if (debugging) {
			pathfinding.render(model);
			physicsSystem.render(camera.combined);
			level.debugRender(model);
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.Q) || gameEnded)
			restart(); // Restart the game never terminate.
	}
	
	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void onStatusChanged(HealthComponent healthComponent) {
		if (healthComponent.isDead()) --playersAlive;
		if (playersAlive <= 1) {
			gameEnded = true;
		}
	}
}
