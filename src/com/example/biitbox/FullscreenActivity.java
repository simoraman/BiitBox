package com.example.biitbox;

import java.io.File;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

public class FullscreenActivity extends Activity {
	boolean loaded = false;
	SoundPool soundPool = null;
	int sound = 0;
	public void play1(View view){
		if(loaded){
			soundPool.play(sound, 1.0f, 1.0f, 0, 0, 1.0f);
		}
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File root = new File(Environment.getExternalStorageDirectory(), "");
		 File gpxfile = new File(root, "sound1.wav");
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
           @Override
           public void onLoadComplete(SoundPool soundPool, int sampleId,
                   int status) {
               loaded = true;
           }
       });
		sound = soundPool.load(gpxfile.getPath(), 1);
		       
        setContentView(R.layout.activity_fullscreen);
        
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}
