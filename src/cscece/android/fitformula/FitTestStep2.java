package cscece.android.fitformula;

import android.app.Activity;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.VideoView;
import android.widget.MediaController;


public class FitTestStep2 extends Activity {

	//Audio
	//AudioTrack mertoAudio;
	SoundManager mSoundManager;
	
	//biometrics needed to choose the test parameters
	private int age;
	private int gender;
	private int pace;
	private int warmupPace;
	private double interval;
	private double warmupInterval;
	
	//Constant Pace Data
	public final String[] AGE_GROUPS = {"15-19","20-29","30-39","40-49","50-59","60-69"}; //more for a reference than anything
	public final int[] BEATS_MEN = {144,144,132,114,102,84,156};
	public final int[] BEATS_WOMEN ={66,120,114,114,102,84,84,132};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fit_test_step2);
	
		//AudioTrack will be used to play the metronome sound
		//mertoAudio = new AudioTrack(AudioManager.STREAM_MUSIC, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes, int mode);
		
		//We will use the SoundManager to play the metronome tone
		//The SoundManager class uses the SoundPool class to play a selected tone
		mSoundManager = new SoundManager();
		mSoundManager.initSounds(getBaseContext());
		mSoundManager.addSound(1, R.raw.pace_tone);
		//That should do it, to play the sound, you just call SoundManager.playSound(1);
		//mSoundManager.playSound(1);
		
		//set values for gender and age now
		//these values will actually be extracted from the DB here!
		gender = FitnessTest.MALE;
		age = 24;
		pace = calcPace(age,gender);
		warmupPace = calcPace(age+10,gender);
		interval = calcInterval(pace);
		warmupInterval = calcInterval(warmupPace);
		
		//Play!
		playSound(warmupInterval);
		
	}//end of onCreate
	

	//TODO: Use AsyncTask to time the tone here!
	
	public int calcPace(int a, int gen){
		
		int theAge = a;
		int theGender = gen;
		int beatIndex = -1;
		int thePace = 0;
		
		if(theAge >= 15 && theAge <= 19){
			beatIndex = 0;
		}else if(theAge >= 20 && theAge <= 29){
			beatIndex = 1;
		}else if(theAge >= 30 && theAge <= 39){
			beatIndex = 2;
		}else if(theAge >= 30 && theAge <= 39){
			beatIndex = 3;
		}else if(theAge >= 40 && theAge <= 49){
			beatIndex = 4;
		}else if(theAge >= 50 && theAge <= 59){
			beatIndex = 5;
		}else{//over 59
			beatIndex = 6;
		}
		
		//now look-up in the array
		if(theGender == FitnessTest.MALE){
			thePace = BEATS_MEN[beatIndex];
		}else{
			thePace = BEATS_WOMEN[beatIndex];
		}
		
		return thePace;
		
		
	}
	
	public double calcInterval(int p){
		
		int thePace = p;
		double bPerSec = thePace / 60.0;
		double inter = 0;
		
		//now lets get the interval
		inter  = 1 / bPerSec;
		return inter;
	}
	
	
	
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