package se.sciion.quake2d.sandbox;

import se.sciion.quake2d.ai.behaviour.BehaviourNode;
import se.sciion.quake2d.ai.behaviour.BehaviourTree;
import se.sciion.quake2d.ai.behaviour.InverterNode;
import se.sciion.quake2d.ai.behaviour.ParallelNode;
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
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.items.Weapon;
import se.sciion.quake2d.level.system.Pathfinding;
import se.sciion.quake2d.level.system.PhysicsSystem;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class LoaderSandbox extends ApplicationAdapter {
	private BehaviourTree behaviourTree;
	private BotInputComponent dummyBot;
	private Pathfinding dummyPathfinder;
	private PhysicsSystem dummyPhysics;
	private Entity dummyEntity;
	private Level dummyLevel;

	private Array<BehaviourNode> createPrototypes(){
		Array<BehaviourNode> prototypes = new Array<BehaviourNode>();
		// prototypes.add(new AttackNearest("", dummyLevel));
		prototypes.add(new CheckArmor(0.0f));
		prototypes.add(new CheckEntityDistance("", 0.0f, dummyLevel));
		prototypes.add(new CheckHealth(0.0f));
		prototypes.add(new CheckWeapon(""));
		prototypes.add(new MoveToNearest("", dummyLevel, dummyPathfinder, dummyPhysics, 0.0f, 15.0f));
		prototypes.add(new PickupArmor(dummyLevel, "armor"));
		prototypes.add(new PickupDamageBoost(dummyLevel, "damage"));
		prototypes.add(new PickupHealth(dummyLevel, "health"));
		prototypes.add(new PickupWeapon("", dummyLevel, dummyPathfinder));
		prototypes.add(new InverterNode());
		prototypes.add(new ParallelNode());
		prototypes.add(new SelectorNode());
		prototypes.add(new SequenceNode());
		prototypes.add(new SucceederNode());
		return prototypes;
	}

	@Override
	public void create() {
		dummyLevel = new Level();
		dummyPhysics = new PhysicsSystem();
		dummyPathfinder = new Pathfinding(30, 30, dummyLevel);
		
		Weapon.tags.add("sniper");
		Weapon.tags.add("shotgun");

		dummyEntity = dummyLevel.createEntity("player");
		BehaviourTreeVisualizer.getInstance(Gdx.graphics.getWidth());
		dummyBot = new BotInputComponent(dummyPathfinder, dummyPhysics);
		dummyEntity.addComponent(dummyBot);
		behaviourTree = new BehaviourTree();
		// behaviourTree.randomize(createPrototypes());
		dummyBot.setBehaviourTree(behaviourTree);
		BehaviourTreeVisualizer.getInstance().setDebugBot(dummyBot);
	}
	
	@Override
	public void render() {
		if (Gdx.input.isKeyJustPressed(Keys.R)) {
			// behaviourTree.randomize(createPrototypes());
		} else if (Gdx.input.isKeyJustPressed(Keys.S)) {
			saveBehaviourTree(behaviourTree, "behaviours/tree.json");
		} else if (Gdx.input.isKeyJustPressed(Keys.L)) {
			behaviourTree = loadBehaviourTree("behaviours/tree.json");
		}
	}

	private BehaviourTree loadBehaviourTree(String filePath) {
		FileHandle fileHandle = Gdx.files.internal(filePath);
		String behaviourTreeJson = fileHandle.readString();
		return BehaviourTree.fromJson(behaviourTreeJson);
	}

	private void saveBehaviourTree(BehaviourTree behaviourTree, String filePath) {
		FileHandle fileHandle = Gdx.files.local(filePath);
		String behaviourTreeJson = behaviourTree.toJson();
		fileHandle.writeString(behaviourTreeJson, false);
	}
}
