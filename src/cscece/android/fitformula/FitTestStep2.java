package cscece.android.fitformula;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



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
	private int restingHr; //This value will be extracted from the DB
	private int afterHr;
	private int beatIndex;
	
	//Constant Pace Data
	public final String[] AGE_GROUPS = {"15-19","20-29","30-39","40-49","50-59","60-69"}; //more for a reference than anything
	public final int[] BEATS_MEN = {144,144,132,114,102,84};
	public final int[] BEATS_WOMEN ={120,114,114,102,84,84};
	
	//Constant Test Phase Data
	public static final int NONE = 0;
	public static final int WARM_UP = 1;
	public static final int AFTER_WARM = 2;
	public static final int AFTER_SIX = 3;
	public static final int DONE = 4;
	
	//ToneTasks
	ToneTask warmupTask;
	ToneTask afterWarmTask;
	
	//Current Test Phase
	private static int currentPhase = NONE; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fit_test_step2);

		
		//We will use the SoundManager to play the metronome tone
		//The SoundManager class uses the SoundPool class to play a selected tone
		mSoundManager = new SoundManager();
		mSoundManager.initSounds(getBaseContext());
		mSoundManager.addSound(1, R.raw.pace_tone);
		//That should do it, to play the sound, you just call SoundManager.playSound(1);
		//mSoundManager.playSound(1);
		
		//set values for gender and age now
		//TODO: These values will actually be extracted from the DB here
		gender = FitnessTest.MALE;
		age = 24;
		restingHr = 65; 
		
		pace = calcPace(age,gender);
		warmupPace = calcPace(age+10,gender);
		interval = calcInterval(pace);
		warmupInterval = calcInterval(warmupPace);
		
		
	}//end of onCreate
	
	@Override
	public void onPause(){
	
		if(warmupTask.getStatus() == AsyncTask.Status.RUNNING){
		
			warmupTask.cancel(true);
		}
		super.onPause();
		
	}
	
	@Override
	public void onStop(){
	
		if(warmupTask.getStatus() == AsyncTask.Status.RUNNING){
			
			warmupTask.cancel(true);
		}
		super.onStop();
	}
	
	@Override
	public void onDestroy(){
		
		if(warmupTask.getStatus() == AsyncTask.Status.RUNNING){
			
			warmupTask.cancel(true);
		}
		super.onDestroy();
		
	}
	
	public void phaseComplete(int hr){
	
		afterHr = hr;
		
		boolean isDone = false;
		int diff = afterHr - restingHr;
	
		//Lets check to see if the new HR is too high	
		if(beatIndex == 0){
			if(diff > 30){
				isDone = true;
			}
		}
		if(beatIndex == 1){
			if(diff > 29){
				isDone = true;
			}
		}
		if(beatIndex == 2){
			if(diff > 28){
				isDone = true;
			}
		}
		if(beatIndex == 3){
			if(diff > 26){
				isDone = true;
			}
		}
		if(beatIndex == 4){
			if(diff > 25){
				isDone = true;
			}
		}
		if(beatIndex == 5){
			if(diff > 24){
				isDone = true;
			}
		}
		
		if(isDone){
			
			testComplete();
		}
		else{
			startNextPhase(currentPhase);
		}
		
	}
	
	public void testComplete(){
		//TODO
	}
	
	//Listener for the 'Cancel Test' button
	public void cancelTest(View view){
		
		finishAndStartOver();
		
	}
	
	public void finishAndStartOver(){
		
		//This will bring us back to the initial FitnessTest Activity
    	finish();
		
	}
	
	public void startNextPhase(int curPhase){
		
		//Increments to the next phase
		currentPhase = curPhase + 1;
		
		double dPace = (double) pace;
		
		Double[] paceInterval = {dPace,interval};
		
		//TODO: Should add some sort of graphic or something....
		TextView instructionText = (TextView)findViewById(R.id.step2_inst);
		instructionText.setText("Step to the beat!");
		instructionText.setTextSize(50);
		
		afterWarmTask = (ToneTask) new ToneTask().execute(paceInterval);
	}
	
	//Listener for the 'Begin Warmup' button
	public void startWarmup(View view){
		
		Button startButton = (Button)findViewById(R.id.begin_warmup_button);
		startButton.setVisibility(Button.GONE);
		
		//TODO: Should add some sort of graphic or something....
		TextView instructionText = (TextView)findViewById(R.id.step2_inst);
		instructionText.setText("Step to the beat!");
		instructionText.setTextSize(50);
		
		
		double dWarmupPace = (double) warmupPace;
		Double[] paceInterval = {dWarmupPace,warmupInterval};
		
		currentPhase = WARM_UP;
		//Play
		warmupTask = (ToneTask) new ToneTask().execute(paceInterval);
		
		
	}
	
	
	public int calcPace(int a, int gen){
		
		int theAge = a;
		int theGender = gen;
		int thePace = 0;
		
		if(theAge >= 15 && theAge <= 19){
			beatIndex = 0;
		}else if(theAge >= 20 && theAge <= 29){
			beatIndex = 1;
		}else if(theAge >= 30 && theAge <= 39){
			beatIndex = 2;
		}else if(theAge >= 40 && theAge <= 49){
			beatIndex = 3;
		}else if(theAge >= 50 && theAge <= 59){
			beatIndex = 4;
		}else{//over 59
			beatIndex = 5;
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
		finishAndStartOver();
		
	}
	
     //This task is in charge of playing the tone at a given interval
	 class ToneTask extends AsyncTask<Double, Boolean, Void> {
	    	
	    	
			@Override
	        protected Void doInBackground(Double... pI) {
				double pace = pI[0];
				double interval = pI[1];
				double milliInterval = interval * 1000;
				int intervalInt = (int) milliInterval;
				int length = (int)pace * 3;//3 minutes
				Boolean each = true;
				
	          for(int i=0; i < length; i++){
	        	  
	        	  if(isCancelled() == true){
	        		  break;
	        	  }
	        	  SystemClock.sleep(intervalInt); //milliseconds
	        	  publishProgress(each);
	          }
	          
	          return(null);
	        }
	        
	        @SuppressWarnings("unchecked")
	        @Override
	        protected void onProgressUpdate(Boolean... progress) {
	          
	        	//Actually play the sound here (in UI thread)
	        	mSoundManager.playSound(1);
	        	
	        }
	        
	        @Override
	        protected void onPostExecute(Void unused) {
	        	
	        	/* TODO: Now that we are done the Warmup, we have to take the pulse again.
	        	 * Since we have actually integrated the HR sensor into our app yet, we will supply
	        	 * a dummy HR for now
	        	 */
	        	int dummyHr = 120;	
	        	
	        	phaseComplete(dummyHr);
	        	
	        }
	        
	        protected void onCancelled(){
	        	
	        	Toast
                .makeText(FitTestStep2.this, "ToneTask Cancelled!", Toast.LENGTH_LONG)
                .show();
	        	
	        }
	    	
	    }
	
}