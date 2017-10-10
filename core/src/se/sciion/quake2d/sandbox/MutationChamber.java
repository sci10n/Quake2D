package se.sciion.quake2d.sandbox;

import javax.sound.midi.Sequence;

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

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Array;

public class MutationChamber extends ApplicationAdapter {
	private Level level;
	private BehaviourTreeVisualizer visualizer1;
	private BehaviourTreeVisualizer visualizer2;
	private BehaviourTree tree1;
	private BehaviourTree tree2;
	
	private Array<BehaviourNode> prototypes;
	
	public MutationChamber() {
	}
	
	@Override
	public void create() {
		
		level = new Level();
		visualizer1 = new BehaviourTreeVisualizer(Gdx.graphics.getWidth());
		visualizer2 = new BehaviourTreeVisualizer(Gdx.graphics.getWidth());
		
		Entity e1 = level.createEntity("player");
		Entity e2 = level.createEntity("player");
		
		level.createEntity("shotgun");
		level.createEntity("sniper");
		
		Weapon.tags.add("shotgun");
		Weapon.tags.add("sniper");
				
		BotInputComponent input1 = new BotInputComponent(null, null);
		BotInputComponent input2 = new BotInputComponent(null, null);
		
		tree1 = new BehaviourTree(new SelectorNode(new InverterNode(new PickupWeapon("shotgun", level, null)), new SucceederNode(new PickupArmor(level, "armor"))));
		tree2 = new BehaviourTree(new PickupHealth(level, "health"));
		
		input1.setBehaviourTree(tree1);
		input2.setBehaviourTree(tree2);
		
		visualizer1.setDebugBot(input1);
		visualizer2.setDebugBot(input2);

		tree1.crossover(tree2, 1.0f);
		
		prototypes = createPrototypes();
		
	}
	
	private Array<BehaviourNode> createPrototypes(){
		Array<BehaviourNode> prototypes = new Array<BehaviourNode>();
		prototypes.add(new AttackNearest("", level));
		prototypes.add(new CheckArmor(0.0f));
		prototypes.add(new CheckEntityDistance("", 0.0f,level));
		prototypes.add(new CheckHealth(0.0f));
		prototypes.add(new CheckWeapon(""));
		prototypes.add(new MoveToNearest("", level, null, null, 0.0f));
		prototypes.add(new PickupArmor(level, "armor"));
		prototypes.add(new PickupDamageBoost(level, "damage"));
		prototypes.add(new PickupHealth(level, "health"));
		prototypes.add(new PickupWeapon("", level, null));
		prototypes.add(new InverterNode());
		prototypes.add(new ParallelNode());
		prototypes.add(new SelectorNode());
		prototypes.add(new SequenceNode());
		prototypes.add(new SucceederNode());
		return prototypes;
	}
	@Override
	public void render() {
		if(Gdx.input.isKeyJustPressed(Keys.C)){
			tree1.crossover(tree2, 1.0f);
		}
		if(Gdx.input.isKeyJustPressed(Keys.M)){
			tree1.mutate(1.0f);
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.R)){
			tree1.randomize(prototypes);
		}
	}
	
}
