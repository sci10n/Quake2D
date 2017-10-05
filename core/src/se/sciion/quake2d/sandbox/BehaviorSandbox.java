package se.sciion.quake2d.sandbox;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import se.sciion.quake2d.ai.behaviour.BehaviourNode;
import se.sciion.quake2d.ai.behaviour.BehaviourStatus;
import se.sciion.quake2d.ai.behaviour.BehaviourTree;
import se.sciion.quake2d.ai.behaviour.SelectorNode;
import se.sciion.quake2d.ai.behaviour.SequenceNode;
import se.sciion.quake2d.ai.behaviour.nodes.NoOp;

// More in the style of unit test
public class BehaviorSandbox extends ApplicationAdapter{

	@Override
	public void create() {
		
		{
			BehaviourNode rootNode = new NoOp(BehaviourStatus.SUCCESS);
			BehaviourTree tree = new BehaviourTree(rootNode);
			
			System.out.println("Expecting Success " + tree.tick());
		}
		
		{
			SequenceNode rootNode = new SequenceNode();
			rootNode.addChild(new NoOp(BehaviourStatus.SUCCESS));
			rootNode.addChild(new NoOp(BehaviourStatus.RUNNING));
			BehaviourTree tree = new BehaviourTree(rootNode);
			
			System.out.println("Expecting Running " + tree.tick());
		}
		
		{
			SelectorNode rootNode = new SelectorNode();
			rootNode.addChild(new NoOp(BehaviourStatus.FAILURE));
			rootNode.addChild(new NoOp(BehaviourStatus.SUCCESS));
			BehaviourTree tree = new BehaviourTree(rootNode);
			
			System.out.println("Expecting Success " + tree.tick());
		}
		
		{
			SelectorNode rootNode = new SelectorNode();
			rootNode.addChild(new NoOp(BehaviourStatus.RUNNING));
			rootNode.addChild(new NoOp(BehaviourStatus.SUCCESS));
			BehaviourTree tree = new BehaviourTree(rootNode);
			
			System.out.println("Expecting Running " + tree.tick());
		}
		
		{
			SequenceNode rootNode = new SequenceNode();
			rootNode.addChild(new NoOp(BehaviourStatus.RUNNING));
			rootNode.addChild(new NoOp(BehaviourStatus.FAILURE));
			BehaviourTree tree = new BehaviourTree(rootNode);
			
			System.out.println("Expecting Running " + tree.tick());
		}
		
		Gdx.app.exit();
	}
	
}
