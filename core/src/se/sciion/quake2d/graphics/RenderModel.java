package se.sciion.quake2d.graphics;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

/**
 * Wrapper around render methods using primitives.
 * Not very nice!
 * @author sciion
 *
 */
public class RenderModel {
	public boolean debugging = false;
	public SpriteBatch spriteRenderer;
	public ShapeRenderer primitiveRenderer;
	public Matrix4 projectionMatrix;
	
	public RenderModel(){
		primitiveRenderer = new ShapeRenderer();
		primitiveRenderer.setAutoShapeType(true);
		spriteRenderer = new SpriteBatch();
	}
	
	public void begin(){
		spriteRenderer.begin();
	}
	
	public void end(){
		spriteRenderer.end();
	}

	public void resetProjectionMatrices() {
		spriteRenderer.setProjectionMatrix(projectionMatrix);
		primitiveRenderer.setProjectionMatrix(projectionMatrix);
	}

	public void setProjectionMatrix(Matrix4 combined) {
		primitiveRenderer.setProjectionMatrix(combined);
		spriteRenderer.setProjectionMatrix(combined);
		projectionMatrix = combined;
	}
}
