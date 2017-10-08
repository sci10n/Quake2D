package se.sciion.quake2d.ai.behaviour.nodes;

import static guru.nidi.graphviz.model.Factory.node;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;

import com.badlogic.gdx.math.Vector2;

import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;
import net.dermetfan.utils.math.MathUtils;
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
    private static int moveToNearestId = 0;

    private String tag;
    private BotInputComponent input;
    private float minDistance;
    private PhysicsSystem physics;
    private Level level;
    private Pathfinding pathfinding;

    public MoveToNearest(String tag, Level level, Pathfinding pathfinding, PhysicsSystem physics, BotInputComponent input, float minDistance) {
        super();
        this.tag = tag;
        this.level = level;
        this.input = input;
        this.minDistance = minDistance;
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

        PhysicsComponent physics = input.getParent().getComponent(ComponentTypes.Physics);
        if (physics == null) {
        	setStatus(BehaviourStatus.FAILURE);
            return getStatus();
        }

        Vector2 fromLoc = physics.getBody().getPosition();
        int bestPath = Integer.MAX_VALUE;

        Vector2 targetPos = null;
        for(Entity e: level.getEntities(tag)) {
            PhysicsComponent ePhysics = e.getComponent(ComponentTypes.Physics);
            if(ePhysics != null) {
                Vector2 ePos = ePhysics.getBody().getPosition();
                int pathLength = pathfinding.findPath(fromLoc,ePos).size;
                if(pathLength < bestPath) {
                    bestPath = pathLength;
                    targetPos = ePos;
                }
            }
        }

        if(targetPos == null) {
        	setStatus(BehaviourStatus.FAILURE);
            input.setTarget(targetPos);
            return getStatus();
        }

        input.setTarget(targetPos);

        float distance = fromLoc.cpy().sub(targetPos).len();

        if (distance > minDistance) {
        	setStatus(BehaviourStatus.RUNNING);
        } else if (distance <= minDistance && this.physics.lineOfSight(fromLoc, targetPos)) {
        	setStatus(BehaviourStatus.SUCCESS);
            input.setTarget(null);

        }

        return getStatus();
    }

    @Override
    public Node toDotNode() {
        Node node = node("moveToNearest" + moveToNearestId++)
                    .with(Shape.RECTANGLE)
					.with(Style.FILLED, Color.rgb(getColor()).fill(), Color.BLACK.radial())
                    .with(Label.of("Move to " + tag));

        return node;
    }

}
