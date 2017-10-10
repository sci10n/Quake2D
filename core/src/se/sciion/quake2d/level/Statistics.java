package se.sciion.quake2d.level;

import com.badlogic.gdx.utils.ObjectMap;

import se.sciion.quake2d.ai.behaviour.BehaviourTree;

public class Statistics {

	
	private ObjectMap<BehaviourTree,Float> totalDamageGiven;
	private ObjectMap<BehaviourTree,Float> totalDamageTaken;
	private ObjectMap<BehaviourTree,Float> armorAtEnd;
	private ObjectMap<BehaviourTree,Float> healthAtEnd;
	private ObjectMap<BehaviourTree,Boolean> survived;
	private ObjectMap<BehaviourTree,Integer> killcount;

	public Statistics() {
		totalDamageGiven = new ObjectMap<BehaviourTree,Float>();
		totalDamageTaken = new ObjectMap<BehaviourTree,Float>();
		armorAtEnd		 = new ObjectMap<BehaviourTree,Float>();
		healthAtEnd		 = new ObjectMap<BehaviourTree,Float>();
		survived		 = new ObjectMap<BehaviourTree,Boolean>();
		killcount		 = new ObjectMap<BehaviourTree,Integer>();
	}
	
	public void recordDamageTaken(BehaviourTree giver, BehaviourTree reciever, float amount) {
		if(giver != null) {
			if(!totalDamageGiven.containsKey(giver)) {
				totalDamageGiven.put(giver, 0.0f);
			}
			totalDamageGiven.put(giver, totalDamageGiven.get(giver) + amount);
		}
		
		if(reciever != null) {
			if(!totalDamageTaken.containsKey(reciever)) {
				totalDamageTaken.put(reciever, 0.0f);
			}
			totalDamageTaken.put(reciever, totalDamageTaken.get(reciever) + amount);
		}
	}

	public void recordHealth(float health, float armor, BehaviourTree entity) {
		if(entity == null) {
			return;
		}
		armorAtEnd.put(entity, armor);
		healthAtEnd.put(entity, health);
	}
	
	public void recordSurvivior(BehaviourTree entity) {
		if(entity == null) {
			return;
		}
		survived.put(entity, true);
	}
	
	public void recordKill(BehaviourTree giver) {
		if(giver == null) {
			return;
		}
		
		if(!killcount.containsKey(giver)) {
			killcount.put(giver, 0);
		}
		killcount.put(giver, killcount.get(giver) + 1);
	}
	
	public float getFitness(BehaviourTree tree) {
		return 0.0f;
	}

	public int getTotalKillcount() {
		int total = 0;
		for(Integer i: killcount.values()) {
			total += i;
		}
		return total;
	}
	
	@Override
	public String toString() {
		String output = "";
		
		for(BehaviourTree tree: totalDamageGiven.keys()) {
			output += "Tree: " + tree.toString() + " damage given: " + totalDamageGiven.get(tree) + "\n";
		}
		for(BehaviourTree tree: totalDamageTaken.keys()) {
			output += "Tree: " + tree.toString() + " damage taken: " + totalDamageGiven.get(tree) + "\n";
		}
		for(BehaviourTree tree: survived.keys()) {
			output += "Tree: " + tree.toString() +  " survived\n";
		}
		return output;
	}
}
