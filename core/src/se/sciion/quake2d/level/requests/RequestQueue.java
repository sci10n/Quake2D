package se.sciion.quake2d.level;

import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.enums.RequestType;
import se.sciion.quake2d.level.events.Request;

public class RequestQueue {

	private Array<Subscriber> subscribers;
	
	
	public RequestQueue() {
		subscribers = new Array<Subscriber>();
	}
	
	public void subscribe(Subscriber s){
		if(!subscribers.contains(s, true)){
			subscribers.add(s);
		}
	}
	
	public void unsubscribe(Subscriber s){
		subscribers.removeValue(s,true);
	}
	
	/**
	 * Synchronously process request
	 * @param r
	 * @return
	 */
	public boolean send(Request r){
		for(Subscriber s: subscribers){
			if(s.getType() == r.getRequestType() && s.process(r)){
				return true;
			}
		}
		return false;
	}
	
}
