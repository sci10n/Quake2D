package overlay;

@:generic
interface Overlay<T> {
    
    public function update(elapsed:Float):Void;
    public function render():Void;
    
    public function addComponent(t:T):Void;
}