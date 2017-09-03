package se.sciion.quake2d.level.requests;

import se.sciion.quake2d.enums.RequestType;

public interface Subscriber<T extends Request> {
	
	public RequestType getType(); 
	public boolean process(T t);

}