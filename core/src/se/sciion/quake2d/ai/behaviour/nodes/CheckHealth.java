package se.sciion.quake2d.ai.behaviour.nodes;

import static guru.nidi.graphviz.model.Factory.node;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;

import org.omg.PortableInterceptor.SUCCESSFUL;

import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;
import se.sciion.quake2d.ai.behaviour.BehaviourNode;
import se.sciion.quake2d.ai.behaviour.BehaviourStatus;
import se.sciion.quake2d.level.components.HealthComponent;

public class CheckHealth extends BehaviourNode {
    private static int checkHealthId = 0;

    private HealthComponent health;
    private float ratio;

    public CheckHealth(HealthComponent health, float ratio) {
        this.health = health;
        this.ratio = ratio;
    }

    @Override
    protected void onEnter() {
        status = BehaviourStatus.RUNNING;
    }

    @Override
    protected BehaviourStatus onUpdate() {

        if(health != null && (health.health/(float)health.MAX_HEALTH) >= ratio) {
            status = BehaviourStatus.SUCCESS;
        }
        else {
            status = BehaviourStatus.FAILURE;
        }
        return status;
    }

    @Override
    public Node toDotNode() {
        return node("checkHealth" + checkHealthId++)
               .with(Shape.ELLIPSE)
				.with(Style.FILLED, Color.rgb(getColor()).fill(), Color.BLACK.radial())
               .with(Label.of("Health > " + ratio));
    }
}
