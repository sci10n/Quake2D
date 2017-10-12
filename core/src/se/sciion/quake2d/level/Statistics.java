package se.sciion.quake2d.level;

import java.io.File;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;

import se.sciion.quake2d.ai.behaviour.BehaviourTree;
import se.sciion.quake2d.level.items.Weapon;

public class Statistics {

	
	private ObjectMap<BehaviourTree,Float> totalDamageGiven;
	private ObjectMap<BehaviourTree,Float> totalDamageTaken;
	private ObjectMap<BehaviourTree,Float> armorAtEnd;
	private ObjectMap<BehaviourTree,Float> healthAtEnd;
	private ObjectMap<BehaviourTree,Boolean> survived;
	private ObjectMap<BehaviourTree,Integer> killcount;
	private ObjectMap<BehaviourTree,Integer> roundsPlayed;
	private ObjectMap<BehaviourTree,Integer> pickedWeapon;
	
	public Statistics() {
		totalDamageGiven = new ObjectMap<BehaviourTree,Float>();
		totalDamageTaken = new ObjectMap<BehaviourTree,Float>();
		armorAtEnd		 = new ObjectMap<BehaviourTree,Float>();
		healthAtEnd		 = new ObjectMap<BehaviourTree,Float>();
		survived		 = new ObjectMap<BehaviourTree,Boolean>();
		killcount		 = new ObjectMap<BehaviourTree,Integer>();
		roundsPlayed	= new ObjectMap<BehaviourTree, Integer>();
		pickedWeapon	= new ObjectMap<BehaviourTree, Integer>();		
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
	
	public void recordWeaponPickup(BehaviourTree tree){
		if(!pickedWeapon.containsKey(tree)){
			pickedWeapon.put(tree, 0);
		}
		pickedWeapon.put(tree, pickedWeapon.get(tree) + 1);

	}
	
	public void recordParticipant(BehaviourTree tree){
		if(!roundsPlayed.containsKey(tree)){
			roundsPlayed.put(tree, 0);
		}
		roundsPlayed.put(tree, roundsPlayed.get(tree) + 1);
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
		
		float damageGiven = totalDamageGiven.get(tree, 0.0f);
		float damageTaken = totalDamageTaken.get(tree, 0.0f);
		float armor	= armorAtEnd.get(tree, 0.0f);
		//float health = healthAtEnd.get(tree,0.0f);
		int killcount = this.killcount.get(tree, 0);
		boolean survived = this.survived.get(tree, false);
		int rounds = roundsPlayed.get(tree,1);
		int weapon = pickedWeapon.get(tree, 0);
		return (5.0f * damageGiven + 2.0f * armor + (survived ? 500.0f : 0.0f) + 50.0f * weapon + 1000.0f * killcount) / (float)(rounds);
	}

	public boolean hasSurvived(BehaviourTree tree) {
		return survived.get(tree, false);
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
		String output = "Match statistics:\n";
		
		for(BehaviourTree tree: totalDamageGiven.keys()) {
			output += "Tree: " + tree.toString() + " damage given: " + totalDamageGiven.get(tree) + "\n";
		}
		for(BehaviourTree tree: totalDamageTaken.keys()) {
			output += "Tree: " + tree.toString() + " damage taken: " + totalDamageGiven.get(tree) + "\n";
		}
		for(BehaviourTree tree: healthAtEnd.keys()) {
			output += "Tree: " + tree.toString() +  " " + healthAtEnd.get(tree,0.0f) + " health at end\n";
		}
		for(BehaviourTree tree: armorAtEnd.keys()) {
			output += "Tree: " + tree.toString() +  " " + armorAtEnd.get(tree,0.0f) + " armor at end\n";
		}
		for(BehaviourTree tree: survived.keys()) {
			output += "Tree: " + tree.toString() +  " survived\n";
		}
		for(BehaviourTree tree: roundsPlayed.keys()) {
			output += "Tree: " + tree.toString() +  " played " + roundsPlayed.get(tree) + " rounds\n";
		}
		for(BehaviourTree tree: pickedWeapon.keys()) {
			output += "Tree: " + tree.toString() +  " picked weapon\n";
		}
		return output;
	}

	public void clear() {
		armorAtEnd.clear();
		healthAtEnd.clear();
		killcount.clear();
		survived.clear();
		totalDamageGiven.clear();
		totalDamageTaken.clear();
		pickedWeapon.clear();
		roundsPlayed.clear();
	}
}
