package se.sciion.quake2d.level;

import se.sciion.quake2d.enums.RequestType;
import se.sciion.quake2d.level.events.Request;

public interface Subscriber<T extends Request> {
	
	public RequestType getType(); 
	public boolean process(T request);

}
