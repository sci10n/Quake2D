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

public class BTVisualizer extends JFrame{
	private OrthographicCamera camera;
	private PhysicsSystem physicsSystem;

	private BotInputComponent debugBot = null;

	private int windowSize;
	private boolean paused = false;
	private boolean running = true;
	
	public BTVisualizer(int size, OrthographicCamera camera, PhysicsSystem physicsSystem) {
		super("Quake 2D - Behaviour Tree");
		this.physicsSystem = physicsSystem;
		this.windowSize = size;
		this.camera = camera;

		setFocusableWindowState(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		// Don't destroy the window just yet baby. Allow users
		// to close the window if they really want to mkay....
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				setVisible(false);
				debugBot = null;
			}
		});
		setResizable(false);
		setVisible(true);
		
		createBufferStrategy(2);
		setVisible(false);

		new Thread(){
		
			public void run() {
				while(running){
					if (debugBot == null) setVisible(false);
					else if(debugBot.getBehaviourTree().isDirty()) {
						setVisible(true);
						visualize(debugBot.getBehaviourTree());
					}
					try {
						Thread.sleep(1000/Math.max(Gdx.graphics.getFramesPerSecond(),1));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				setVisible(false);
				dispose();
			};
		}.start();
		
		
	}

	private void visualize(BehaviourTree behaviourTree) {
		Graph btGraph = behaviourTree.toDotGraph();
		BufferedImage btImage = Graphviz.fromGraph(btGraph)
			                            .width(windowSize)
			                            .render(Format.PNG).toImage();

		setSize(btImage.getWidth() + 10, btImage.getHeight() + getInsets().top + 5);
		BufferStrategy bs = getBufferStrategy();
		
		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.WHITE);
		g.drawRect(0, getInsets().top, btImage.getWidth() + 10,
				   btImage.getHeight() + 5);
		g.drawImage(btImage, 5, getInsets().top, null);
		g.dispose();
		bs.show();
	}
	
	public void setRunning(boolean running){
		this.running = running;
	}
	
	public boolean pause() {
		if (paused && Gdx.input.isButtonPressed(Buttons.LEFT)) {
			Vector3 screenMousePosition = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0.0f);
			Vector3 mousePosition = camera.unproject(screenMousePosition);
			PhysicsComponent component = physicsSystem.queryComponentAt(mousePosition.x, mousePosition.y);
			if (component != null) {
				if(component.getParent() != null){
					BotInputComponent newDebugBot = component.getParent().getComponent(ComponentTypes.BotInput);
					if(newDebugBot != null) {
						debugBot = newDebugBot;
						visualize(debugBot.getBehaviourTree());
                        setVisible(true);
					}
				}
			} else debugBot = null;
		}
		
		if(Gdx.input.isKeyPressed(Keys.P) && !paused){
			paused = true;
		} else if(Gdx.input.isKeyPressed(Keys.P) && paused){
			return false;
		} else if(Gdx.input.isKeyPressed(Keys.R)){
			paused = false;
		}
		
		return paused;
	}
}
