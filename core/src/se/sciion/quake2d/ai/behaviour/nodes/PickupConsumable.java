package se.sciion.quake2d.ai.behaviour.nodes;

import static guru.nidi.graphviz.model.Factory.node;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;
import se.sciion.quake2d.ai.behaviour.BehaviourNode;
import se.sciion.quake2d.ai.behaviour.BehaviourStatus;
import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.components.PhysicsComponent;
import se.sciion.quake2d.level.system.PhysicsSystem;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public abstract class PickupConsumable extends BehaviourNode {
	
	protected Level level;
	protected String tag;

	public PickupConsumable(Level level, String tag) {
		super();
		this.level = level;
		this.tag = tag;
		
	}

	@Override
	protected void onEnter() {
		setStatus(BehaviourStatus.RUNNING);
		super.onEnter();
	}
	
	@Override
	protected BehaviourStatus onUpdate() {
		
		if(restored()){
			setStatus(BehaviourStatus.SUCCESS);
			return getStatus();
		}
		
		PhysicsComponent physicsComponent = entityOwner.getComponent(ComponentTypes.Physics);
		BotInputComponent input = entityOwner.getComponent(ComponentTypes.BotInput);
		if(physicsComponent == null || input == null){
			setStatus(BehaviourStatus.FAILURE);
			return getStatus();
		}
		
		Vector2 fromPos = physicsComponent.getBody().getPosition();
		
		Vector2 target = null;
		float bestDistance = Float.MAX_VALUE;
		for(Entity e: level.getEntities(tag)){
			PhysicsComponent ePhys = e.getComponent(ComponentTypes.Physics);
			if(ePhys == null){
				continue;
			}
			
			Vector2 ePos = ePhys.getBody().getPosition();
			float eDist = Vector2.dst2(ePos.x, ePos.y, fromPos.x,fromPos.y);
			if(eDist < bestDistance){
				target = ePos;
				bestDistance = eDist;
			}
		}
		
		if(target == null){
			setStatus(BehaviourStatus.FAILURE);
			return getStatus();
		}
		
		input.setTarget(target);		
		return getStatus();
	}
	
	protected abstract boolean restored();
	
	@Override
	public void mutate() {
		
	}
	
	@Override
	public Node toDotNode() {
		  return node("pickupConsumable" + getNext())
	               .with(Shape.RECTANGLE)
				   .with(Style.FILLED, Color.rgb(getColor()).fill(), Color.BLACK.radial())
	               .with(Label.of("Pick up " + tag));
	}

}
