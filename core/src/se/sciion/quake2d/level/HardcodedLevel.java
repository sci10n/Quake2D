package se.sciion.quake2d.level;

import com.badlogic.gdx.utils.Array;

/**
 * Difference to regular level is that this one can take an external array of game entities.
 * @author sciion
 *
 */
public class HardcodedLevel extends Level{

	public HardcodedLevel(Array<Entity> entities) {
		this.entities = entities;
	}

}
