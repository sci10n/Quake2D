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
import se.sciion.quake2d.level.Statistics;
import se.sciion.quake2d.level.system.Pathfinding;
import se.sciion.quake2d.level.system.PhysicsSystem;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Trees {

	private class Candidate {
		public BehaviourTree tree;
		public float fitness;
	}
	
	private int populationLimit = 5;
	private Array<BehaviourTree> population;
	private Array<BehaviourNode> prototypes;
	
	private float crossoverChance = 0.2f;
	private float mutationChance = 0.2f;
	
	public Trees(){
	}
	
	
	
	// Get a number of pooled trees based on their fitness
	public void select(Statistics stats){
		Array<BehaviourTree> selectedTrees = new Array<BehaviourTree>();
		Array<Candidate> candidates = new Array<Trees.Candidate>();
		
		float sumFitness = 1.0f;
		for(BehaviourTree tree: population){
			sumFitness += stats.getFitness(tree);
		}
		
		for(BehaviourTree tree: population){
			float normalizedFitness = stats.getFitness(tree)/((float)sumFitness);
			
			System.out.println("Tree: " + tree + " with fitness " + normalizedFitness);
			if(MathUtils.randomBoolean(normalizedFitness)){
				Candidate c = new Candidate();
				c.tree = tree;
				c.fitness = normalizedFitness;
				candidates.add(c);
			}
		}
		
	}
	
	public void mutate(){
		for(BehaviourTree tree: population){
			tree.mutate(mutationChance);
		}
	}
	
	public void crossover(){
		for(BehaviourTree tree: population){
			BehaviourTree tree2 = population.random();
			if(tree != tree2){
				tree.crossover(tree2, crossoverChance);
			}
		}
	}
	
	public Array<BehaviourTree> getPopulation() {
		return population;
	}


	public void createPrototypes(Level level, PhysicsSystem physics, Pathfinding pathfinding){
		prototypes = new Array<BehaviourNode>();
		prototypes.add(new AttackNearest("", level));
		prototypes.add(new CheckArmor(0.0f));
		//prototypes.add(new CheckEntityDistance("", 0.0f,level));
		prototypes.add(new CheckHealth(0.0f));
		//prototypes.add(new CheckWeapon(""));
		prototypes.add(new MoveToNearest("", level, pathfinding,physics, 0.0f, 15.0f));
		prototypes.add(new PickupArmor(level, "armor"));
		//prototypes.add(new PickupDamageBoost(level, "damage"));
		prototypes.add(new PickupHealth(level, "health"));
		prototypes.add(new PickupWeapon("", level, pathfinding));
		//prototypes.add(new InverterNode());
		//prototypes.add(new ParallelNode());
		prototypes.add(new SelectorNode());
		prototypes.add(new SequenceNode());
		prototypes.add(new SucceederNode());
	}
	
}
