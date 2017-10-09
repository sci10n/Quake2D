package se.sciion.quake2d.ai.behaviour.nodes;

import static guru.nidi.graphviz.model.Factory.node;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;
import se.sciion.quake2d.ai.behaviour.BehaviourNode;
import se.sciion.quake2d.ai.behaviour.BehaviourStatus;
import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.components.InventoryComponent;
import se.sciion.quake2d.level.components.PhysicsComponent;
import se.sciion.quake2d.level.system.Pathfinding;
import se.sciion.quake2d.level.system.PhysicsSystem;

public class PickUpItem extends BehaviourNode {

    private String id;
    private Level level;
    private Pathfinding pathfinding;
    private BotInputComponent input;

    public PickUpItem(String id, Level level, Pathfinding pathfinding, BotInputComponent input) {
        this.id = id;
        this.level = level;
        this.pathfinding = pathfinding;
        this.input = input;
    }

    @Override
    protected void onEnter() {
    	setStatus(BehaviourStatus.RUNNING);
    }

    @Override
    protected BehaviourStatus onUpdate() {

        InventoryComponent inventory = input.getParent().getComponent(ComponentTypes.Inventory);
        if(inventory == null){
        	setStatus(BehaviourStatus.SUCCESS);
            return getStatus();
        }

        if(inventory.containsItem(id)){
        	setStatus(BehaviourStatus.SUCCESS);
            return getStatus();
        }

        PhysicsComponent physics = input.getParent().getComponent(ComponentTypes.Physics);
        if(physics == null) {
        	setStatus(BehaviourStatus.FAILURE);
            return getStatus();
        }

        Vector2 fromPos = physics.getBody().getPosition();
        Vector2 targetPos = null;
        int bestPath = Integer.MAX_VALUE;


        for(Entity e: level.getEntities(id)){
        	PhysicsComponent p = e.getComponent(ComponentTypes.Physics);
            if(p != null){
                Vector2 ePos = p.getBody().getPosition();
                int pathLength = pathfinding.findPath(fromPos, ePos).size;

                if(pathLength < bestPath){
                    bestPath = pathLength;
                    targetPos = ePos;
                }
            }
        }

        if(targetPos == null) {
        	setStatus(BehaviourStatus.FAILURE);
            return getStatus();
        }
        
        input.setTarget(targetPos);

        return getStatus();
    }

    @Override
    public Node toDotNode() {
        return node("pickUpItem" + getNext())
               .with(Shape.RECTANGLE)
			   .with(Style.FILLED, Color.rgb(getColor()).fill(), Color.BLACK.radial())
               .with(Label.of("Pick up " + id));
    }

}
