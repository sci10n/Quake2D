package se.sciion.quake2d.ai.behaviour.nodes;

import static guru.nidi.graphviz.model.Factory.node;

import org.apache.bcel.generic.CPInstruction;

import com.badlogic.gdx.math.Vector2;

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
import se.sciion.quake2d.level.components.HealthComponent;
import se.sciion.quake2d.level.components.PhysicsComponent;

/**
 * Returns {@link BehaviourStatus.SUCCESS} if nearest entity is within threshold distance
 * @author erija578
 *
 */
public class CheckEntityDistance extends BehaviourNode{

	private String targetId;
	private float threshold;
	private Level level;

	public CheckEntityDistance(String targetId, float threshold, Level level) {
		super();
		this.targetId = targetId;
		this.threshold = threshold;
		this.level = level;
	}

	@Override
	protected BehaviourStatus onUpdate() {
		
		PhysicsComponent physics = parent.getComponent(ComponentTypes.Physics);
		if(physics == null){
			setStatus(BehaviourStatus.FAILURE);
			return getStatus();
		}
		
		Vector2 fromPos = physics.getBody().getPosition();
		float nearest = Float.MAX_VALUE;
		for(Entity e: level.getEntities(targetId)){
			if(e == parent){
				continue;
			}
			
			PhysicsComponent targetPhys = e.getComponent(ComponentTypes.Physics);
			if(targetPhys == null){
				setStatus(BehaviourStatus.FAILURE);
				return getStatus();
			}

			Vector2 targetPos = targetPhys.getBody().getPosition();
			float dist = Vector2.dst(fromPos.x, fromPos.y, targetPos.x, targetPos.y);
			if(dist < nearest){
				nearest = dist;
			}
		}
		
		if(nearest <= threshold){
			setStatus(BehaviourStatus.SUCCESS);
		}
		else {
			setStatus(BehaviourStatus.FAILURE);
		}
		
		return getStatus();
	}

	@Override
	public Node toDotNode() {
		 return node("entityDistance" + getNext())
	               .with(Shape.ELLIPSE)
					.with(Style.FILLED, Color.rgb(getColor()).fill(), Color.BLACK.radial())
	               .with(Label.of("Distance to " + targetId + " < " + threshold));
	}

}
