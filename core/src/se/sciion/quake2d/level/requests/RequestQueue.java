package se.sciion.quake2d.level.requests;

import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.enums.RequestType;

// Someone should deal with theses generic inference types :/
public class RequestQueue<T extends Request> {

	private Array<Subscriber<T>> subscribers;
	
	
	public RequestQueue() {
		subscribers = new Array<Subscriber<T>>();
	}
	
	public void subscribe(Subscriber<T> s){
		if(!subscribers.contains(s, true)){
			subscribers.add(s);
		}
	}
	
	public void unsubscribe(Subscriber<T> s){
		subscribers.removeValue(s,true);
	}
	
	/**
	 * Synchronously process request
	 * @param r
	 * @return
	 */
	public boolean send(T t){
		for(Subscriber<T> s: subscribers){
			if(s.getType() == t.getRequestType()){
				return s.process(t);
			}
		}
		return false;
	}
	
}
