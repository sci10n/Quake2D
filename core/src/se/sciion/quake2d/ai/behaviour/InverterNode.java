package se.sciion.quake2d.ai.behaviour;

import static guru.nidi.graphviz.model.Factory.*;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;

public class InverterNode extends DecoratorNode {
    private static int inverterNodeId = 0;

    public InverterNode(BehaviourNode behaviour) {
        super(behaviour);
    }

    @Override
    protected void onEnter() {
        status = BehaviourStatus.RUNNING;
    }

    @Override
    protected BehaviourStatus onUpdate() {
        status = child.tick();
        if(child.status == BehaviourStatus.SUCCESS) {
            status = BehaviourStatus.FAILURE;
        } else if(child.status == BehaviourStatus.FAILURE) {
            status = BehaviourStatus.SUCCESS;
        }

        return status;
    }

    @Override
    public Node toDotNode() {
        return node("inverter" + inverterNodeId++)
               .with(Shape.DIAMOND)
				.with(Style.FILLED, Color.rgb(getColor()).fill(), Color.BLACK.radial())
               .with(Label.of("Invert"))
               .link(child.toDotNode());
    }
}
