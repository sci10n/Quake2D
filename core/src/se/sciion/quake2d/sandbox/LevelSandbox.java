package se.sciion.quake2d.sandbox;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;

import java.util.HashMap;

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

import se.sciion.quake2d.ai.behaviour.BehaviourTree;
import se.sciion.quake2d.ai.behaviour.SelectorNode;
import se.sciion.quake2d.ai.behaviour.SequenceNode;
import se.sciion.quake2d.ai.behaviour.nodes.Attack;
import se.sciion.quake2d.ai.behaviour.nodes.MoveToEntity;
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
import se.sciion.quake2d.level.items.Items;
import se.sciion.quake2d.level.system.Pathfinding;
import se.sciion.quake2d.level.system.PhysicsSystem;

public class LevelSandbox extends ApplicationAdapter {

	private AssetManager assets;

	OrthographicCamera camera;

	private Level level;
	private RenderModel model;
	private Pathfinding pathfinding;
	// Request queue for inter-entity/system communication.
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

		{
			Entity playerEntity = level.createEntity("player");
			// Player components
			PlayerInputComponent playerMovement = new PlayerInputComponent(camera, pathfinding);

			CircleShape shape = new CircleShape();
			shape.setRadius(0.5f);
			PhysicsComponent playerPhysics = physicsSystem.createComponent(10.0f, 10.0f, BodyType.DynamicBody, shape);
			WeaponComponent playerWeapon = new WeaponComponent(level,physicsSystem);

			HealthComponent playerHealth = new HealthComponent(10);
			physicsSystem.registerCallback(playerHealth, playerEntity);
			
			playerEntity.addComponent(playerHealth);
			playerEntity.addComponent(playerPhysics);
			playerEntity.addComponent(playerMovement);
			playerEntity.addComponent(playerWeapon);
			playerEntity.addComponent(new InventoryComponent(Items.Shotgun));
		}

		// Sniper weapon pickup
		{
//			Entity sniperPickup = level.createEntity("sniper");
//			PolygonShape boxShape = new PolygonShape();
//			boxShape.setAsBox(0.5f, 0.5f);
//			Vector2 origin = new Vector2(5, 15);
//
//			PhysicsComponent pickupPhysics = physicsSystem.createComponent(origin.x, origin.y, BodyType.DynamicBody,
//					boxShape);
//			sniperPickup.addComponent(pickupPhysics);
//			PickupComponent pickup = new PickupComponent(Items.Sniper);
//			physicsSystem.registerCallback(pickup, sniperPickup);
//			sniperPickup.addComponent(pickup);

		}

		// Shotgun weapon pickup
		{
			Entity shotgunPickup = level.createEntity("shotgun");
			PolygonShape boxShape = new PolygonShape();
			boxShape.setAsBox(0.5f, 0.5f);

			Vector2 origin = new Vector2(5, 25);
			PhysicsComponent pickupPhysics = physicsSystem.createComponent(origin.x, origin.y, BodyType.DynamicBody,boxShape);
			shotgunPickup.addComponent(pickupPhysics);
			PickupComponent pickup = new PickupComponent(Items.Shotgun);
			physicsSystem.registerCallback(pickup, shotgunPickup);
			shotgunPickup.addComponent(pickup);

		}
		{
			Entity healthPickup = level.createEntity("health");
			PolygonShape boxShape = new PolygonShape();
			boxShape.setAsBox(0.5f, 0.5f);

			Vector2 origin = new Vector2(20, 25);
			PhysicsComponent pickupPhysics = physicsSystem.createComponent(origin.x, origin.y, BodyType.DynamicBody,boxShape);
			healthPickup.addComponent(pickupPhysics);
			PickupComponent pickup = new PickupComponent(new Consumable(1, 0));
			physicsSystem.registerCallback(pickup, healthPickup);
			healthPickup.addComponent(pickup);
		}
		{
			Entity healthPickup = level.createEntity("health");
			PolygonShape boxShape = new PolygonShape();
			boxShape.setAsBox(0.5f, 0.5f);

			Vector2 origin = new Vector2(21, 25);
			PhysicsComponent pickupPhysics = physicsSystem.createComponent(origin.x, origin.y, BodyType.DynamicBody,boxShape);
			healthPickup.addComponent(pickupPhysics);
			PickupComponent pickup = new PickupComponent(new Consumable(1, 0));
			physicsSystem.registerCallback(pickup, healthPickup);
			healthPickup.addComponent(pickup);
		}
		// Bot dummy
		{
			Entity entity = level.createEntity("bot");

			CircleShape shape = new CircleShape();
			shape.setRadius(0.5f);
			PhysicsComponent botPhysics = physicsSystem.createComponent(12.0f, 4.0f, BodyType.DynamicBody, shape);
			WeaponComponent botWeapon = new WeaponComponent(level,physicsSystem);
			HealthComponent botHealth = new HealthComponent(10);
			physicsSystem.registerCallback(botHealth, entity);

			BotInputComponent botInput = new BotInputComponent(pathfinding);
			entity.addComponent(botHealth);
			entity.addComponent(botPhysics);
			entity.addComponent(botWeapon);
			entity.addComponent(new InventoryComponent());
			entity.addComponent(botInput);
			
			MoveToEntity node1 = new MoveToEntity(level.getEntity("shotgun"), physicsSystem, botInput, 1.0f);
			Attack node2 = new Attack(level.getEntity("player"), botInput);
			MoveToEntity node3 = new MoveToEntity(level.getEntity("player"), physicsSystem, botInput, 20.0f);
			
			SequenceNode sequence = new SequenceNode(node3, node2);
			SelectorNode selector = new SelectorNode(node1, sequence);

			tree = new BehaviourTree(selector);

		}
		// Static level entity
		{
			Entity leftWallEntity = level.createEntity();
			Entity rightWallEntity = level.createEntity();
			Entity downWallEntity = level.createEntity();
			Entity upWallEntity = level.createEntity();

			PolygonShape verticalBox = new PolygonShape();
			PolygonShape horizontalBox = new PolygonShape();

			verticalBox.setAsBox(0.5f, 15.0f);
			horizontalBox.setAsBox(15.0f, 0.5f);

			PhysicsComponent leftWall = physicsSystem.createComponent(0, 15, BodyType.StaticBody, verticalBox);
			PhysicsComponent rightWall = physicsSystem.createComponent(30, 15, BodyType.StaticBody, verticalBox);

			PhysicsComponent upWall = physicsSystem.createComponent(15, 0, BodyType.StaticBody, horizontalBox);
			PhysicsComponent downWall = physicsSystem.createComponent(15, 30, BodyType.StaticBody, horizontalBox);

			leftWallEntity.addComponent(leftWall);
			rightWallEntity.addComponent(rightWall);
			downWallEntity.addComponent(downWall);
			upWallEntity.addComponent(upWall);

			{
				Entity wall = level.createEntity();
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(5, 5);
				wall.addComponent(physicsSystem.createComponent(15, 15, BodyType.StaticBody, shape));
			}
			{
				Entity wall = level.createEntity();
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(1, 3);
				wall.addComponent(physicsSystem.createComponent(15, 25, BodyType.StaticBody, shape));
			}
			{
				Entity wall = level.createEntity();
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(1, 2);
				wall.addComponent(physicsSystem.createComponent(25, 25, BodyType.StaticBody, shape));
			}
			{
				Entity wall = level.createEntity();
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(1, 4);
				wall.addComponent(physicsSystem.createComponent(5, 20, BodyType.StaticBody, shape));
			}
			{
				Entity wall = level.createEntity();
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(4, 1);
				wall.addComponent(physicsSystem.createComponent(6, 5, BodyType.StaticBody, shape));
			}
			{
				Entity wall = level.createEntity();
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(4, 1);
				wall.addComponent(physicsSystem.createComponent(24, 5, BodyType.StaticBody, shape));
			}
		}

		pathfinding.update(physicsSystem);
	}

	@Override
	public void dispose() {

	}

	public void loadAssets() {
		assets = new AssetManager();
		assets.setLoader(Texture.class, new TextureLoader(new InternalFileHandleResolver()));
		assets.finishLoading();
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

		//pathfinding.render(model);

		model.begin();
		level.render(model);
		model.end();

		// Currently only Box2D debug renderer
		physicsSystem.render(camera.combined);

	}

	@Override
	public void resize(int width, int height) {
	}
}
