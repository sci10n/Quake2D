package se.sciion.quake2d.ai.behaviour.nodes;

import static guru.nidi.graphviz.model.Factory.node;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;
import se.sciion.quake2d.ai.behaviour.BehaviourNode;
import se.sciion.quake2d.ai.behaviour.BehaviourStatus;
import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.components.HealthComponent;

public class CheckArmor extends BehaviourNode {

    private float ratio;

    public CheckArmor(float ratio) {
        this.ratio = ratio;
    }

    @Override
    protected void onEnter() {
    	setStatus(BehaviourStatus.RUNNING);
    }

    @Override
    protected BehaviourStatus onUpdate() {

    	HealthComponent health = entityOwner.getComponent(ComponentTypes.Health);
    	if(health != null && health.ratioArmor() > ratio) {
        	setStatus(BehaviourStatus.SUCCESS);
        }
        else {
        	setStatus(BehaviourStatus.FAILURE);
        }
        return getStatus();
    }

    @Override
    public Node toDotNode() {
        return node("checkArmor" + getNext())
               .with(Shape.ELLIPSE)
				.with(Style.FILLED, Color.rgb(getColor()).fill(), Color.BLACK.radial())
               .with(Label.of("Armor > " + ratio));
    }

	@Override
	public void mutate() {
			ratio += MathUtils.random(0.2f) - 0.1f;
			ratio = MathUtils.clamp(ratio, 0.0f, 1.0f);
	}
	
	@Override
	public BehaviourNode clone() {
		return new CheckArmor(ratio);
	}
	
	@Override
	public BehaviourNode randomized() {
		return new CheckArmor(MathUtils.random());
	}
	
	@Override
	public Element toXML(Document doc) {
		Element e = doc.createElement(getClass().getSimpleName());
		e.setAttribute("ratio", Float.toString(ratio));
		return e;
	}
	
	@Override
	public BehaviourNode fromXML(Element element) {
		ratio = Float.parseFloat(element.getAttribute("ratio"));
		return this;
	}
}

