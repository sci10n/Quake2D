package se.sciion.quake2d.ai.behaviour.nodes;

import static guru.nidi.graphviz.model.Factory.node;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

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
import se.sciion.quake2d.level.system.PhysicsSystem;

public class MoveToNearest extends BehaviourNode {

    private String tag;
    private float minDistance;
    private float maxDistance;
    private PhysicsSystem physics;
    private Level level;
    private Pathfinding pathfinding;

    public MoveToNearest(String tag, Level level, Pathfinding pathfinding, PhysicsSystem physics, float minDistance, float maxDistance) {
        super();
        this.tag = tag;
        this.level = level;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.physics = physics;
        this.pathfinding = pathfinding;
    }

    @Override
    protected void onEnter() {
    	setStatus(BehaviourStatus.RUNNING);
        super.onEnter();

    }

    @Override
    protected BehaviourStatus onUpdate() {

    	BotInputComponent input = entityOwner.getComponent(ComponentTypes.BotInput);
        PhysicsComponent physics = entityOwner.getComponent(ComponentTypes.Physics);
        if (physics == null) {
        	setStatus(BehaviourStatus.FAILURE);
            return getStatus();
        }

        Vector2 fromLoc = physics.getBody().getPosition();
        int bestPath = Integer.MAX_VALUE;

        Vector2 targetPos = null;
        for(Entity e: level.getEntities(tag)) {
        	if(e == input.getParent()){
        		continue;
        	}
        	
            PhysicsComponent ePhysics = e.getComponent(ComponentTypes.Physics);
            if(ePhysics != null) {
                Vector2 ePos = ePhysics.getBody().getPosition();
                int pathLength = pathfinding.findPath(fromLoc,ePos, entityOwner).size;
                if(pathLength < bestPath) {
                    bestPath = pathLength;
                    targetPos = ePos;
                }
            }
        }

        if(targetPos == null) {
        	setStatus(BehaviourStatus.SUCCESS);
            input.setTarget(targetPos);
            return getStatus();
        }


        float distance = fromLoc.cpy().sub(targetPos).len();
        
        if(distance <= maxDistance && distance >= minDistance && this.physics.lineOfSight(fromLoc, targetPos)){
        	setStatus(BehaviourStatus.SUCCESS);
        	input.setTarget(null);
        	return getStatus();
        }

        input.setTarget(targetPos);
        return getStatus();
    }

    @Override
    public Node toDotNode() {
    	if(tag == null){
    		System.out.println("Null tag!");
    	}
        Node node = node("moveToNearest" + getNext())
                    .with(Shape.RECTANGLE)
					.with(Style.FILLED, Color.rgb(getColor()).fill(), Color.BLACK.radial())
                    .with(Label.of("Move to " + tag));

        return node;
    }

	@Override
	public void mutate() {
			tag = level.getTags().random();
			//minDistance += MathUtils.random(0.2f) - 0.1f;
			minDistance = 0;
	}

	@Override
	public BehaviourNode clone() {
		return new MoveToNearest(tag, level, pathfinding, physics, minDistance, maxDistance);
	}
	
	@Override
	public BehaviourNode randomized() {
		return new MoveToNearest(level.getTags().random(), level, pathfinding, physics, 0, maxDistance);
	}
	
	@Override
	public Element toXML(Document doc) {
		Element e = doc.createElement(getClass().getSimpleName());
		e.setAttribute("tag", tag);
		e.setAttribute("min-distance", Float.toString(minDistance));
		e.setAttribute("max-distance", Float.toString(maxDistance));
		return e;
	}

	@Override
	public BehaviourNode fromXML(Element element) {
		tag = element.getAttribute("tag");
		minDistance = Float.parseFloat(element.getAttribute("min-distance"));
		maxDistance = Float.parseFloat(element.getAttribute("max-distance"));
		return this;
	}
}
