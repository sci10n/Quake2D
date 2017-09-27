package se.sciion.quake2d;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;

// Real game. Should be designed once Level Sandbox is done
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

	}
	
	@Override
	public void dispose () {

	}
}
