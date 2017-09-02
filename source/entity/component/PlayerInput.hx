package entity.component;

import flixel.FlxG;
import nape.phys.Body;
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
        var scl:Float = 4.0;
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


        var physics:Physics = cast(parent.get_component(Physics));

        if(physics != null) {
            var b:Body = physics.get_body();
            var len:Float = Math.sqrt(dx*dx + dy*dy);

            if(len == 0)
                b.velocity.setxy(dx,dy);
            else
                b.velocity.setxy((dx/len) * scl,(dy/len) * scl);
        }
        
    }
}