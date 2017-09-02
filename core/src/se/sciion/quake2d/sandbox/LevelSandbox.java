package se.sciion.quake2d.sandbox;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.HardcodedLevel;
import se.sciion.quake2d.level.components.PlayerInputComponent;
import se.sciion.quake2d.level.components.SpriteComponent;

public class LevelSandbox extends ApplicationAdapter{

	OrthographicCamera camera;
	SpriteBatch batch;
	
	private HardcodedLevel level;
	private AssetManager assets;
	
	
	@Override
	public void create () {
		
		// Set up level object
		camera = new OrthographicCamera();
		batch = new SpriteBatch();
		loadAssets();
		
		// Bag of entities
		Array<Entity> entities = new Array<Entity>();
		
		Entity playerEntity = new Entity();
		
		SpriteComponent playerSprite = new SpriteComponent(0, 0, 1, 1, assets.get("textures/Dummy.png", Texture.class));
		PlayerInputComponent playerMovement = new PlayerInputComponent();
		
		playerEntity.addComponent(playerSprite);
		playerEntity.addComponent(playerMovement);
		
		entities.add(playerEntity);
		
		level = new HardcodedLevel(entities);
	}
	
	public void loadAssets(){
		assets = new AssetManager();
		assets.setLoader(Texture.class, new TextureLoader(new InternalFileHandleResolver()));
		
		assets.load("textures/Dummy.png", Texture.class);
		
		assets.finishLoading();
	}
	
	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false, width / 32.0f, height / 32.0f);
		super.resize(width, height);
	}
	
	@Override
	public void render () {
		
		camera.update();
		level.tick(Gdx.graphics.getDeltaTime());
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		level.render(batch);
		batch.end();
	}
	
	@Override
	public void dispose () {

	}
}
