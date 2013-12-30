package com.example.biitbox;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FullscreenActivity extends Activity {
	
	private SoundService soundService;
	
	public void play1(View view){
		switch (view.getId()) {
			case R.id.button1:
				play(1);
				break;
			case R.id.button2:
				play(2);
				break;
			case R.id.button3:
				play(3);
				break;
		}
	}

	private void play(Integer sound) {
		if(soundService.isReady()){
			soundService.playSample(sound);
		}
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        soundService = new SoundService();
        soundService.loadSounds();
		       
        setContentView(R.layout.activity_fullscreen);
        
        disableButtonSounds();
    }

	private void disableButtonSounds() {
		this.findViewById(R.id.button1).setSoundEffectsEnabled(false);
        this.findViewById(R.id.button2).setSoundEffectsEnabled(false);
        this.findViewById(R.id.button3).setSoundEffectsEnabled(false);
	}

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}
