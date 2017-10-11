package se.sciion.quake2d.ai.behaviour;

import static guru.nidi.graphviz.model.Factory.node;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

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

    public SelectorNode(Array<BehaviourNode> children){
    	super(children);
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
        if (currentChild < children.size && children.size != 0) {
        	setStatus(children.get(currentChild).tick());
            if (getStatus() == BehaviourStatus.FAILURE) {
                if (currentChild < children.size) {
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
    
    @Override
    public void mutate(float chance) {
		if(MathUtils.randomBoolean(chance)){
			int t = MathUtils.random(2);
			// Remove random child
			if(t == 0 && children.size > 0) {
				children.removeIndex(MathUtils.random(0, children.size-1));
			}
			// Add new child
			if(t == 1) {
				children.add(Trees.prototypes.random().clone());
			}
			
			// Shuffle
			if(t == 2){
				children.shuffle();
			}
			
			for(int i = 0; i< children.size; i++){
				children.get(i).mutate(chance);
			}
		}
    }
	@Override
	public BehaviourNode clone() {
		SelectorNode node = new SelectorNode();
		for(int i = 0; i< children.size; i++){
			node.addChild(children.get(i).clone());
		}
		return node;
	}
	
	@Override
	public BehaviourNode randomized() {
		int numChild = MathUtils.random(1,5);
		Array<BehaviourNode> children = new Array<BehaviourNode>();
		for(int i = 0; i < numChild ; i++)
			children.add(Trees.prototypes.random().randomized());
		
		return new SelectorNode(children);
	}
	
}
