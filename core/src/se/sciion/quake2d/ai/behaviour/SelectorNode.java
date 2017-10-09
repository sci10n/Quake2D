package se.sciion.quake2d.ai.behaviour;

import static guru.nidi.graphviz.model.Factory.node;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Rank;
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

    public SelectorNode() {
        super();
    }

    public SelectorNode(BehaviourNode... behaviourNodes) {
        super(behaviourNodes);
        currentChild = 0;

    }

    @Override
    protected void onEnter() {
        super.onEnter();
        setStatus(BehaviourStatus.RUNNING);
        currentChild = 0;
    }

    @Override
    protected BehaviourStatus onUpdate() {
        if (currentChild < children.size() && !children.isEmpty()) {
        	setStatus(children.get(currentChild).tick());
            if (getStatus() == BehaviourStatus.FAILURE) {
                if (currentChild < children.size()) {
                    ++currentChild;
                    return onUpdate();
                }
            }
        }
        return getStatus();
    }

    @Override
    public Node toDotNode() {
        Node selector = node("selector" + getNext())
                        .with(Shape.RECTANGLE)
                        .with(Style.FILLED, Color.rgb(getColor()).fill(), Color.BLACK.radial())
                        .with(Label.of("Selector"))
                        .with(Rank.SAME);
       
        for(BehaviourNode child : children)
            selector = selector.link(child.toDotNode());
        return selector;
    }
}
