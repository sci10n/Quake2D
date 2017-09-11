package se.sciion.quake2d.level.components;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.system.Pathfinding;

public class LineOfSightComponent extends EntityComponent{
	
	private Pathfinding pathfinder;
	
	public LineOfSightComponent(Pathfinding pathfinding) {
		this.pathfinder = pathfinding;
	}
	
	@Override
	public void render(RenderModel batch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tick(float delta) {
		
		PhysicsComponent physics = getParent().getComponent(ComponentTypes.Physics);
		if(physics != null)
			pathfinder.setPlayerPosition(physics.getBody().getPosition());
	}

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.LineOfSight;
	}

}
