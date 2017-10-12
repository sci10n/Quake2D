package se.sciion.quake2d.ai.behaviour.visualizer;

import java.awt.*;
import javax.swing.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import se.sciion.quake2d.ai.behaviour.BehaviourTree;
import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.components.PhysicsComponent;
import se.sciion.quake2d.level.system.PhysicsSystem;

public class BehaviourTreeVisualizer extends JFrame {
	private static final long serialVersionUID = 1L;

	private BotInputComponent debugBot = null;
	private static BehaviourTreeVisualizer instance = null;

	private int windowSize;
	private boolean paused = false;
	private boolean running = true;
	private boolean visible = true;
	
	private BufferedImage offscreen;
	
	public BehaviourTreeVisualizer(int size) {
		super("Quake 2D - Behaviour Tree Visualizer");

		running = true;
		setResizable(false);
		this.windowSize = size;
		setFocusableWindowState(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setVisible(true);
		createBufferStrategy(2);

		new Thread() {
			
			public void run() {
				while(running){
					if (debugBot != null && isVisible()) {
						visualize(debugBot.getBehaviourTree());
					}

					try {
						Thread.sleep(1000 / Math.max(Gdx.graphics.getFramesPerSecond(), 1));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				setVisible(false);
				dispose();
			};

		}.start();
	}

	public static BehaviourTreeVisualizer getInstance() {
		return instance;
	}

	public static BehaviourTreeVisualizer getInstance(int size) {
		if (instance == null)
			instance = new BehaviourTreeVisualizer(size);
		return instance;
	}

	private synchronized void visualize(BehaviourTree behaviourTree) {
		int xOffset = 10;
		int yOffset = 5;

		Graph btGraph = behaviourTree.toDotGraph();
		if(offscreen == null || behaviourTree.isDirty() || paused) {
			offscreen = Graphviz.fromGraph(btGraph)
			                    .width(windowSize)
			                    .render(Format.PNG).toImage();

			setSize(offscreen.getWidth()  + xOffset,
			        offscreen.getHeight() + getInsets().top + yOffset);
		}
		
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) // Something is fishy....
			return;
		
		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.WHITE);
		g.drawRect(0, getInsets().top, offscreen.getWidth()  + xOffset,
		                               offscreen.getHeight() + yOffset);
		g.drawImage(offscreen, yOffset, getInsets().top, null);
		g.dispose();

		bs.show();
	}
	
	public void setRunning(boolean running){
		this.running = running;
	}

	public boolean isRunning() {
		return running;
	}
	
	public void setDebugBot(BotInputComponent component){
		this.debugBot = component;
	}

	public BotInputComponent getDebugBot() {
		return debugBot;
	}
	
	public boolean isPaused() {
		if(Gdx.input.isKeyPressed(Keys.P) && !paused)
			paused = true;
		else if(Gdx.input.isKeyPressed(Keys.P) && paused)
			return false;
		else if(Gdx.input.isKeyPressed(Keys.R))
			paused = false;
		return paused;
	}
}
