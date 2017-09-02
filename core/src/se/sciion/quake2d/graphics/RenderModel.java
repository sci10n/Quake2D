package se.sciion.quake2d.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

public class RenderModel {

	public ShapeRenderer primitiveRenderer;
	public SpriteBatch spriteRenderer;
	
	public RenderModel(){
		primitiveRenderer = new ShapeRenderer();
		spriteRenderer = new SpriteBatch();
		primitiveRenderer.setAutoShapeType(true);
	}
	
	
	public void begin(){
		primitiveRenderer.begin();
		spriteRenderer.begin();
	}
	
	
	public void end(){
		primitiveRenderer.end();
		spriteRenderer.end();
	}

	public void setProjectionMatrix(Matrix4 combined) {
		primitiveRenderer.setProjectionMatrix(combined);
		spriteRenderer.setProjectionMatrix(combined);
	}
}
