package se.sciion.quake2d.sandbox;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import se.sciion.quake2d.ai.behaviour.BehaviourTree;
import se.sciion.quake2d.ai.behaviour.Trees;
import se.sciion.quake2d.ai.behaviour.visualizer.BehaviourTreeVisualizer;
import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.Statistics;
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.components.HealthComponent;
import se.sciion.quake2d.level.system.Environment;
import se.sciion.quake2d.level.system.Pathfinding;
import se.sciion.quake2d.level.system.PhysicsSystem;
import se.sciion.quake2d.level.system.SoundSystem;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
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

public class LevelSandbox extends ApplicationAdapter {

	private AssetManager assets;

	private OrthographicCamera camera;
	
	private Level level;
	private RenderModel model;
	
	public static float WIN_TIME = 1000.0f;
	public static String TITLE = "Quake 2D";
	public static String PLAY_LEVEL = "";
    public static boolean DEBUG  = false;
	public static boolean EVOLVE = true;
	public static float GP_DELTA = 50.0f;

    private Pathfinding pathfinding;
	private BehaviourTreeVisualizer visualizer;

	private PhysicsSystem physicsSystem;
	private Environment environment;
	
	private final Array<String> levels;
	
	private Trees trees;
	
	private int width;
	private int height;
	private final int ROUND_PER_GENERATION = 5;
	private float winTimer = 0.0f;
	private int numRounds = 0;
	
	public LevelSandbox(String ... levels) {
		this.levels = new Array<String>(levels);
		int lastLevel = this.levels.size - 1;
		PLAY_LEVEL = this.levels.get(lastLevel);
	}
	
	@Override
	public void create() {
		width = (int)(2*600 * Gdx.graphics.getDensity());
		height = (int)(2*600 * Gdx.graphics.getDensity());
		Gdx.graphics.setWindowedMode(width, height);
		Gdx.graphics.setTitle("Quake 2D");

		// This is a bit weird, but still make sense I guess...
		visualizer = BehaviourTreeVisualizer.getInstance(width);
		model = new RenderModel();
		assets = new AssetManager();

		loadAssets();
		
		level = new Level();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 30, 30);
		physicsSystem = new PhysicsSystem();
		pathfinding = new Pathfinding(30, 30, level);
		trees = new Trees();
		trees.createPrototypes(level, physicsSystem, pathfinding);

		beginMatch(PLAY_LEVEL);
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
		environment = new Environment(levelPath, level, physicsSystem, pathfinding, camera, assets);
		environment.start();

		if(EVOLVE && trees.getPopulation() == null){
			trees.initPopulation(5);
		}
		
		SoundSystem.getInstance().playSound("fight");
		pathfinding.update(physicsSystem);
		
		if (EVOLVE) {
			for(Entity e: level.getEntities("player")){
				BotInputComponent input = e.getComponent(ComponentTypes.BotInput);
				if(input != null){
					BehaviourTree tree = trees.getPopulation().random();
					input.setBehaviourTree(tree);
					level.getStats().recordParticipant(tree);
				}
			}
		}
	}
	
	@Override
	public void dispose() {
		assets.dispose();
		model.spriteRenderer.dispose();
		environment.dispose();
	}

	public void endGeneration(){
		// Calculate fitness
		Statistics stats = level.getStats();
		// Select next gen of trees
		trees.select(stats);
		// Crosover
		trees.crossover();
		// Mutate
		trees.mutate();
		
		level.clearStats();
	}
	
	public void endMatch() {
		if (EVOLVE) {
			for(Entity e: level.getEntities("player")) {
				BotInputComponent input = e.getComponent(ComponentTypes.BotInput);
				if(input != null){
					float h = 0.0f;
					float a = 0.0f;
					HealthComponent health = e.getComponent(ComponentTypes.Health);
					if(health != null){
						h = health.getHealth();
						a = health.getArmor();
					}
					level.getStats().recordHealth(h, a, input.getBehaviourTree());
				}
			}
		}
		
		environment.stop();
		physicsSystem.cleanup();
		physicsSystem.clear();

		if (EVOLVE) {
			Statistics stats = level.getStats();
			System.out.println(stats.toString());
					
			numRounds++;
			if(numRounds >= ROUND_PER_GENERATION){
				endGeneration();
			}
		} else level.clearStats();

		level.cleanup();
	}

	private void toggleDebugDraw() {
		DEBUG = !DEBUG;
	}

	private void toggleEvolution() {
		EVOLVE = !EVOLVE;
		endMatch();
		beginMatch(PLAY_LEVEL);
	}

	private void findNextLevel() {
		int levelNumber = -1;
		for (int keyCode = Keys.NUM_1; keyCode <= Keys.NUM_9; ++keyCode) {
			if (Gdx.input.isKeyJustPressed(keyCode)) {
				levelNumber = keyCode - Keys.NUM_1;
				break;
			}
		}

		if (levelNumber != -1 && levelNumber < levels.size) {
			PLAY_LEVEL = levels.get(levelNumber);
			endMatch();
			beginMatch(PLAY_LEVEL);
		}
	}

	private boolean isDebugging() {
		return DEBUG;
	}
	
	@Override
	public void render() {
		findNextLevel(); // Do we switch to new levels?
		float frameDelta = Gdx.graphics.getDeltaTime();

		if (Gdx.input.isKeyPressed(Keys.LEFT) && EVOLVE)
			GP_DELTA = MathUtils.clamp(GP_DELTA - 32.0f * frameDelta, 1.0f, 512.0f);
		else if (Gdx.input.isKeyPressed(Keys.RIGHT) && EVOLVE)
			GP_DELTA = MathUtils.clamp(GP_DELTA + 32.0f * frameDelta, 1.0f, 512.0f);
		if (Gdx.input.isKeyPressed(Keys.F) && EVOLVE) {
			Gdx.graphics.setTitle(TITLE + " @ " + (int)GP_DELTA + "x");
			frameDelta *= GP_DELTA;
		} else Gdx.graphics.setTitle(TITLE);

		if (Gdx.input.isKeyJustPressed(Keys.O))
			toggleDebugDraw();
		if (Gdx.input.isKeyJustPressed(Keys.E))
			toggleEvolution();

		if (EVOLVE) SoundSystem.getInstance().setMute(true);
		else if (Gdx.input.isKeyJustPressed(Keys.M))
			SoundSystem.getInstance().toggleMute();

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
			}

			physicsSystem.render(camera.combined);
		}

		if(Gdx.input.isKeyJustPressed(Keys.Q) || !environment.isRunning()){
			endMatch();
			beginMatch(PLAY_LEVEL);
		}
	}
	
}
