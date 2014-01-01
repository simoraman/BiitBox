package com.example.biitbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Environment;

import com.musicg.wave.Wave;

public class SoundService {
	SoundPool soundPool = null;
	List<Integer> sounds = new ArrayList<Integer>();
	List<Integer> ints = new ArrayList<Integer>();
	private AudioRecord mRecorder;
	private boolean recording;
	private Thread recordingThread;
	int bufferSize = AudioRecord.getMinBufferSize(44100,
			AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

	public void loadSounds() {
		soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		File root = new File(Environment.getExternalStorageDirectory(), "");
		for (int i = 0; i < 3; i++) {
			int index = i + 1;
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
		soundPool.play(sounds.get(sound - 1), 1.0f, 1.0f, 0, 0, 1.0f);
	}

	public void startRecording() {
		recording = true;
		mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
				3584);
		mRecorder.startRecording();
		recordingThread = new Thread(new Runnable() {

			@Override
			public void run() {
				writeAudioDataToFile();
			}
		}, "AudioRecorder Thread");

		recordingThread.start();
		System.out.println("start recording;");
	}

	public void stopRecording() {
		if (recording) {
			recording = false;
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
			recordingThread = null;

			 Wave wave = new
			 Wave(Environment.getExternalStorageDirectory()+"/sound1.wav");
			 short[] amps = wave.getSampleAmplitudes();
			 short high=0;
			 short low=0;
			 
			 for(short amp : amps){
				 if(amp<low) low=amp;
				 if(amp>high) high = amp;
				 
			 }
			 System.out.println("high: "+high+" low:"+low);
			soundPool.release();
			soundPool = null;
			
			System.out.println("Stop recording");
			copyWaveFile(getTempFilename(), getFilename());
			deleteTempFile();
			
			loadSounds();
		}

	}

	private String getFilename() {
		return Environment.getExternalStorageDirectory() + "/sound1.wav";
	}

	private void writeAudioDataToFile() {

		byte data[] = new byte[bufferSize];
		String filename = getTempFilename();
		FileOutputStream os = null;

		try {
			os = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int read = 0;

		if (null != os) {
			while (recording) {
				read = mRecorder.read(data, 0, bufferSize);

				if (AudioRecord.ERROR_INVALID_OPERATION != read) {
					try {
						os.write(data);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void deleteTempFile() {
		File file = new File(getTempFilename());

		file.delete();
	}

	private String getTempFilename() {
		return Environment.getExternalStorageDirectory() + "/temp.wav";
	}

	private void copyWaveFile(String inFilename, String outFilename) {
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = 44100;
		int channels = 1;
		long byteRate = 16 * 44100 * channels / 8;

		byte[] data = new byte[bufferSize];

		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;

			WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
					longSampleRate, channels, byteRate);

			while (in.read(data) != -1) {
				out.write(data);
			}

			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
			long totalDataLen, long longSampleRate, int channels, long byteRate)
			throws IOException {

		byte[] header = new byte[44];

		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8); // block align
		header[33] = 0;
		header[34] = 16; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		out.write(header, 0, 44);
	}

}
