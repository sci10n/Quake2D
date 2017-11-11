package se.sciion.quake2d.level.system;

import java.util.HashMap;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.assets.AssetManager;

public class SoundSystem {
	private static SoundSystem instance;
	private HashMap<String, Sound> sounds;
    private boolean muted;
	private float volume;
    private boolean setup;

	public SoundSystem(float volume) {
        this.setup = false;
        this.muted = false;
		this.volume = volume;
		this.sounds = new HashMap<String, Sound>();
	}

	public void setup(AssetManager assetManager, String[] soundPaths) {
		for (String soundPath : soundPaths) {
			String soundName = soundPath.split("/")[1].split("\\.")[0];
			addSound(soundName, assetManager.get(soundPath, Sound.class));
		}

        setup = true;
	}
	
	public static SoundSystem getInstance() {
		if (instance == null)
			instance = new SoundSystem(0.5f);
		return instance;
	}

	public boolean addSound(String id, Sound sound) {
        if (!setup) return false;
		if(!sounds.containsKey(id)) {
			sounds.put(id, sound);
		} else return false;

		return true;
	}

	public void stopSounds() {
		for (Sound sound : sounds.values())
			sound.stop();
	}

    public void toggleMute() {
        stopSounds();
        muted = !muted;
    }

	public void setMute(boolean muted) {
		if (muted) stopSounds();
		this.muted = muted;
	}

	public void playSound(String id) {
        if (!setup) return;
        if (!muted) getSound(id).play(volume);
	}

	public long loopSound(String id) {
        if (!setup) return -1;
		if (!muted)
			return getSound(id).loop(volume);
		return 0;
	}

	public void playSound(String id, Vector2 position, float speed) {
        if (!setup) return;
		if (!muted) {
			float panning = ((position.x / 30.0f) - 0.5f)*2.0f;
			long handle = getSound(id).play(volume);
			getSound(id).setPitch(handle, speed);
			getSound(id).setPan(handle, panning, volume);
		}
	}

	public long loopSound(String id, Vector2 position, float speed) {
        if (!setup) return -1;
		if (!muted) {
			float panning = ((position.x / 30.0f) - 0.5f)*2.0f;
			long handle = getSound(id).loop(volume);
			getSound(id).setPitch(handle, speed);
			getSound(id).setPan(handle, panning, volume);
		}

		return 0;
	}

	public void setVolume(float volume) {
		this.volume = volume;
		stopSounds();
	}

	public float getVolume() {
		return volume;
	}

	public Sound getSound(String id) {
		return sounds.get(id);
	}

	public void removeSound(String id) {
		sounds.get(id).dispose();
		sounds.remove(id);
	}
}
