package se.sciion.quake2d.ai.behaviour;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

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

public class Trees {

	
	private static FileHandle outputFile;
	
	private class Candidate {
		public BehaviourTree tree;
		public float fitness;
	}
	
	public int generation = 0;
	public int populationLimit = 50;
	private Array<BehaviourTree> population;
	public static ObjectMap<String,BehaviourNode> prototypesMap;
	public static Array<BehaviourNode> prototypes;

	private float crossoverChance = 0.3f;
	private float mutationChance = 0.2f;

	
	public Trees(){
		outputFile = new FileHandle(new File("statistics_" + new SimpleDateFormat("HH_mm_ss").format(new Date())));
		outputFile.writeString("Generation,Fitness,Victory\n", true);
	}
	
	// Get a number of pooled trees based on their fitness
	public void select(Statistics stats){
		Array<BehaviourTree> offsprings = new Array<BehaviourTree>();
		Array<Candidate> candidates = new Array<Trees.Candidate>();
		
		float normalizedFitness = 0.0f;
		for(BehaviourTree tree: population){
			normalizedFitness += stats.getFitness(tree);
		}
		
		for(BehaviourTree tree: population){
				Candidate c = new Candidate();
				c.tree = tree;
				c.fitness = stats.getFitness(tree)/normalizedFitness;
				candidates.add(c);
				if(stats.hasSurvived(tree)){
					System.out.println("Tree: " + tree + " survivied");
				}
				
				tree.toXML("trees/" + generation + "_" + stats.getFitness(tree) +"_" + population.indexOf(tree, true) + ".xml");
				outputFile.writeString("" + generation + "," +  stats.getFitness(tree) + "," + (stats.hasSurvived(tree) ? 1 : 0 ) +  "\n", true);
		}
		
		candidates.sort((Candidate c1, Candidate c2) -> (int)Math.signum(c2.fitness - c1.fitness));
		
		while(offsprings.size < populationLimit) {
			float accumulatedProb = 0.0f;
			float randomValue = MathUtils.random();
			for(Candidate c: candidates) {
				if((c.fitness + accumulatedProb) < randomValue) {
					offsprings.add(c.tree.clone());
				}
				accumulatedProb += c.fitness;
			}
		}
		
		population = offsprings;
		generation++;
		
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
		prototypesMap = new ObjectMap<String,BehaviourNode>();
		prototypesMap.put("AttackNearest", new AttackNearest("", level, physics));
		prototypesMap.put("CheckArmor", new CheckArmor(0.0f));
		prototypesMap.put("CheckEntityDistance", new CheckEntityDistance("", 0.0f,level));
		prototypesMap.put("CheckHealth", new CheckHealth(0.0f));
		prototypesMap.put("CheckWeapon", new CheckWeapon(""));
		prototypesMap.put("MoveToNearest", new MoveToNearest("", level, pathfinding,physics, 0.0f, 15.0f));
		prototypesMap.put("PickupArmor", new PickupArmor(level, "armor"));
		prototypesMap.put("PickupDamageBoost", new PickupDamageBoost(level, "damage"));
		prototypesMap.put("PickupHealth", new PickupHealth(level, "health"));
		prototypesMap.put("PickupWeapon", new PickupWeapon("", level, pathfinding));
		//prototypesMap.put("InverterNode", new InverterNode());
		//prototypesMap.put("ParallelNode", new ParallelNode());
		prototypesMap.put("SelectorNode", new SelectorNode());
		prototypesMap.put("SequenceNode", new SequenceNode());
		//prototypesMap.put("SucceederNode", new SucceederNode());
		
		prototypes = prototypesMap.values().toArray();
	}

	


	// Init population with single node trees
	public void initPopulation() {
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
	
	public BehaviourTree getEnemy(Level level, PhysicsSystem system, Pathfinding pathfinding){
		CheckArmor checkArmor = new CheckArmor(0.25f);
		CheckHealth checkHealth = new CheckHealth(0.50f);
		PickupHealth pickupHealth = new PickupHealth(level, "health");
		PickupArmor pickupArmor = new PickupArmor(level, "armor");
		PickupDamageBoost pickupBoost = new PickupDamageBoost(level, "damage");
		
		PickupWeapon pickupWeaponShotgun = new PickupWeapon("shotgun",level,pathfinding);
		PickupWeapon pickupWeaponRifle = new PickupWeapon("rifle",level,pathfinding);

		AttackNearest attackPlayer = new AttackNearest("player", level, system);
		MoveToNearest moveToPlayer = new MoveToNearest("player",level ,pathfinding,system, 0.0f, 5.0f);
		
		CheckEntityDistance distanceCheck = new CheckEntityDistance("player", 15, level);
		CheckEntityDistance otherDistanceCheck = new CheckEntityDistance("player", 5, level);
		CheckWeapon rifleCheck = new CheckWeapon("rifle");
		CheckWeapon shotgunCheck = new CheckWeapon("shotgun");
		
		SequenceNode s1 = new SequenceNode(new InverterNode(checkHealth), pickupHealth);
		SequenceNode s4 = new SequenceNode(new InverterNode(checkArmor), pickupArmor);
		SequenceNode s2 = new SequenceNode(new SucceederNode(new SelectorNode(new SequenceNode(otherDistanceCheck, new InverterNode(shotgunCheck), pickupWeaponShotgun), new SequenceNode(distanceCheck, new InverterNode(rifleCheck), pickupWeaponRifle))),  new SucceederNode(pickupBoost), moveToPlayer, attackPlayer);
		SelectorNode s3 = new SelectorNode(s1, s4, s2);
		
//		TreePool pool = new TreePool();
		BehaviourTree tree = new BehaviourTree(s3);
		return tree;
	}
}
