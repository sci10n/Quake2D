package se.sciion.quake2d.level.requests;

import com.badlogic.gdx.physics.box2d.Body;

import se.sciion.quake2d.enums.RequestType;

public class DestroyBody extends Request {

	private Body bodyRef;

	public DestroyBody(Body body) {
		this.bodyRef = body;
	}

	public Body getBodyRef() {
		return bodyRef;
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.DestroyBody;
	}

}
