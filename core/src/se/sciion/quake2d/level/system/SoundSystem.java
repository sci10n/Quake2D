package se.sciion.quake2d.level.system;

import java.util.HashMap;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.utils.Disposable;

public class SoundSystem implements Disposable {
	private HashMap<String, Sound> sounds;
	private HashMap<String, Music> musics;
    private boolean muted;
	private float volume;

	public SoundSystem(float volume) {
        this.muted = false;
		this.volume = volume;
		this.sounds = new HashMap<String, Sound>();
		this.musics = new HashMap<String, Music>();
	}

	public boolean addSound(String id, Sound sound) {
		if(!sounds.containsKey(id)) {
			sounds.put(id, sound);
		} else return false;

		return true;
	}

	public boolean addMusic(String id, Music music) {
		if(!musics.containsKey(id)) {
			musics.put(id, music);
		} else return false;

		return true;
	}

	public void stopSounds() {
		for (Sound sound : sounds.values())
			sound.stop();
	}

	public void stopMusic() {
		for (Music music : musics.values())
			music.stop();
	}

	public void stopAll() {
		stopSounds();
		stopMusic();
	}

    public void toggleMute() {
        stopAll();
        muted = !muted;
    }

	public void playSound(String id) {
        if (!muted)
            getSound(id).play(volume);
	}

	public void loopMusic(String id) {
        if (muted) return;
		getMusic(id).setLooping(true);
		getMusic(id).setVolume(volume);
		getMusic(id).play();
	}

	public void setVolume(float volume) {
		this.volume = volume;
		stopSounds(); // Can't set volume.
		for (Music music : musics.values())
			music.setVolume(volume);
	}

	public float getVolume() {
		return volume;
	}

	public Sound getSound(String id) {
		return sounds.get(id);
	}

	public Music getMusic(String id) {
		return musics.get(id);
	}

	public void removeSound(String id) {
		sounds.get(id).dispose();
		sounds.remove(id);
	}

	public void removeMusic(String id) {
		musics.get(id).dispose();
		musics.remove(id);
	}

	@Override
	public void dispose() {
		for (Sound sound : sounds.values())
			sound.dispose();
		for (Music music : musics.values())
			music.dispose();
	}
}
