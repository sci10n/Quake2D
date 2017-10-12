package se.sciion.quake2d.sandbox;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import se.sciion.quake2d.ai.behaviour.BehaviourTree;
import se.sciion.quake2d.ai.behaviour.Trees;
import se.sciion.quake2d.ai.behaviour.nodes.AttackNearest;
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
	
    public static boolean DEBUG = true;
    private boolean paused = false;
    
    private Pathfinding pathfinding;
	private BehaviourTreeVisualizer visualizer;

	private PhysicsSystem physicsSystem;
	private Environment environment;
	
	private final Array<String> levels;
	
	private Trees trees;
	
	private int width;
	private int height;
	private int ROUND_PER_GENERATION;
	private int numRounds = 0;
	
	public LevelSandbox(String ... levels) {
		SoundSystem.getInstance().toggleMute();
		this.levels = new Array<String>(levels);
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
	
	private int counter1 = 0;
	private int counter2 = 0;
	public void beginMatch(String levelPath) {
		environment = new Environment(levelPath, level, physicsSystem, pathfinding, camera, assets);
		environment.start();

		if(trees.getPopulation() == null){
			trees.initPopulation();
			ROUND_PER_GENERATION = trees.populationLimit;
		}
		
		SoundSystem.getInstance().playSound("fight");
		
		System.out.println("Trees: " + counter1 + " " + counter2 + " fight!");
		pathfinding.update(physicsSystem);
		Array<Entity> players = level.getEntities("player");
		
		
		BotInputComponent input = players.get(1).getComponent(ComponentTypes.BotInput);
		if(input != null){
			BehaviourTree hardCoded = new BehaviourTree(new AttackNearest("player", level, physicsSystem));
			BehaviourTree tree = trees.getPopulation().get(counter1);
			input.setBehaviourTree(tree);
			level.getStats().recordParticipant(tree);
		}
		
		BotInputComponent input2 = players.get(0).getComponent(ComponentTypes.BotInput);
		if(input2 != null){
			input2.setBehaviourTree(trees.getEnemy(level, physicsSystem, pathfinding));
		}
		counter1 = (counter1 + 1) % trees.populationLimit;
	}
	
	@Override
	public void dispose() {
		assets.dispose();
		model.spriteRenderer.dispose();
		environment.dispose();
	}

	private int genCounter = 0;
	public void endGeneration(){
		System.out.println("Gen: " + ++genCounter);
		// Calculate fitness
		Statistics stats = level.getStats();
		// Select next gen of trees
		trees.select(stats);
		// Crosover
		trees.crossover();
		// Mutate
		trees.mutate();

		level.clearStats();
		
		counter1 = 0;
		counter2 = 0;
	}
	
	public void endMatch() {
		for(Entity e: level.getEntities("player")){
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
		
		environment.stop();
		physicsSystem.cleanup();
		physicsSystem.clear();
				
		//System.out.println(stats.toString());
		SoundSystem.getInstance().playSound("impressive");
				
		numRounds++;
		if(numRounds > ROUND_PER_GENERATION){
			endGeneration();
			numRounds = 0;
		}
		level.cleanup();
	}

	private void toggleDebugDraw() {
		DEBUG = !DEBUG;
	}

	private boolean isDebugging() {
		return DEBUG;
	}
	
	private float timescale = 1.0f;
	@Override
	public void render() {
		if(Gdx.input.isKeyJustPressed(Keys.NUM_1)){
			timescale = 1.0f;
		}
		if(Gdx.input.isKeyJustPressed(Keys.NUM_2)){
			timescale = 2.0f;
		}
		if(Gdx.input.isKeyJustPressed(Keys.NUM_3)){
			timescale = 8.0f;
		}
		if(Gdx.input.isKeyJustPressed(Keys.NUM_4)){
			timescale = 32.0f;
		}
		
		float frameDelta = Gdx.graphics.getDeltaTime() * timescale;

		
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
			}
			physicsSystem.render(camera.combined);
		}

		if(Gdx.input.isKeyJustPressed(Keys.Q) || !environment.isRunning()){
			endMatch();
			beginMatch(levels.random());
		}
	}
	
}
