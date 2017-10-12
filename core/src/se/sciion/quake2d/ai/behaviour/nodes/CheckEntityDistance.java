package se.sciion.quake2d.ai.behaviour.nodes;

import static guru.nidi.graphviz.model.Factory.node;

import org.apache.bcel.generic.CPInstruction;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

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

	private String tag;
	private float threshold;
	private Level level;

	public CheckEntityDistance(String tag, float threshold, Level level) {
		super();
		this.tag = tag;
		this.threshold = threshold;
		this.level = level;
	}

	@Override
	protected BehaviourStatus onUpdate() {
		
		PhysicsComponent physics = entityOwner.getComponent(ComponentTypes.Physics);
		if(physics == null){
			setStatus(BehaviourStatus.FAILURE);
			return getStatus();
		}
		
		Vector2 fromPos = physics.getBody().getPosition();
		float nearest = Float.MAX_VALUE;
		for(Entity e: level.getEntities(tag)){
			if(e == entityOwner){
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
	               .with(Label.of("Distance to " + tag + " < " + threshold));
	}

	@Override
	public void mutate() {
			threshold += MathUtils.random(-2, 2);
			threshold = MathUtils.clamp(threshold, 0, 40);
			tag = level.getTags().random();
	}

	@Override
	public BehaviourNode clone() {
		return new CheckEntityDistance(tag,threshold, level);
	}
	
	@Override
	public BehaviourNode randomized() {
		return new CheckEntityDistance(level.getTags().random(), MathUtils.random(0, 5), level);
	}

}
