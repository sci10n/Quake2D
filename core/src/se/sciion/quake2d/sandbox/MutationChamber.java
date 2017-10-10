package se.sciion.quake2d.sandbox;

import se.sciion.quake2d.ai.behaviour.BehaviourTree;
import se.sciion.quake2d.ai.behaviour.InverterNode;
import se.sciion.quake2d.ai.behaviour.SelectorNode;
import se.sciion.quake2d.ai.behaviour.SucceederNode;
import se.sciion.quake2d.ai.behaviour.nodes.PickupArmor;
import se.sciion.quake2d.ai.behaviour.nodes.PickupHealth;
import se.sciion.quake2d.ai.behaviour.nodes.PickupWeapon;
import se.sciion.quake2d.ai.behaviour.visualizer.BTVisualizer;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.components.BotInputComponent;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class MutationChamber extends ApplicationAdapter {
	private Level level;
	private BTVisualizer visualizer1;
	private BTVisualizer visualizer2;
	private BehaviourTree tree1;
	private BehaviourTree tree2;
	
	public MutationChamber() {
	}
	
	@Override
	public void create() {
		level = new Level();
		visualizer1 = new BTVisualizer(Gdx.graphics.getWidth());
		visualizer2 = new BTVisualizer(Gdx.graphics.getWidth());
		
		Entity e1 = level.createEntity();
		Entity e2 = level.createEntity();
		
		BotInputComponent input1 = new BotInputComponent(null, null);
		BotInputComponent input2 = new BotInputComponent(null, null);
		
		tree1 = new BehaviourTree(new SelectorNode(new InverterNode(new PickupArmor(level, "armor")), new SucceederNode(new PickupArmor(level, "armor"))));
		tree2 = new BehaviourTree(new PickupHealth(level, "health"));
		
		input1.setBehaviourTree(tree1);
		input2.setBehaviourTree(tree2);
		
		visualizer1.setDebugBot(input1);
		visualizer2.setDebugBot(input2);

		tree1.crossover(tree2, 1.0f);
	}
	
	@Override
	public void render() {
		if(Gdx.input.isKeyJustPressed(Keys.ANY_KEY)){
			tree1.crossover(tree2, 1.0f);
		}
	}
	
}
