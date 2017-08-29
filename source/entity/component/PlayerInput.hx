package entity.component;

import flixel.FlxG;
import flixel.FlxSprite;

class PlayerInput extends Component
{

    public function new() 
    {
       
    }

    override public function get_type():ComponentType 
    {
         return PlayerInput; 
    }

    override public function update(elapsed:Float):Void
    { 

        // Updates sprite velocity based on keyboard input
        var scl:Float = 32.0;
        var dx:Float = 0.0;
        var dy:Float = 0.0;

        if(FlxG.keys.pressed.W) 
        {
            dy = -1;
        }
        else if(FlxG.keys.pressed.S) 
        {
            dy = 1;
        }

        if(FlxG.keys.pressed.A) 
        {
            dx = -1;
        }
        else if(FlxG.keys.pressed.D) 
        {
            dx = 1;
        }

        var sprite:Sprite = cast(parent.get_component(Sprite));

        if(sprite != null)
        {
            var s:FlxSprite = sprite.get_sprite();
            var len:Float = Math.sqrt(dx*dx + dy*dy); 
            if(len != 0)
                len = 1.0/len;
            trace(len);
            s.velocity.x = dx;
            s.velocity.y = dy;
            s.velocity.scale(scl * len);
        }
    }
}