package entity.component;

import nape.phys.Body;


import flixel.FlxSprite;

class Physics extends Component{

    private var body:Body;

    public function new(body:Body):Void 
    {

        this.body = body;
    }

    override public function get_type():ComponentType 
    {
         return Physics; 
    }

    override public function update(elapsed:Float):Void
    { 

        var sprite:Sprite = cast(parent.get_component(Sprite));

         if(sprite != null)
         {
             var s:FlxSprite = sprite.get_sprite();
             s.x = (body.position.x - s.width/2.0);
             s.y = (body.position.y - s.height/2.0);
             s.angle  = body.rotation * 57.295;
         }
    }

    public function get_body():Body 
    {
        return body;
    }

}