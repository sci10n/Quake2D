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
import se.sciion.quake2d.level.components.InventoryComponent;
import se.sciion.quake2d.level.items.Weapon;

public class CheckWeapon extends BehaviourNode {

    private String weaponType;
    
    public CheckWeapon(String weaponType) {
    	this.weaponType = weaponType;
    }

    @Override
    protected void onEnter() {
    	setStatus(BehaviourStatus.RUNNING);
    }

    @Override
    protected BehaviourStatus onUpdate() {

		InventoryComponent inventory = entityOwner.getComponent(ComponentTypes.Inventory);

        if(inventory != null) {
        	for(Weapon w: inventory.getItems(Weapon.class)){
        		if(w.getTag().equals(weaponType)){
                	setStatus(BehaviourStatus.SUCCESS);
                	return getStatus();
        		}
        	}
        	setStatus(BehaviourStatus.FAILURE);
        }
        else {
        	setStatus(BehaviourStatus.FAILURE);
        }
        return getStatus();
    }

    @Override
    public Node toDotNode() {
        return node("checkWeapon" + getNext())
               .with(Shape.ELLIPSE)
				.with(Style.FILLED, Color.rgb(getColor()).fill(), Color.BLACK.radial())
               .with(Label.of("Has " + weaponType + "?"));
    }
    
	@Override
	public void mutate() {
			weaponType = Weapon.tags.random();
	}

	@Override
	public BehaviourNode clone() {
		return new CheckWeapon(weaponType);
	}
	
	@Override
	public BehaviourNode randomized() {
		return new CheckWeapon(Weapon.tags.random());
	}
	
	@Override
	public Element toXML(Document doc) {
		Element e = doc.createElement(getClass().getSimpleName());
		e.setAttribute("type", weaponType);
		return e;
	}
	
	@Override
	public BehaviourNode fromXML(Element element) {
		weaponType = element.getAttribute("type");
		return this;
	}
}

