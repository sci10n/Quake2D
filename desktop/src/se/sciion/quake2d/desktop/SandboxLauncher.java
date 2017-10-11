package se.sciion.quake2d.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import se.sciion.quake2d.sandbox.LevelSandbox;

public class SandboxLauncher {

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.useGL30 = true;
		config.width = 600;
		config.height = 600;
		config.resizable = false;
		
		new LwjglApplication(new LevelSandbox("levels/level_2.tmx", "levels/level_3.tmx"), config);
		//new LwjglApplication(new MutationChamber(), config);

	}
}
