package se.sciion.quake2d.level.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.components.PhysicsComponent;
import se.sciion.quake2d.level.items.Item;

public class Pathfinding {

	private final int WIDTH;
	private final int HEIGHT;
	
	private Vector2 grid[][];
	
	private PhysicsSystem physics;
	private Vector2 playerPosition;
	
	// Items of interest for pathfinders
	private HashMap<Entity,Vector2> entities;
	
	public Pathfinding(int width, int height) {
		WIDTH = width;
		HEIGHT = height;
		grid = new Vector2[WIDTH][HEIGHT];
		entities = new HashMap<Entity,Vector2>();
	}
	
	public void render(RenderModel model) { 
		model.primitiveRenderer.begin(ShapeType.Line);
		for(int x = 0; x < WIDTH; x++){
			for(int y = 0; y < HEIGHT; y++){
				if(grid[x][y] != null){
					model.primitiveRenderer.setColor(Color.DARK_GRAY);
					if(physics.lineOfSight(playerPosition,grid[x][y])){
						model.primitiveRenderer.setColor(Color.FOREST);
					}
					for(Vector2 neighbor: neighbors(grid[x][y])){
						model.primitiveRenderer.line(grid[x][y], neighbor);
					}
				}
			}
		}
		model.primitiveRenderer.end();

	}
	
	public void setPlayerPosition(Vector2 playerPosition){
		this.playerPosition = playerPosition;	
	}
	
	private ArrayList<Vector2> neighbors(Vector2 v){
		int x = (int) v.x;
		int y = (int) v.y;
		ArrayList<Vector2> n = new ArrayList<Vector2>();
		try{
			// Diagonals
			if (x > 0 && y > 0 && grid[x - 1][y - 1] != null){
				n.add(grid[x - 1][y - 1]);
			}
			if (x < WIDTH-1 && y > 0 && grid[x + 1][y - 1] != null){
				n.add(grid[x + 1][y - 1]);
			}
			if (x < WIDTH-1 && y < HEIGHT-1 && grid[x + 1][y + 1] != null){
				n.add(grid[x + 1][y + 1]);
			}
			if (x > 0 && y < HEIGHT-1 && grid[x - 1][y + 1] != null){
				n.add(grid[x - 1][y + 1]);
			}
			
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
	public Array<Vector2> findPath(Vector2 start, Vector2 target){

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
			if(c.cpy().sub(target).len2() < 1.0f){
				return reconstruct(cameFrom,c);
			}
			
			openSet.remove(c);
			closedSet.add(c);
			for(Vector2 n: neighbors(c)){
				if(closedSet.contains(n))
					continue;
				if(!openSet.contains(n))
					openSet.add(n);
				
				// Should not be concerned with player position. Should be moved to sepparate function.
				float extraScore = 1;
				if(physics.lineOfSight(playerPosition,n)){
					extraScore += 4;
				}
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
	
	
	public void addEntity(Entity e, Vector2 origin){
		entities.put(e, origin);
	}
	
	// Get Vector2 location of item.
	public Vector2 getEntityLocation(Entity e){
		if(entities.containsKey(e)){
			return entities.get(e);
		}
		return null;
	}
	
	public void removeEntity(Entity e){
		if(entities.containsKey(e)){
			entities.remove(e);
		}
		
	}
	public Vector2 playerPosition(){
		return playerPosition;
	}
	
	// Update graph. Should only be called after static objects have been removed/added/modified
	public void update(PhysicsSystem physics){
		this.physics = physics;
		for(int x = 0; x < WIDTH; x++){
			for(int y = 0; y < HEIGHT; y++){
				if(physics.containsSolidObject(x,y,0.25f,0.25f)){
					grid[x][y] = null;
				}
				else {
					grid[x][y] = new Vector2(x, y);
				}
			}
		}
	}

	// Update all positions
	public void tick() {
		for(Entity e: entities.keySet()) {
			PhysicsComponent physics = e.getComponent(ComponentTypes.Physics);
			if(physics != null) {
				entities.put(e, physics.getBody().getPosition());
			}
		}
	}

	
}