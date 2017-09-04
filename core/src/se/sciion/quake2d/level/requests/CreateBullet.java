package se.sciion.quake2d.level.requests;

import com.badlogic.gdx.math.Vector2;

import se.sciion.quake2d.enums.RequestType;

public class CreateBullet extends Request{

	private Vector2 origin;
	private Vector2 direction;
	private float speed;
	private int damage;
	
	public CreateBullet(Vector2 origin, Vector2 direction,  float speed, int damage) {
		this.origin = origin;
		this.direction = direction;
		this.speed = speed;
		this.damage = damage;
	}
	
	public Vector2 getOrigin() {
		return origin;
	}
	public Vector2 getDirection() {
		return direction;
	}
	public float getSpeed() {
		return speed;
	}
	public int getDamage() {
		return damage;
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.CreateBullet;
	}
	

}
