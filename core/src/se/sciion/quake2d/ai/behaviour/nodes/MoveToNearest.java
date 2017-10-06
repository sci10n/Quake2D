package se.sciion.quake2d.ai.behaviour.nodes;

import static guru.nidi.graphviz.model.Factory.node;

import com.badlogic.gdx.math.Vector2;

import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;
import se.sciion.quake2d.ai.behaviour.BehaviourNode;
import se.sciion.quake2d.ai.behaviour.BehaviourStatus;
import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.components.BotInputComponent;
import se.sciion.quake2d.level.components.PhysicsComponent;
import se.sciion.quake2d.level.system.Pathfinding;
import se.sciion.quake2d.level.system.PhysicsSystem;

public class MoveToNearest extends BehaviourNode {
		private static int moveToNearestId = 0;
		
		private String id;
		private BotInputComponent input;
		private float minDistance;
		private PhysicsSystem physics;
		private Level level;
		private Pathfinding pathfinding;
		
		public MoveToNearest(String id, Level level, Pathfinding pathfinding, PhysicsSystem physics, BotInputComponent input, float minDistance) {
			super();
			this.id = id;
			this.level = level;
			this.input = input;
			this.minDistance = minDistance;
			this.physics = physics;
			this.pathfinding = pathfinding;
		}

		@Override
		protected void onEnter() {
			status = BehaviourStatus.RUNNING;
			super.onEnter();

		}

		@Override
		protected BehaviourStatus onUpdate() {
			
			PhysicsComponent physics = input.getParent().getComponent(ComponentTypes.Physics);
			if (physics == null) {
				status = BehaviourStatus.FAILURE;
				return status;
			}
			
			Vector2 fromLoc = physics.getBody().getPosition();
			int bestPath = Integer.MAX_VALUE;
			
			Vector2 targetPos = null;
			for(Entity e: level.getEntities(id)) {
				PhysicsComponent ePhysics = e.getComponent(ComponentTypes.Physics);
				if(ePhysics != null) {
					Vector2 ePos = ePhysics.getBody().getPosition();
					int pathLength = pathfinding.findPath(fromLoc, ePos).size;
					if(pathLength < bestPath) {
						bestPath = pathLength;
						targetPos = ePos;
					}
				}
			}
			
			if(targetPos == null) {
				status = BehaviourStatus.SUCCESS;
				return status;
			}
						
			input.setTarget(targetPos);

			float distance = fromLoc.cpy().sub(targetPos).len();

			if (distance > minDistance) {
				status = BehaviourStatus.RUNNING;
			} else if (distance <= minDistance && this.physics.lineOfSight(fromLoc, targetPos)) {
				status = BehaviourStatus.SUCCESS;
				input.setTarget(null);

			}

			return status;
		}
		
	@Override
	public Node toDot() {
		Node node = node("MoveToNearest" + moveToNearestId++).with(Label.of("Move To Nearest " + id));
		
		return node;
	}

}
