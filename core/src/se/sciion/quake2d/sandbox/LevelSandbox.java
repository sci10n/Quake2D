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

import se.sciion.quake2d.ai.behaviour.visualizer.BTVisualizer;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.Statistics;
import se.sciion.quake2d.level.system.Environemnt;
import se.sciion.quake2d.level.system.Pathfinding;
import se.sciion.quake2d.level.system.PhysicsSystem;


public class LevelSandbox extends ApplicationAdapter {

	private AssetManager assets;

	private OrthographicCamera camera;
	
	private Level level;
	private RenderModel model;
	
    private boolean debugging = false;

    private Pathfinding pathfinding;
	private BTVisualizer visualizer;

	private PhysicsSystem physicsSystem;
	private Environemnt environment;
	
	private final Array<String> levels;
	
	private int width;
	private int height;
	
	public LevelSandbox(String ... levels) {
		this.levels = new Array<String>(levels);
	}
	
	@Override
	public void create() {
		width = (int)(2*600 * Gdx.graphics.getDensity());
		height = (int)(2*600 * Gdx.graphics.getDensity());
		
		Gdx.graphics.setTitle("Quake 2D");
		Gdx.graphics.setWindowedMode(width, height);

		visualizer = new BTVisualizer(width, camera, physicsSystem);
		model = new RenderModel();
		loadAssets();
		beginMatch(levels.random());
	}

	public void loadAssets() {
		assets = new AssetManager();
		assets.setLoader(Texture.class, new TextureLoader(new InternalFileHandleResolver()));
		assets.setLoader(TextureAtlas.class, new TextureAtlasLoader(new InternalFileHandleResolver()));
		assets.setLoader(Sound.class, new SoundLoader(new InternalFileHandleResolver()));
		assets.setLoader(Music.class, new MusicLoader(new InternalFileHandleResolver()));
		
		assets.load("images/spritesheet.atlas", TextureAtlas.class);
		assets.load("images/bullet.png", Texture.class);
		assets.load("images/amount.png", Texture.class);
		assets.load("images/muzzle.png", Texture.class);
		
		assets.load("audio/armor.wav", Sound.class);
		assets.load("audio/damage.wav", Sound.class);
		assets.load("audio/fight.wav", Sound.class);
		assets.load("audio/hit.wav", Sound.class);
		assets.load("audio/impressive.wav", Sound.class);
		assets.load("audio/move1.wav", Sound.class);
		assets.load("audio/move2.wav", Sound.class);
		assets.load("audio/rifle.wav", Sound.class);
		assets.load("audio/shotgun.wav", Sound.class);
		assets.load("audio/sniper.wav", Sound.class);
		assets.load("audio/weapon.wav", Sound.class);
		//assets.load("audio/music.ogg", Music.class);

		while(!assets.update()) {
			
			System.out.println(assets.getProgress());
		}
		System.out.println("ASSETS LOADDED!");
	}
	
	
	public void beginMatch(String levelPath) {
		level = new Level();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 30, 30);
		physicsSystem = new PhysicsSystem();
		pathfinding = new Pathfinding(30, 30, level);
		
		environment = new Environemnt(levelPath, level, physicsSystem, pathfinding, camera, assets);
		environment.start();
		
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
		System.out.println(stats.toString());
	}
	
	@Override
	public void render() {
		final float frameDelta = Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyJustPressed(Keys.O))
			debugging = !debugging;
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
		if(environment != null && environment.isRunning()) {
			camera.update();
	
			if (!visualizer.pause()) {
				level.tick(frameDelta);
				physicsSystem.update(frameDelta);
				physicsSystem.cleanup();
				pathfinding.update(physicsSystem);
				environment.tick(frameDelta);
			}
	

			model.setProjectionMatrix(camera.combined);
			environment.render(model);
			if (debugging) {
				pathfinding.render(model);
				physicsSystem.render(camera.combined);
				level.debugRender(model);
			}
			
			if(Gdx.input.isKeyJustPressed(Keys.Q))
				endMatch();
		}
	}
	

}
