package se.sciion.quake2d.ai.behaviour.nodes;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import static guru.nidi.graphviz.model.Factory.node;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Color;
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
import se.sciion.quake2d.level.system.Pathfinding;

public class AttackNearest extends BehaviourNode{

    private String tag;
    private Level level;

    public AttackNearest(String tag, Level level) {
        this.tag = tag;
        this.level = level;
    }

    @Override
    protected void onEnter() {
        super.onEnter();
        setStatus(BehaviourStatus.RUNNING);
    }

    @Override
    protected BehaviourStatus onUpdate() {

        BotInputComponent input = entityOwner.getComponent(ComponentTypes.BotInput);
        PhysicsComponent physics =entityOwner.getComponent(ComponentTypes.Physics);
        if (physics == null) {
        	setStatus(BehaviourStatus.FAILURE);
            return getStatus();
        }
        
        Vector2 position = physics.getBody().getPosition();
   
    	Entity nearestTarget = null;
    	double nearestDistance = 300.0;
        for(Entity e: level.getEntities(tag)) {
        	if(e == input.getParent()){
        		continue;
        	}
        	
            PhysicsComponent ePhysics = e.getComponent(ComponentTypes.Physics);
            if(ePhysics != null) {
                Vector2 ePos = ePhysics.getBody().getPosition();
                double distance = position.cpy().sub(ePos).len();
                if (distance < nearestDistance) {
                	nearestDistance = distance;
                	nearestTarget = e;
                }
            }
        }

        if(nearestTarget == null){
        	setStatus(BehaviourStatus.FAILURE);
        	return getStatus();
        }
        
        PhysicsComponent targetPhysics = nearestTarget.getComponent(ComponentTypes.Physics);
        if(targetPhysics == null) {
        	setStatus(BehaviourStatus.FAILURE);
            return getStatus();
        }

        Vector2 direction = targetPhysics.getBody().getPosition().cpy().sub(physics.getBody().getPosition());
        if(input.fire(direction.nor())) {
        	setStatus(BehaviourStatus.SUCCESS);
        }
        else {
        	setStatus(BehaviourStatus.FAILURE);
        }

        return getStatus();
    }

    @Override
    public Node toDotNode() {
        return node("attackNearest" + getNext())
               .with(Shape.RECTANGLE)
				.with(Style.FILLED, Color.rgb(getColor()).fill(), Color.BLACK.radial())
               .with(Label.of("Attack " + tag));
    }

	@Override
	public void mutate(float chance) {
		if(MathUtils.randomBoolean(chance)){
			String tag = level.getTags().random();
			this.tag = tag;
		}
	}

	@Override
	public BehaviourNode randomized(Array<BehaviourNode> prototypes) {
		return new AttackNearest(level.getTags().random(), level);
	}

}
