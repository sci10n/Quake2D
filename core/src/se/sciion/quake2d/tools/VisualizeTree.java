package se.sciion.quake2d.tools;

import java.io.File;

import com.badlogic.gdx.Gdx;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import se.sciion.quake2d.ai.behaviour.BehaviourTree;
import se.sciion.quake2d.ai.behaviour.Trees;

public class VisualizeTree {

	public static void main(String[] args) {
		
		Trees trees = new Trees();
		trees.createPrototypes(null, null, null);
		String path = "trees_agains_bot/se.sciion.quake2d.ai.behaviour.BehaviourTree@176f5bd2_76_4750.0";
		BehaviourTree tree = new BehaviourTree();
		tree.fromXML(path);
		
		Graph g = tree.toDotGraph();
		System.out.println(g);
		try {
			File file = new File("export/" + path.subSequence(0, path.length()) + ".png");
			Graphviz.fromGraph(g)
					.width(1024)
					.render(Format.PNG).toFile(file);
		} catch (Exception hehNotEvenOnce) {
			// Be naughty and never catch exceptions, that's the true Java way.
			hehNotEvenOnce.printStackTrace();
		}
	}

}
