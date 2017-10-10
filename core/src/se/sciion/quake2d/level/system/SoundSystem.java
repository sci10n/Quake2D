package se.sciion.quake2d.level.system;

import java.util.HashMap;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.assets.AssetManager;

public class SoundSystem {
	private static SoundSystem instance;
	private HashMap<String, Sound> sounds;
    private boolean muted;
	private float volume;

	public SoundSystem(float volume) {
        this.muted = false;
		this.volume = volume;
		this.sounds = new HashMap<String, Sound>();
	}

	public void setup(AssetManager assetManager, String[] soundPaths) {
		for (String soundPath : soundPaths) {
			String soundName = soundPath.split("/")[1].split("\\.")[0];
			addSound(soundName, assetManager.get(soundPath, Sound.class));
		}
	}
	
	public static SoundSystem getInstance() {
		if (instance == null)
			instance = new SoundSystem(0.5f);
		return instance;
	}

	public boolean addSound(String id, Sound sound) {
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

	public void playSound(String id) {
        if (!muted) getSound(id).play(volume);
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
