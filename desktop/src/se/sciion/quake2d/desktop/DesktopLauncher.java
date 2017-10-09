package se.sciion.quake2d.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import se.sciion.quake2d.Quake2DGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.useGL30 = false;
		config.width = 800;
		config.height = 600;
		config.resizable = false;
		config.gles30ContextMajorVersion = 4;
		config.gles30ContextMinorVersion = 5;
		
		new LwjglApplication(new Quake2DGame(), config);
	}
}
