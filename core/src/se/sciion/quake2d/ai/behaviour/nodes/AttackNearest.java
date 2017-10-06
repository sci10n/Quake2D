package se.sciion.quake2d.ai.behaviour.nodes;

import com.badlogic.gdx.math.Vector2;

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
    private static int attackId = 0;

    private String id;
    private Level level;
    private BotInputComponent input;

    public AttackNearest(String id,  BotInputComponent input, Level level) {
        this.id = id;
        this.input = input;
        this.level = level;
    }

    @Override
    protected void onEnter() {
        super.onEnter();
        status = BehaviourStatus.RUNNING;
    }

    @Override
    protected BehaviourStatus onUpdate() {

        PhysicsComponent physics = input.getParent().getComponent(ComponentTypes.Physics);
        if (physics == null) {
            status = BehaviourStatus.FAILURE;
            return status;
        }
        
        Vector2 position = physics.getBody().getPosition();
   
    	Entity nearestTarget = null;
    	double nearestDistance = 300.0;
        for(Entity e: level.getEntities(id)) {
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

        PhysicsComponent targetPhysics = nearestTarget.getComponent(ComponentTypes.Physics);
        if(targetPhysics == null) {
            status = BehaviourStatus.FAILURE;
            return status;
        }


        Vector2 direction = targetPhysics.getBody().getPosition().cpy().sub(physics.getBody().getPosition());
        if(input.fire(direction.nor())) {
            status = BehaviourStatus.SUCCESS;
        }
        else {
            status = BehaviourStatus.FAILURE;
        }

        return status;
    }

    @Override
    public Node toDotNode() {
        return node("attackNearest" + attackId++)
               .with(Shape.RECTANGLE)
				.with(Style.FILLED, Color.rgb(getColor()).fill(), Color.BLACK.radial())
               .with(Label.of("Attack " + id));
    }

}
