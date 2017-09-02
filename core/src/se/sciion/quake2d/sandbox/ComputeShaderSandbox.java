package se.sciion.quake2d.sandbox;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import se.sciion.quake2d.graphics.ComputeShader;

public class ComputeShaderSandbox  extends ApplicationAdapter{

	SpriteBatch batch;
	OrthographicCamera camera;
	ComputeShader shader;
	@Override
	public void create () {
		camera = new OrthographicCamera();
		batch = new SpriteBatch();
		
		shader = new ComputeShader("shader.comp", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
		
		shader.render();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(shader.getBuffer(), 0, 0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		shader.dispose();
	}
}
