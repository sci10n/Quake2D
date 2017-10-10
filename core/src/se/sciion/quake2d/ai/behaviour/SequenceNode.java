package se.sciion.quake2d.ai.behaviour;

import static guru.nidi.graphviz.model.Factory.node;

import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;

// Process each child in order. Fails if one fails.
public class SequenceNode extends CompositeNode{

    public SequenceNode() {
        super();
    }
    
    public SequenceNode(Array<BehaviourNode> children){
    	super(children);
    }

    public SequenceNode(BehaviourNode ...behaviourNodes){
        super(behaviourNodes);
        currentChild = 0;

    }

    @Override
    protected void onEnter() {
        currentChild = 0;
        setStatus(BehaviourStatus.RUNNING);
        super.onEnter();
    }

    @Override
    protected BehaviourStatus onUpdate() {
        if (currentChild < children.size && children.size != 0) {
        	setStatus(children.get(currentChild).tick());
            if(getStatus() == BehaviourStatus.SUCCESS){
                ++currentChild;
                return onUpdate();
            }
        }
        return getStatus();
    }

    public Node toDotNode() {
        Node sequence = node("sequence" + getNext())
                        .with(Shape.RECTANGLE)
                        .with(Style.FILLED, Color.rgb(getColor()).fill(), Color.BLACK.radial())
                        .with(Label.of("Sequence"))
                        .with(Rank.SAME);

        for(BehaviourNode child : children)
            sequence = sequence.link(child.toDotNode());

        return sequence;
    }
    
	@Override
	public BehaviourNode randomized(Array<BehaviourNode> prototypes) {
		int numChildren = MathUtils.random(1, 5);
		Array<BehaviourNode> children = new Array<BehaviourNode>();
		for(int i = 0; i <numChildren; i++){
			children.add(prototypes.random().randomized(prototypes));
		}
		return new SequenceNode(children);
	}

}
