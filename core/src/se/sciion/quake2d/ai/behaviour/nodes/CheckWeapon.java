package se.sciion.quake2d.ai.behaviour.nodes;

import static guru.nidi.graphviz.model.Factory.node;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;
import se.sciion.quake2d.ai.behaviour.BehaviourNode;
import se.sciion.quake2d.ai.behaviour.BehaviourStatus;
import se.sciion.quake2d.level.components.InventoryComponent;
import se.sciion.quake2d.level.items.Weapon;

public class CheckWeapon extends BehaviourNode {

    private String weaponType;
    private InventoryComponent inventory;
    
    public CheckWeapon(String weaponType, InventoryComponent inventory) {
    	this.weaponType = weaponType;
    	this.inventory = inventory;
    }

    @Override
    protected void onEnter() {
    	setStatus(BehaviourStatus.RUNNING);
    }

    @Override
    protected BehaviourStatus onUpdate() {

        if(inventory != null) {
        	for(Weapon w: inventory.getItems(Weapon.class)){
        		if(w.getTag().equals(weaponType)){
                	setStatus(BehaviourStatus.SUCCESS);
        		}
        	}
        }
        else {
        	setStatus(BehaviourStatus.FAILURE);
        }
        return getStatus();
    }

    @Override
    public Node toDotNode() {
        return node("checkHealth" + getNext())
               .with(Shape.ELLIPSE)
				.with(Style.FILLED, Color.rgb(getColor()).fill(), Color.BLACK.radial())
               .with(Label.of("Weapon of type > " + weaponType));
    }
}

