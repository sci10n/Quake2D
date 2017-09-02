package;

import flixel.FlxState;
import entity.Entity;
import entity.component.Sprite;
import entity.component.PlayerInput;
import entity.component.Physics;
import nape.phys.BodyType;
import flixel.FlxG;
import flixel.system.scaleModes.FillScaleMode;
import flixel.system.scaleModes.FixedScaleMode;
import flixel.system.scaleModes.RatioScaleMode;
import flixel.system.scaleModes.RelativeScaleMode;

import overlay.PhysicsOverlay;

class PlayState extends FlxState
{

	// Store game objects. (Bags of components)
	private var entities:Array<Entity>;

	// Physics system
	private var physics:PhysicsOverlay;

	override public function create():Void
	{
		super.create();
		FlxG.scaleMode = new RelativeScaleMode(32,32);
		FlxG.camera.scroll.x = -FlxG.width/2;
		FlxG.camera.scroll.y = -FlxG.height/2;

		// Create physics system (mostly wrapper around Nape.Space)
		physics = new PhysicsOverlay();

		// Create collection of entities
		entities = new Array<Entity>();

		// Create player entity
		var player:Entity = new Entity();

		var playerSprite:Sprite = new Sprite(this,1,1);
		var playerInput:PlayerInput = new PlayerInput();

		// Fucking retarded render system requires stuff in pix corrdinates
		var playerPhysics:Physics = new Physics(physics.create_body(0,0,1,1,BodyType.DYNAMIC));
		player.add_component(playerSprite);
		player.add_component(playerInput);
		player.add_component(playerPhysics);

		// Add player to game state
		entities.push(player);
		
		// Create arena
		var leftWall:Entity = new Entity();
		leftWall.add_component(new Sprite(this,1,20));
		leftWall.add_component(new Physics(physics.create_body(-10,0,1,20, BodyType.STATIC)));
		entities.push(leftWall);

		var rightWall:Entity = new Entity();
		rightWall.add_component(new Sprite(this,1,20));
		rightWall.add_component(new Physics(physics.create_body(10,0,1,20, BodyType.STATIC)));
		entities.push(rightWall);

		var upWall:Entity = new Entity();
		upWall.add_component(new Sprite(this,20,1));
		upWall.add_component(new Physics(physics.create_body(0,7.5,20,1,BodyType.STATIC)));
		entities.push(upWall);

		var downWall:Entity = new Entity();
		downWall.add_component(new Sprite(this,20,1));
		downWall.add_component(new Physics(physics.create_body(0,-7.5,20,1, BodyType.STATIC)));
		entities.push(downWall);
	}

	override public function update(elapsed:Float):Void
	{
		super.update(elapsed);
		FlxG.camera.update(elapsed);
		// Placed before systemic updates to deal with current inputs
		for(e in entities)
		{
			e.update(elapsed);
		}

		physics.update(elapsed);

	}
}
