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
	public static Array<BehaviourNode> prototypes;
	
	private float crossoverChance = 0.2f;
	private float mutationChance = 0.5f;
	
	public Trees(){
	}
	
	// Get a number of pooled trees based on their fitness
	public void select(Statistics stats){
		Array<BehaviourTree> offsprings = new Array<BehaviourTree>();
		Array<Candidate> candidates = new Array<Trees.Candidate>();
		
		float sumFitness = 1.0f;
		for(BehaviourTree tree: population){
			sumFitness += stats.getFitness(tree);
		}
		
		for(BehaviourTree tree: population){
			float normalizedFitness = stats.getFitness(tree)/((float)sumFitness);
			
				Candidate c = new Candidate();
				c.tree = tree;
				c.fitness = normalizedFitness;
				candidates.add(c);
		}
		
		candidates.sort((Candidate c1, Candidate c2) -> (int)Math.signum(c2.fitness - c1.fitness));
		if(candidates.size > 2)
			candidates.removeRange(2, candidates.size - 1);
		
		// Bad place to be
		if(candidates.size == 0 || candidates.first().fitness < 0.1f){
			System.out.println("No Offspring Generated");
			initPopulation(populationLimit);
			return;
		}
		
		// Fill offsprings with clones of best candidates
		for(int i = offsprings.size; i <= populationLimit;i++){
			BehaviourTree tree = candidates.random().tree.clone();
			offsprings.add(tree);
		}
		population = offsprings;
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
		prototypes.add(new AttackNearest("", level, physics));
		prototypes.add(new CheckArmor(0.0f));
		//prototypes.add(new CheckEntityDistance("", 0.0f,level));
		//prototypes.add(new CheckHealth(0.0f));
		prototypes.add(new CheckWeapon(""));
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



	// Init population with single node trees
	public void initPopulation(int populationLimit) {
		population = new Array<BehaviourTree>();
		for(int i = 0; i < populationLimit; i++){
			BehaviourNode root = prototypes.random().randomized();
			while(!(root instanceof CompositeNode)){
				root = prototypes.random().randomized();
			}
			BehaviourTree tree = new BehaviourTree(root.randomized());
			population.add(tree);
		}
	}
	
}
