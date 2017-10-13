package se.sciion.quake2d.ai.behaviour;
import static guru.nidi.graphviz.model.Factory.graph;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import guru.nidi.graphviz.model.Graph;
import se.sciion.quake2d.level.Entity;

public class BehaviourTree{
	
	public static boolean dirty = true;
	// Node has changed state since last we checked;
	
    protected BehaviourNode root;

    
    public BehaviourTree(){
    	
    }
    
    // Wrapper for the BehaviourTree. It is usually
    // easier to using BehaviourTreeBuilder though.
    public BehaviourTree(BehaviourNode behaviour) {
        root = behaviour;
    }

    public BehaviourStatus tick() {
        return root.tick();
    }

    public Graph toDotGraph() {
        // Traverses the tree and produced a Graphviz traversable graph.
        return graph("BehaviourTree").directed().with(root.toDotNode());
    }
	
	public boolean isDirty(){
		boolean tmp = dirty;
		dirty = false;
		return tmp;
	}
	
	public void setOwner(Entity owner){
		root.setOwner(owner);
	}
	
	
	public void crossover(BehaviourTree tree, float chance){
		
		if(!MathUtils.randomBoolean(chance)){
			return;
		}
		
		Array<BehaviourNode> nodes1 = new Array<BehaviourNode>();
		root.flatten(nodes1);
		BehaviourNode swapNode1 = nodes1.random();
		
		Array<BehaviourNode> nodes2 = new Array<BehaviourNode>();
		tree.root.flatten(nodes2);
		BehaviourNode swapNode2 = nodes2.random();
	
		BehaviourNode parent1 = swapNode1.getParent();
		BehaviourNode parent2 = swapNode2.getParent();
		if(parent1 != null){
			CompositeNode parent = (CompositeNode) parent1;
			parent.replaceChild(swapNode1, swapNode2);
		}
		else {
			root = swapNode2;
			root.setOwner(swapNode1.entityOwner);
			root.setParent(null);
		}
		
		if(parent2 != null){
			CompositeNode parent = (CompositeNode) parent2;
			parent.replaceChild(swapNode2, swapNode1);
		}
		else {
			tree.root = swapNode1;
			tree.root.setOwner(swapNode2.entityOwner);
			tree.root.setParent(null);
		}
		
		dirty = true;
	}
	
	public float getFitness(){
		return 0.0f;
	}

	public void mutate(float mutationChance) {
		if(MathUtils.randomBoolean(mutationChance)){
			Array<BehaviourNode> nodes = new Array<BehaviourNode>();
			root.flatten(nodes);
			
			if(MathUtils.randomBoolean(0.8f)){
				nodes.random().mutate();
			}
			else{
				BehaviourNode newNode = Trees.prototypes.random().randomized();
				BehaviourNode oldNode = nodes.random();
				CompositeNode parent = (CompositeNode)(oldNode.getParent());
				if(parent != null)
					parent.replaceChild(oldNode, newNode);
			}
			
			dirty = true;
		}
	}
	
	public BehaviourTree clone(){
		BehaviourTree tree = new BehaviourTree();
		tree.root = root.clone();
		return tree;
	}

	public void toXML(String filePath){
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		Document doc = null;

		try {
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			
			Element e = doc.createElement("BehaviorTree");
			e.appendChild(root.toXML(doc));
			doc.appendChild(e);
			
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filePath));
			
			TransformerFactory factory =  TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT,"yes");
			
			transformer.transform(source, result);
			
		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}
	}
	
	public BehaviourTree fromXML(String path){
		try {
			File file = new File(path);
			DocumentBuilder docBuilder;
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = docBuilder.parse(file);
			
			Element treeElement =  (Element) doc.getFirstChild();
			NodeList list = treeElement.getChildNodes();
			for(int i = 0; i < list.getLength(); i++){
			
				if(list.item(i).getNodeType() == Node.ELEMENT_NODE){
					Element rootElement = (Element) list.item(i);
					root = Trees.prototypesMap.get(rootElement.getTagName()).clone().fromXML(rootElement);
				}
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return this;
	}
	
}
