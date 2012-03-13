package cscece.android.fitformula;

import android.app.Activity;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;
import android.widget.MediaController;


public class FitTestStep2 extends Activity {

	//Audio
	AudioTrack mertoAudio;
	
	//biometrics needed to choose the test parameters
	private int age;
	private int gender;
	private int pace;
	private int warmupPace;
	
	//Constant Pace Data
	public final String[] AGE_GROUPS = {"15-19","20-29","30-39","40-49","50-59","60-69"};
	public final int[] BEATS_MEN = {144,144,132,114,102,84,156};
	public final int[] BEATS_WOMEN ={66,120,114,114,102,84,84,132};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fit_test_step2);
	
		//AudioTrack will be used to play the metronome sound
		mertoAudio = new AudioTrack(AudioManager.STREAM_MUSIC, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes, int mode);
		

	}//end of onCreate
	
	
	/*
	 * App will crash if we go 'Back' to the previous Activity.
	 * This is because the previous Activity was finished(), because it was a VideoView.
	 * There's no reason to have that huge video file sitting on the application stack.
	 */
	@Override
	public void onBackPressed(){
		//Do nothing
		return;
		
	}
	
}