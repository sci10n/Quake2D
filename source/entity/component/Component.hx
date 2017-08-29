package entity.component;

class Component 
{

    private var parent:Entity;

    public function set_parent(?parent:Entity):Void 
    {
        this.parent = parent;
    }

    public function get_parent():Entity 
    {
       return parent;
    } 

    // Work around since abstract classes does not exist in haXe.
   public function get_type():ComponentType { return None; }
   public function update(elapsed:Float):Void { }
}