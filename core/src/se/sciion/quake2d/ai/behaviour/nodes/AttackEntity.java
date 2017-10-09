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
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.components.PhysicsComponent;

public class AttackEntity extends BehaviourNode{

    private Entity target;

    public AttackEntity(Entity target) {
        this.target = target;
    }

    @Override
    protected void onEnter() {
        super.onEnter();
        setStatus(BehaviourStatus.RUNNING);
    }

    @Override
    protected BehaviourStatus onUpdate() {

        PhysicsComponent targetPhysics = target.getComponent(ComponentTypes.Physics);
        PhysicsComponent physics = parent.getComponent(ComponentTypes.Physics);
        BotInputComponent input = parent.getComponent(ComponentTypes.BotInput);
        if(targetPhysics == null || physics == null || input == null) {
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
        return node("attackEntity" + getNext())
               .with(Shape.RECTANGLE)
				.with(Style.FILLED, Color.rgb(getColor()).fill(), Color.BLACK.radial())
               .with(Label.of("Attack"));
    }

}
