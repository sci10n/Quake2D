package se.sciion.quake2d.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class SheetRegion {
	public TextureRegion texture;
	public Vector2 offset = new Vector2(0.0f, 0.0f);
	public Vector2 origin = new Vector2(0.0f, 0.0f);
	public float rotation = 0.0f;
	public Vector2 scale = new Vector2(0.0f, 0.0f);
}
