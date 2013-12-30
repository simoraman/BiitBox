package com.example.biitbox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Environment;

public class SoundService {
	SoundPool soundPool = null;
	List<Integer> sounds = new ArrayList<Integer>();
	List<Integer> ints = new ArrayList<Integer>();
	public void loadSounds() {
		soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		File root = new File(Environment.getExternalStorageDirectory(), "");
		for(int i = 0; i < 3; i++){
			int index = i+1;
			File sound1 = new File(root, "sound" + index + ".wav");
			String path = sound1.getPath();
			int soundId = soundPool.load(path, 1);
			sounds.add(soundId);
		}
		
		
		
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
           @Override
           public void onLoadComplete(SoundPool soundPool, int sampleId,
                   int status) {
               ints.add(sampleId);
           }
       });
		
	}
	public boolean isReady() {
		return (ints.size() >= 3);
	}
	public void playSample(Integer sound) {
		soundPool.play(sounds.get(sound-1), 1.0f, 1.0f, 0, 0, 1.0f);		
	}

}
