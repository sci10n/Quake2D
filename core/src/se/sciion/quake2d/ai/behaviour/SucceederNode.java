package se.sciion.quake2d.ai.behaviour;

import static guru.nidi.graphviz.model.Factory.node;

import com.badlogic.gdx.utils.Array;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.RankDir;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;

public class SucceederNode extends DecoratorNode {

	public SucceederNode(){
		super();
	}
	
    public SucceederNode(BehaviourNode behaviour) {
        super(behaviour);
    }

    @Override
    protected void onEnter() {
    	setStatus(BehaviourStatus.RUNNING);
    }

    @Override
    protected BehaviourStatus onUpdate() {
    	BehaviourNode child = children.first();
    	child.tick();
        if(child.getStatus() == BehaviourStatus.SUCCESS) {
        	setStatus(BehaviourStatus.SUCCESS);
        } else if(child.getStatus() == BehaviourStatus.FAILURE) {
        	setStatus(BehaviourStatus.SUCCESS);
        }
        return getStatus();
    }

    @Override
    public Node toDotNode() {
    	BehaviourNode child = children.first();
        return node("succeeder" + getNext())
                   .with(Shape.RECTANGLE)
					.with(Style.FILLED, Color.rgb(getColor()).fill(), Color.BLACK.radial())
                   .with(Label.of("Succeed"))
                   .link(child.toDotNode());
    }

	@Override
	public BehaviourNode randomized(Array<BehaviourNode> prototypes) {
		return new SucceederNode(prototypes.random().randomized(prototypes));
	}

}
