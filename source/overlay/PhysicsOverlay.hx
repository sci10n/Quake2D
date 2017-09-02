package overlay;

import entity.component.Physics;
import nape.geom.Vec2;
import nape.space.Space;
import nape.phys.Body;
import nape.phys.BodyType;
import nape.shape.Polygon;
import nape.space.Space;
import nape.phys.Material;

class PhysicsOverlay implements Overlay<Physics> {

    private var space:Space;

    public function new ():Void {
        var gravity = Vec2.weak(0,0);
        space = new Space(gravity);
    }

    public function update(elapsed:Float):Void {
        space.step(elapsed);
    }

    public function render():Void {

    }

    public function addComponent(component:Physics):Void {
        component.get_body().space = space;
    }

    public function create_body(x:Float,y:Float,w:Float,h:Float, type:BodyType):Body {
        var body:Body = new Body(type);
        var material:Material = new Material(0,0,0,1,0);
        body.shapes.add(new Polygon(Polygon.box(w,h),material));
        body.position.setxy(x,y);
        body.allowRotation = false;
        body.space = space;
        return body;
    }

    public function get_space():Space {
        return space;
    }
}