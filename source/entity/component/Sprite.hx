package entity.component;

import flixel.FlxSprite;
import flixel.FlxState;
import flixel.util.FlxColor;

class Sprite extends Component {

    private var sprite:FlxSprite;
    private var scl:Int = 1;

    public function new(world:FlxState, w:Float,h:Float) 
    {
        sprite = new FlxSprite();
        var color:FlxColor = new FlxColor();
        color.setRGBFloat(Math.random(),Math.random(),Math.random(),1.0);
        sprite.makeGraphic(cast(w), cast(h), color);
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

