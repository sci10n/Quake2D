package se.sciion.quake2d.ai.behaviour.nodes;

import static guru.nidi.graphviz.model.Factory.node;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;

import com.badlogic.gdx.math.Vector2;

import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;
import se.sciion.quake2d.ai.behaviour.BehaviourNode;
import se.sciion.quake2d.ai.behaviour.BehaviourStatus;
import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.components.PhysicsComponent;
import se.sciion.quake2d.level.system.Pathfinding;
import se.sciion.quake2d.level.system.PhysicsSystem;

public class MoveToEntity extends BehaviourNode {
    private static int moveToEntityId = 0;

    private Entity target;
    private BotInputComponent input;
    private float minDistance;
    private PhysicsSystem physics;

    public MoveToEntity(Entity target, PhysicsSystem physics, BotInputComponent input,
            float minDistance) {
        super();
        this.target = target;
        this.input = input;
        this.minDistance = minDistance;
        this.physics = physics;

    }

    @Override
    protected void onEnter() {
        status = BehaviourStatus.RUNNING;
        super.onEnter();

    }

    @Override
    protected BehaviourStatus onUpdate() {

        if(target == null) {
            status = BehaviourStatus.FAILURE;
            return status;
        }

        PhysicsComponent physics = input.getParent().getComponent(ComponentTypes.Physics);
        if (physics == null) {
            status = BehaviourStatus.FAILURE;
            return status;
        }

        Vector2 fromLoc = physics.getBody().getPosition();

        PhysicsComponent targetPhysics = target.getComponent(ComponentTypes.Physics);
        if (targetPhysics == null ) {
            status = BehaviourStatus.FAILURE;
            System.out.println(status);
            input.setTarget(null);

            return status;
        }

        Vector2 targetLoc = targetPhysics.getBody().getPosition();

        input.setTarget(targetLoc);

        float distance = fromLoc.cpy().sub(targetLoc).len();

        if (distance > minDistance) {
            status = BehaviourStatus.RUNNING;

        } else if (distance <= minDistance && this.physics.lineOfSight(fromLoc, targetLoc)) {
            status = BehaviourStatus.SUCCESS;
            input.setTarget(null);

        }

        return status;
    }

    @Override
    public Node toDotNode() {
        return node("moveToEntity" + moveToEntityId++)
               .with(Shape.RECTANGLE)
				.with(Style.FILLED, Color.rgb(getColor()).fill(), Color.BLACK.radial())
               .with(Label.of("Move to"));
    }
}
