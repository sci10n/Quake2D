package se.sciion.quake2d.ai.behaviour;

import static guru.nidi.graphviz.model.Factory.*;

import java.util.Arrays;
import java.util.List;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.RankDir;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;

/**
 * Continue processing until ail children are attempted or one succeeds.
 * 
 * @author sciion
 *
 */
public class SelectorNode extends CompositeNode {
    private static int selectorId = 0;

    public SelectorNode() {
        super();
    }

    public SelectorNode(List<BehaviourNode> behaviours) {
        super(behaviours);
        currentChild = 0;
    }

    public SelectorNode(BehaviourNode... behaviourNodes) {
        super(Arrays.asList(behaviourNodes));
        currentChild = 0;

    }

    @Override
    protected void onEnter() {
        super.onEnter();
        status = BehaviourStatus.RUNNING;
        currentChild = 0;
    }

    @Override
    protected BehaviourStatus onUpdate() {
        if (currentChild < children.size() && !children.isEmpty()) {
            status = children.get(currentChild).tick();
            if (status == BehaviourStatus.FAILURE) {
                if (currentChild < children.size()) {
                    ++currentChild;
                    return onUpdate();
                }
            }
        }
        return status;
    }

    @Override
    public Node toDotNode() {
        Node selector = node("selector" + selectorId++)
                        .with(Shape.RECTANGLE)
                        .with(Style.FILLED, Color.rgb(getColor()).fill(), Color.BLACK.radial())
                        .with(Label.of("Select"))
                        .with(Rank.SAME);
       
        for(BehaviourNode child : children)
            selector = selector.link(child.toDotNode());
        return selector;
    }
}
