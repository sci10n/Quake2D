package;

import flixel.FlxState;
import entity.Entity;
import entity.component.Sprite;
import entity.component.PlayerInput;

class PlayState extends FlxState
{

	// Store game objects. (Bags of components)
	private var entities:Array<Entity>;

	override public function create():Void
	{
		super.create();
		entities = new Array<Entity>();

		// Create player entity
		var player:Entity = new Entity();

		var playerSprite:Sprite = new Sprite(this);
		var playerInput:PlayerInput = new PlayerInput();

		player.add_component(playerSprite);
		player.add_component(playerInput);

		// Add player to game state
		entities.push(player);

	}

	override public function update(elapsed:Float):Void
	{
		super.update(elapsed);
		for(e in entities)
		{
			e.update(elapsed);
		}
	}
}
