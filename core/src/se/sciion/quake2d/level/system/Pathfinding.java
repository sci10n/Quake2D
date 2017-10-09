package se.sciion.quake2d.level.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.components.PhysicsComponent;
import se.sciion.quake2d.level.items.Item;

public class Pathfinding {

	private final int WIDTH;
	private final int HEIGHT;
	
	private Vector2 grid[][];
	
	private PhysicsSystem physics;
	
	private Level level;
	
	public Pathfinding(int width, int height, Level level) {
		WIDTH = width;
		HEIGHT = height;
		grid = new Vector2[WIDTH][HEIGHT];
		this.level = level;
	}
	
	public void render(RenderModel model) {
		model.primitiveRenderer.begin(ShapeType.Line);
		for(int x = 0; x < WIDTH; x++){
			for(int y = 0; y < HEIGHT; y++){
				if(grid[x][y] != null){
					model.primitiveRenderer.setColor(Color.DARK_GRAY);
					
					Array<Entity> entities = level.getEntities("player");
					float vs[] = new float[entities.size];
					for(int i = 0; i < entities.size; i++){
						Entity e = entities.get(i);
						PhysicsComponent c = e.getComponent(ComponentTypes.Physics);
						if(c != null) {
							Vector2 start = c.getBody().getPosition();
							vs[i] = enemyLineOfSight(grid[x][y], start, e);
							if(physics.lineOfSight(start,grid[x][y])){
							}
						}
								
					}
					model.primitiveRenderer.setColor(new Color(vs[0] / net.dermetfan.utils.math.MathUtils.max(vs), 0.0f, vs[1] / net.dermetfan.utils.math.MathUtils.max(vs),1.0f));
					
					for(Vector2 neighbor: neighbors(grid[x][y])){
						model.primitiveRenderer.line(grid[x][y], neighbor);
					}
				}
			}
		}
		model.primitiveRenderer.end();
	}
	

	
	private ArrayList<Vector2> neighbors(Vector2 v){
		int x = (int) v.x;
		int y = (int) v.y;
		ArrayList<Vector2> n = new ArrayList<Vector2>();
		try{
			// Diagonals cannot be trusted
//			if (x > 0 && y > 0 && grid[x - 1][y - 1] != null){
//				n.add(grid[x - 1][y - 1]);
//			}
//			if (x < WIDTH-1 && y > 0 && grid[x + 1][y - 1] != null){
//				n.add(grid[x + 1][y - 1]);
//			}
//			if (x < WIDTH-1 && y < HEIGHT-1 && grid[x + 1][y + 1] != null){
//				n.add(grid[x + 1][y + 1]);
//			}
//			if (x > 0 && y < HEIGHT-1 && grid[x - 1][y + 1] != null){
//				n.add(grid[x - 1][y + 1]);
//			}
			
			// Manhattan
			if (x > 0 && grid[x - 1][y] != null){
				n.add(grid[x - 1][y]);
			}
			if (x < WIDTH-1 && grid[x + 1][y] != null){
				n.add(grid[x + 1][y]);
			}
			if (y > 0 && grid[x][y - 1] != null){
				n.add(grid[x][y - 1]);
			}
			if (y < HEIGHT-1 && grid[x][y + 1] != null){
				n.add(grid[x][y + 1]);
			}
		}catch(IndexOutOfBoundsException e){
			
		}
		return n;
	}
	
	
	// Returns the last node in the list which has direct line of sight from start node
	private int smooth(Array<Vector2> path, int startIndex){
		for(int i = Math.max(path.size-1,0); i > startIndex; i--){
			if(physics.lineOfSight(path.get(startIndex), path.get(i))){
				return i;
			}
		}
		return startIndex;
	}
	
	private Array<Vector2> reconstruct(HashMap<Vector2,Vector2> cameFrom, Vector2 start) {
		Array<Vector2> path = new Array<Vector2>();
		path.add(start);
		Vector2 current = start;
		while(true){
			current = cameFrom.get(current);
			if(current == null)
				break;
				path.add(current);
		}
		
		// Remove redundant nodes
//		Array<Vector2> smoothedPath = new Array<Vector2>(path.size);
//		smoothedPath.add(path.first());
//		for(int i = 0; i < path.size; i++){
//			int nextIndex = smooth(path, i);
//			smoothedPath.add(path.get(nextIndex));
//			i = nextIndex;
//		}
		return path;
	}
	
	
	// Get path from start to target using a* and euclidean distance as heuristic.
	public Array<Vector2> findPath(Vector2 from, Vector2 to, Entity requestor){
		
		Vector2 start = null;
		Vector2 target = null;
		float nearestStart = Float.MAX_VALUE;
		float nearestEnd = Float.MAX_VALUE;
		for(int x = 0; x < WIDTH; x++){
			for(int y = 0; y < HEIGHT; y++){
				if(grid[x][y] != null){
					float d1 = Vector2.dst2(from.x, from.y, grid[x][y].x, grid[x][y].y);
					float d2 = Vector2.dst2(to.x, to.y, grid[x][y].x, grid[x][y].y);
					
					if(d1 < nearestStart){
						start = grid[x][y];
						nearestStart = d1;
					}
					
					if(d2 < nearestEnd){
						target = grid[x][y];
						nearestEnd = d2;
					}
				}
			}
		}
		HashMap<Vector2,Vector2> cameFrom = new HashMap<Vector2, Vector2>();
		HashMap<Vector2,Float> gScore = new HashMap<Vector2, Float>();
		HashMap<Vector2,Float> fScore = new HashMap<Vector2, Float>();
		
		PriorityQueue<Vector2> closedSet =  new PriorityQueue<Vector2>((Vector2 v1, Vector2 v2) -> (int) Math.signum((gScore.get(v1) - gScore.get(v2))));
		PriorityQueue<Vector2> openSet = new PriorityQueue<Vector2>((Vector2 v1, Vector2 v2) -> (int) Math.signum((gScore.get(v1) - gScore.get(v2))));
		
		openSet.add(start);
		for(int x = 0; x < WIDTH; x++){
			for(int y = 0; y < HEIGHT; y++){
				if(grid[x][y] == null)
					continue;
				gScore.put(grid[x][y], 999999.0f);
				fScore.put(grid[x][y], 999999.0f);
			}
		}
		
		gScore.put(start, 0.0f);
		fScore.put(start,start.cpy().sub(target).len2());
		
		while(!openSet.isEmpty()){
			Vector2 c = openSet.peek();
			if(c.cpy().sub(target).len2() < 0.1f){
				return reconstruct(cameFrom,c);
			}
			
			openSet.remove(c);
			closedSet.add(c);
			for(Vector2 n: neighbors(c)){
				if(closedSet.contains(n))
					continue;
				if(!openSet.contains(n))
					openSet.add(n);
				
				// Should not be concerned with player position. Should be moved to separate function.
				float extraScore = 1 + enemyLineOfSight(n,from,requestor);
				
				float tentative_gScore = gScore.get(c) + c.cpy().sub(n).len2() + extraScore;
				
				if(tentative_gScore >= gScore.get(n))
					continue;

				cameFrom.put(n, c);
				gScore.put(n, tentative_gScore);
				fScore.put(n, gScore.get(n) + n.cpy().sub(target).len2() + extraScore);
			}
		}
		
		return new Array<Vector2>();

	}
	
	private float enemyLineOfSight(Vector2 target,Vector2 start, Entity requestor) {
		float total = 0;
		
		Array<Entity> enemies = level.getEntities("player");
		for(int i = 0; i < enemies.size; i++){
			Entity e = enemies.get(i);
			if(e == requestor){
				continue;
			}
			
			PhysicsComponent p = e.getComponent(ComponentTypes.Physics);
			if(p == null)
				continue;
			
			Vector2 enemyPos = p.getBody().getPosition();
			
			if(Vector2.dst(enemyPos.x, enemyPos.y, target.x,target.y) < 0.001f){
				continue;
			}
			
			if(physics.lineOfSight(enemyPos,target)){
				total += 4;
			}
		}
		return total;
	}
	
	// Does not check for disjoint graphs
	public boolean reachable(Vector2 p){
		try{
			if(grid[(int) p.x][(int) p.y] != null){
				return true;
			}
		}catch(IndexOutOfBoundsException e){
		}
		return false;
	}
	
	// Update graph. Should only be called after static objects have been removed/added/modified
	public void update(PhysicsSystem physics){
		this.physics = physics;
		for(int x = 0; x < WIDTH; x++){
			for(int y = 0; y < HEIGHT; y++){
				if(physics.containsSolidObject(x + 0.5f,y + 0.5f,0.5f,0.5f)){
					grid[x][y] = null;
				}
				else {
					grid[x][y] = new Vector2(x + 0.5f, y + 0.5f);
				}
			}
		}
	}
	
}
