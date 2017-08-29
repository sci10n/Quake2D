package entity;

import entity.component.Component;
import entity.component.ComponentType;

class Entity 
{

    private var components:Array<Component>;

    public function new () 
    {
        components = new Array<Component>();
    }

    public function update(elapsed:Float):Void 
    {
        for(c in components) 
        {
            c.update(elapsed);
        }
    }

    public function add_component(c:Component):Void
    {
        if(components.indexOf(c) == -1) 
        {
            c.set_parent(this);
            components.push(c);
        }
    }

    public function remove_component(t:ComponentType):Void 
    {
        for(i in 0...components.length)
        {
            if(components[i].get_type() == t)
            {
                components.remove(components[i]);
                break;
            } 
        }
    }

    public function get_component(t:ComponentType):Component
    {
        for(i in 0...components.length)
        {
            if(components[i].get_type() == t)
            {
                return components[i];
            }
        }
        return null;
    }
}