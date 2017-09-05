package se.sciion.quake2d.sandbox;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import se.sciion.quake2d.ai.behaviour.BehaviourNode;
import se.sciion.quake2d.ai.behaviour.BehaviourStatus;
import se.sciion.quake2d.ai.behaviour.BehaviourTree;
import se.sciion.quake2d.ai.behaviour.SelectorNode;
import se.sciion.quake2d.ai.behaviour.SequenceNode;
import se.sciion.quake2d.ai.behaviour.nodes.NoOpNode;

// More in the style of unit test
public class BehaviorSandbox extends ApplicationAdapter{

	@Override
	public void create() {
		
		{
			BehaviourNode rootNode = new NoOpNode(BehaviourStatus.SUCCESS);
			BehaviourTree tree = new BehaviourTree(rootNode);
			
			System.out.println("Expecting Success " + tree.tick());
		}
		
		{
			SequenceNode rootNode = new SequenceNode();
			rootNode.addChild(new NoOpNode(BehaviourStatus.SUCCESS));
			rootNode.addChild(new NoOpNode(BehaviourStatus.RUNNING));
			BehaviourTree tree = new BehaviourTree(rootNode);
			
			System.out.println("Expecting Running " + tree.tick());
		}
		
		{
			SelectorNode rootNode = new SelectorNode();
			rootNode.addChild(new NoOpNode(BehaviourStatus.FAILURE));
			rootNode.addChild(new NoOpNode(BehaviourStatus.SUCCESS));
			BehaviourTree tree = new BehaviourTree(rootNode);
			
			System.out.println("Expecting Success " + tree.tick());
		}
		
		{
			SelectorNode rootNode = new SelectorNode();
			rootNode.addChild(new NoOpNode(BehaviourStatus.RUNNING));
			rootNode.addChild(new NoOpNode(BehaviourStatus.SUCCESS));
			BehaviourTree tree = new BehaviourTree(rootNode);
			
			System.out.println("Expecting Running " + tree.tick());
		}
		
		{
			SequenceNode rootNode = new SequenceNode();
			rootNode.addChild(new NoOpNode(BehaviourStatus.RUNNING));
			rootNode.addChild(new NoOpNode(BehaviourStatus.FAILURE));
			BehaviourTree tree = new BehaviourTree(rootNode);
			
			System.out.println("Expecting Running " + tree.tick());
		}
		
		Gdx.app.exit();
	}
	
}
