package se.sciion.quake2d.level.system;

import se.sciion.quake2d.level.components.HealthComponent;

public interface HealthListener {
	public void onStatusChanged(HealthComponent healthComponent);
}