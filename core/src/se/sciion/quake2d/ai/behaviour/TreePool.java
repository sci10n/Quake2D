package se.sciion.quake2d.ai.behaviour;

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
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.system.Pathfinding;
import se.sciion.quake2d.level.system.PhysicsSystem;

import com.badlogic.gdx.utils.Array;

public class TreePool {

	
	private Array<BehaviourTree> population;
	private float crossoverChance = 0.2f;
	private float mutationChance = 0.2f;
	
	public TreePool(){
		population = new Array<BehaviourTree>();
	}
	
	
	// Get a number of pooled trees based on their fitness
	public void select(){
		
	}
	
	public void mutate(){
		BehaviourTree tree = population.random();
		tree.mutate(mutationChance);
	}
	
	public void crossover(){
		
		BehaviourTree tree1 = population.random();
		BehaviourTree tree2 = population.random();
		tree1.crossover(tree2, crossoverChance);
	}
	
	public Array<BehaviourNode> getPrototypes(Level level, PhysicsSystem physics, Pathfinding pathfinding){
		Array<BehaviourNode> prototypes = new Array<BehaviourNode>();
		prototypes.add(new AttackNearest("", level));
		prototypes.add(new CheckArmor(0.0f));
		prototypes.add(new CheckEntityDistance("", 0.0f,level));
		prototypes.add(new CheckHealth(0.0f));
		prototypes.add(new CheckWeapon(""));
		prototypes.add(new MoveToNearest("", level, pathfinding,physics, 0.0f));
		prototypes.add(new PickupArmor(level, "armor"));
		prototypes.add(new PickupDamageBoost(level, "damage"));
		prototypes.add(new PickupHealth(level, "health"));
		prototypes.add(new PickupWeapon("", level, pathfinding));
		prototypes.add(new InverterNode());
		prototypes.add(new ParallelNode());
		prototypes.add(new SelectorNode());
		prototypes.add(new SequenceNode());
		prototypes.add(new SucceederNode());
		return prototypes;
	}
	
}
