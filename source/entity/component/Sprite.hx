package entity.component;

import flixel.FlxSprite;
import flixel.FlxState;
import flixel.util.FlxColor;

class Sprite extends Component {

    private var sprite:FlxSprite;

    public function new(world:FlxState) 
    {
        sprite = new FlxSprite();
        sprite.makeGraphic(32, 32, FlxColor.WHITE);
        world.add(sprite);
    }

    override public function get_type():ComponentType 
    {
         return Sprite; 
    }

    override public function update(elapsed:Float):Void
    { 
            sprite.update(elapsed);
    }

    public function get_sprite():FlxSprite
    {
        return sprite;
    }
}

