package se.sciion.quake2d.ai.behaviour;

import java.awt.*;
import javax.swing.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import java.awt.image.BufferedImage;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.components.PhysicsComponent;
import se.sciion.quake2d.level.system.PhysicsSystem;

public class BTVisualizer {
	private OrthographicCamera camera;
	private PhysicsSystem physicsSystem;

	private boolean paused = false;
	private boolean visualizeStep = false;
	private BotInputComponent debugBot = null;

	private JFrame visualizerWindow;
	private JLabel visualizerBuffer;

	public BTVisualizer(OrthographicCamera camera, PhysicsSystem physicsSystem) {
		this.physicsSystem = physicsSystem;
		this.camera = camera;

		visualizerWindow = new JFrame();
		visualizerWindow.setVisible(false);
		visualizerWindow.setTitle("Behaviour Tree");
		visualizerWindow.setFocusableWindowState(false);

		JPanel panel = new JPanel();
		visualizerBuffer = new JLabel();
		visualizerBuffer.setIcon(new ImageIcon());
		panel.add(visualizerBuffer);
		visualizerWindow.getContentPane().add(panel);
	}

	private void visualize(BehaviourTree behaviourTree) {
		BufferedImage btImage = Graphviz.fromGraph(behaviourTree.toDotGraph())
			                            .width(1280).render(Format.PNG).toImage();
		visualizerBuffer.setIcon(new ImageIcon(btImage));
		visualizerWindow.setVisible(true);
	}

	public boolean pause() {
		if (paused && Gdx.input.isButtonPressed(Buttons.LEFT)) {
			Vector3 screenMousePosition = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0.0f);
			Vector3 mousePosition = camera.unproject(screenMousePosition);
			PhysicsComponent component = physicsSystem.queryComponentAt(mousePosition.x, mousePosition.y);
			if (component != null) {
				BotInputComponent newDebugBot = component.getParent().getComponent(ComponentTypes.BotInput);
				if (newDebugBot != debugBot) visualizeStep = true;
				debugBot = newDebugBot;
			}
		}

		if (Gdx.input.isKeyPressed(Keys.P)) {
			paused = true;
			visualizeStep = true;
			visualizerWindow.setVisible(false);
			return false;
		} else if (visualizeStep && !Gdx.input.isKeyPressed(Keys.P)) {
			visualizeStep = false;
			if (debugBot != null)
				visualize(debugBot.getBehaviourTree());
		} else if (Gdx.input.isKeyPressed(Keys.ANY_KEY)) {
			visualizerWindow.setVisible(false);
			paused = false;
		}

		return paused;
	}
}
