package se.sciion.quake2d.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import se.sciion.quake2d.sandbox.LevelSandbox;

public class SandboxLauncher {

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.useGL30 = false;
		config.width = 800;
		config.height = 600;
		config.resizable = false;
		config.gles30ContextMajorVersion = 4;
		config.gles30ContextMinorVersion = 5;
		
		new LwjglApplication(new LevelSandbox("levels/level_1.tmx", "levels/level_2.tmx"), config);
	}
}
