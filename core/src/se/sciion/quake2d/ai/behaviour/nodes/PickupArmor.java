package se.sciion.quake2d.ai.behaviour.nodes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.ai.behaviour.BehaviourNode;
import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.components.HealthComponent;

public class PickupArmor extends PickupConsumable {

	private int previousArmor;
	
	public PickupArmor(Level level, String tag) {
		super(level, tag);
		previousArmor = 0;
	}

	@Override
	protected boolean restored() {
		HealthComponent health = entityOwner.getComponent(ComponentTypes.Health);
		boolean restored = false;
		if(health != null){
			restored = health.getArmor() > previousArmor || health.getArmor() >= health.MAX_ARMOR;
		}
		
		previousArmor = health.getArmor();
		return restored;
	}

	@Override
	public BehaviourNode clone() {
		return new PickupArmor(level, tag);
	}

	@Override
	public BehaviourNode randomized() {
		return new PickupArmor(level, tag);
	}
	
	@Override
	public Element toXML(Document doc) {
		Element e = doc.createElement(getClass().getSimpleName());
		e.setAttribute("tag", tag);
		return e;
	}
	
	@Override
	public BehaviourNode fromXML(Element element) {
		tag = element.getAttribute("tag");
		return this;
	}
}
