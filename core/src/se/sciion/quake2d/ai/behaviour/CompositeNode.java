package se.sciion.quake2d.ai.behaviour;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.level.Entity;

public abstract class CompositeNode extends BehaviourNode {
	protected Array<BehaviourNode> children; // Sibling children.
	protected int currentChild; // Current index
	
	public CompositeNode(){
		children = new Array<BehaviourNode>();
		currentChild = 0;
	}
	
	public CompositeNode(Array<BehaviourNode> nodes){
		children = nodes;
		for(BehaviourNode n : children) {
			n.setParent(this);
		}
		currentChild = 0;
	}
	
	public CompositeNode(BehaviourNode... nodes) {
		children = new Array<BehaviourNode>(nodes);
		for (BehaviourNode n : children) {
			n.setParent(this);
			n.setOwner(entityOwner);
		}
		currentChild = 0;
	}

	public void addChild(BehaviourNode node) {
		if (currentChild == 0) // Prevent modifying during execution.
		{
			children.add(node);
			node.setParent(this);
			node.setOwner(entityOwner);
		}
	}

	public void removeChild(BehaviourNode node) {
		if (currentChild == 0) // Prevent modifying during execution.
			children.removeValue(node, true);
	}
	
	public void replaceChild(BehaviourNode child, BehaviourNode replacement){
		int indexOf = children.indexOf(child, true);
		children.set(indexOf, replacement);
		replacement.setParent(this);
		replacement.setOwner(entityOwner);
	}
	
	@Override
	public void flatten(Array<BehaviourNode> nodes) {
		nodes.add(this);
		for (BehaviourNode n : children) {
			n.flatten(nodes);
		}
	}
	
	@Override
	public void setOwner(Entity parent) {
		this.entityOwner = parent;
		for (BehaviourNode n : children) {
			n.setOwner(parent);
		}
	}
	
	@Override
	public BehaviourNode fromXML(Element element) {
		NodeList list = element.getChildNodes();
		for(int i = 0 ; i < list.getLength(); i++){
			if(list.item(i).getNodeType() == Node.ELEMENT_NODE){
				Element n = (Element) list.item(i);
				BehaviourNode child = Trees.prototypesMap.get(n.getTagName()).clone().fromXML(n);
				children.add(child);
			}

		}
		
		return this;
	}
	
	@Override
	public Element toXML(Document doc) {
		Element e = doc.createElement(getClass().getSimpleName());
		for(BehaviourNode n: children){
			e.appendChild(n.toXML(doc));
		}
		doc.appendChild(e);
		return e;
	}
	
}
