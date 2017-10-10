package se.sciion.quake2d.sandbox;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.ai.behaviour.visualizer.BehaviourTreeVisualizer;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.Statistics;
import se.sciion.quake2d.level.system.Environment;
import se.sciion.quake2d.level.system.Pathfinding;
import se.sciion.quake2d.level.system.PhysicsSystem;
import se.sciion.quake2d.level.system.SoundSystem;


public class LevelSandbox extends ApplicationAdapter {

	private AssetManager assets;

	private OrthographicCamera camera;
	
	private Level level;
	private RenderModel model;
	
    public static boolean DEBUG = true;
    private boolean paused = false;
    
    private Pathfinding pathfinding;
	private BehaviourTreeVisualizer visualizer;

	private PhysicsSystem physicsSystem;
	private Environment environment;
	
	private final Array<String> levels;
	
	private int width;
	private int height;
	
	public LevelSandbox(String ... levels) {
		
		SoundSystem.getInstance().toggleMute();
		this.levels = new Array<String>(levels);
	}
	
	@Override
	public void create() {
		width = (int)(3*600 * Gdx.graphics.getDensity());
		height = (int)(3*600 * Gdx.graphics.getDensity());
		Gdx.graphics.setWindowedMode(width, height);
		Gdx.graphics.setTitle("Quake 2D");

		// This is a bit weird, but still make sense I guess...
		visualizer = BehaviourTreeVisualizer.getInstance(width);
		model = new RenderModel();
		assets = new AssetManager();

		loadAssets();
		beginMatch(levels.random());
	}

	public void loadAssets() {
		assets.setLoader(Texture.class, new TextureLoader(new InternalFileHandleResolver()));
		assets.setLoader(TextureAtlas.class, new TextureAtlasLoader(new InternalFileHandleResolver()));
		assets.setLoader(Sound.class, new SoundLoader(new InternalFileHandleResolver()));
		assets.setLoader(Music.class, new MusicLoader(new InternalFileHandleResolver()));

		String[] spriteSheets = {
			"images/spritesheet.atlas"
		};

		for (String spriteSheetPath : spriteSheets) {
			assets.load(spriteSheetPath, TextureAtlas.class);
		}

		String[] images = {
			"images/bullet.png",
			"images/amount.png",
			"images/muzzle.png"
		};

		for (String imagePath : images) {
			assets.load(imagePath, Texture.class);
		}

		String[] sounds = {
			"audio/armor.wav",
			"audio/damage.wav",
			"audio/fight.wav",
			"audio/health.wav",
			"audio/hit.wav",
			"audio/impressive.wav",
			"audio/move1.wav",
			"audio/move2.wav",
			"audio/rifle.wav",
			"audio/shotgun.wav",
			"audio/sniper.wav",
			"audio/weapon.wav",
		};

		for (String soundPath : sounds) {
			assets.load(soundPath, Sound.class);
		}

		assets.finishLoading();
		
		// Map over the path file names to logical name.
		SoundSystem.getInstance().setup(assets, sounds);
	}
	
	
	public void beginMatch(String levelPath) {
		level = new Level();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 30, 30);
		physicsSystem = new PhysicsSystem();
		pathfinding = new Pathfinding(30, 30, level);
		
		environment = new Environment(levelPath, level, physicsSystem, pathfinding, camera, assets);
		environment.start();

		SoundSystem.getInstance().playSound("fight");
		
		pathfinding.update(physicsSystem);
	}
	
	@Override
	public void dispose() {
		assets.dispose();
		model.spriteRenderer.dispose();
	}

	public void endMatch() {
		environment.stop();
		physicsSystem.cleanup();
		physicsSystem.dispose();
		environment.dispose();
		
		Statistics stats = level.getStats();

		SoundSystem.getInstance().playSound("impressive");

		beginMatch(levels.random());
	}

	private void toggleDebugDraw() {
		DEBUG = !DEBUG;
	}

	private boolean isDebugging() {
		return DEBUG;
	}
	
	@Override
	public void render() {
		final float frameDelta = Gdx.graphics.getDeltaTime() * 1.0f;
		if (Gdx.input.isKeyJustPressed(Keys.O))
			toggleDebugDraw();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

		camera.update();

		if(environment != null && environment.isRunning()) {

			if (!visualizer.isPaused()) {
				level.tick(frameDelta);
				physicsSystem.update(frameDelta);
				physicsSystem.cleanup();

				pathfinding.update(physicsSystem);
				environment.tick(frameDelta);
			}

			model.setProjectionMatrix(camera.combined);
			environment.render(model);

			if (isDebugging()) {
				pathfinding.render(model);
				level.debugRender(model);
				pathfinding.render(model);
			}
			physicsSystem.render(camera.combined);

			
		}
		if(Gdx.input.isKeyJustPressed(Keys.Q) || !environment.isRunning()){
			endMatch();
			beginMatch(levels.random());
		}
	}
	
}
