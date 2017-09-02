package se.sciion.quake2d.level.system;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

public abstract class System {

	public abstract void render(Matrix4 combined);
	public abstract void update(float delta);
	
}
