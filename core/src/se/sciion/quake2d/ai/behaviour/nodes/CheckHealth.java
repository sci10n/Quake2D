package se.sciion.quake2d.ai.behaviour.nodes;

import static guru.nidi.graphviz.model.Factory.node;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;

import org.omg.PortableInterceptor.SUCCESSFUL;

import com.badlogic.gdx.Gdx;

import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;
import se.sciion.quake2d.ai.behaviour.BehaviourNode;
import se.sciion.quake2d.ai.behaviour.BehaviourStatus;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.components.HealthComponent;

public class CheckHealth extends BehaviourNode {

    private HealthComponent health;
    private float ratio;

    private String tag;
    private Level level;
    
    public CheckHealth(String tag, Level level, float ratio){
    	this.tag = tag;
    	this.ratio = ratio;
    	this.level = level;
    }
    
    public CheckHealth(HealthComponent health, float ratio) {
        this.health = health;
        this.ratio = ratio;
    }

    @Override
    protected void onEnter() {
    	setStatus(BehaviourStatus.RUNNING);
    }

    @Override
    protected BehaviourStatus onUpdate() {

    	if(health != null) {
	        if(health != null && health.ratioHealth() > ratio) {
	        	setStatus(BehaviourStatus.SUCCESS);
	        }
	        else {
	        	setStatus(BehaviourStatus.FAILURE);
	        }
    	}
    	else {
    		Entity e;
    		float nearest = Float.MAX_VALUE;
    		for(Entity e: level.getEntities(tag)){
    			if(e == )
    		}
    	}
        return getStatus();
    }

    @Override
    public Node toDotNode() {
        return node("checkHealth" + getNext())
               .with(Shape.ELLIPSE)
				.with(Style.FILLED, Color.rgb(getColor()).fill(), Color.BLACK.radial())
               .with(Label.of("Health > " + ratio));
    }
}
