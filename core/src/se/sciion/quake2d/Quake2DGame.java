package se.sciion.quake2d;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;


public class Quake2DGame extends ApplicationAdapter {
	
	OrthographicCamera camera;
	
	
	@Override
	public void create () {
		camera = new OrthographicCamera();
	}

	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false, width, height);
		super.resize(width, height);
	}
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
		
	}
	
	@Override
	public void dispose () {

	}
}
