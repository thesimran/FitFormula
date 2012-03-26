package cscece.android.fitformula;


import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
	private double vo2Max;
	private double engeryCost;
	private int weight;
	
	//Constant Pace Data
	public final String[] AGE_GROUPS = {"15-19","20-29","30-39","40-49","50-59","60-69"}; //more for a reference than anything
	public final int[] BEATS_MEN = {144,144,132,114,102,84};
	public final int[] BEATS_WOMEN ={120,114,114,102,84,84};
	public final double[] ENERGY_COST = {1.63,1.49,1.49,1.32,1.05,1.05};
	
	//Constant Test Phase Data
	public static final int NONE = 0;
	public static final int WARM_UP = 1;
	public static final int AFTER_WARM = 2;
	public static final int AFTER_SIX = 3;
	public static final int DONE = -1;
	
	//DB
	private DatabaseHelper db;
	private Cursor userCursor = null;
	private Cursor hrCursor = null;
	
	//ToneTasks
	ToneTask warmupTask;
	ToneTask afterWarmTask;
	
	//TextViews
	TextView instructionText;
	
	//Current Test Phase
	private static int currentPhase = NONE; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.fit_test_step2);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle);
		instructionText = (TextView)findViewById(R.id.step2_inst);
	
		
		
	}//end of onCreate
	
	@Override
	public void onResume(){
		
		//We will use the SoundManager to play the metronome tone
		//The SoundManager class uses the SoundPool class to play a selected tone
		mSoundManager = new SoundManager();
		mSoundManager.initSounds(getBaseContext());
		mSoundManager.addSound(1, R.raw.pace_tone);
	
		//set values for gender and age now
		//These values are extracted from the DB
		db= new DatabaseHelper(this);
		userCursor=db
                .getReadableDatabase()
                .rawQuery("SELECT _id, gender, age "+
                          "FROM " + DatabaseHelper.USER_TABLE_NAME,
                          null);
		hrCursor = db
					.getReadableDatabase()
					.rawQuery("SELECT _id, heartrate, date " + 
								"FROM " + DatabaseHelper.HR_TABLE_NAME,
								null);
		
		//should only be one row in the table
		userCursor.moveToFirst();
		gender = userCursor.getInt(1);
		age = userCursor.getInt(2);
		
		//hr should come from the last entry, meaning it was taken from the latest date
		hrCursor.moveToLast();
		restingHr = hrCursor.getInt(1);
		
		//Closing
		hrCursor.close();
		userCursor.close();
		db.close();
		
		pace = calcPace(age,gender);
		if(age > 59){
			//mad slow for the old people
			warmupPace = 66;
			
		}else{
			warmupPace = calcPace(age+10,gender);
		}
		
		interval = calcInterval(pace);
		warmupInterval = calcInterval(warmupPace);
		
		/*Testing
		testComplete(100);
		*/
		super.onResume();
	}
	
	@Override
	public void onPause(){
	
		try{
			if(warmupTask.getStatus() == AsyncTask.Status.RUNNING){
			
				warmupTask.cancel(true);
			}
		}catch(Exception e){
			
		}
		try{
			if(afterWarmTask.getStatus() == AsyncTask.Status.RUNNING){
				
				afterWarmTask.cancel(true);
			}
		}catch(Exception e){
			
		}
		db.close();
		super.onPause();
		
	}
	
	@Override
	public void onStop(){
	
		try{
			if(warmupTask.getStatus() == AsyncTask.Status.RUNNING){
				
				warmupTask.cancel(true);
			}
		}catch(Exception e){
			
		}	
		try{	
			if(afterWarmTask.getStatus() == AsyncTask.Status.RUNNING){
				
				afterWarmTask.cancel(true);
			}
		}catch(Exception e){
			
		}
		db.close();
		super.onStop();
	}
	
	@Override
	public void onDestroy(){
		
		try{
			if(warmupTask.getStatus() == AsyncTask.Status.RUNNING){
				
				warmupTask.cancel(true);
			}
		}catch(Exception e){
			
		}	
		try{
			if(afterWarmTask.getStatus() == AsyncTask.Status.RUNNING){
				
				afterWarmTask.cancel(true);
			}
		}catch(Exception e){
			
		}
		db.close();
		super.onDestroy();
		
	}
	
	public void phaseComplete(int hr){
		
		afterHr = hr;
		
		if(currentPhase == DONE){
			//Done the test!
			testComplete(afterHr);
			return;
		}
		
		
		
		
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
			
			testComplete(afterHr);
		}
		else{
			startNextPhase(currentPhase);
		}
		
	}
	
	public void testComplete(int hr){
	
		//get weight from db so we can calc Vo2Max
		db= new DatabaseHelper(this);
		userCursor=db
                .getReadableDatabase()
                .rawQuery("SELECT _id, weight "+
                          "FROM " + DatabaseHelper.USER_TABLE_NAME,
                          null);
		userCursor.moveToFirst();
		weight = userCursor.getInt(1);
		userCursor.close();
		
		Log.d("TEST", "" + weight);
		vo2Max = 42.5 + 16.6 * getEnergyCost() - .12 * weight - .12 * hr - .24 * age;
		
		//save this to the DB
		int heartRate = hr;
		long date = System.currentTimeMillis();
		//Save to DB
    	ContentValues values = new ContentValues();
		SQLiteDatabase dbSQL = db.getWritableDatabase();
    	values.put(DatabaseHelper.heartrate, heartRate);
    	values.put(DatabaseHelper.date, date);
    	dbSQL.insert(DatabaseHelper.HR_TABLE_NAME, null, values);
		values.clear();
		
		values.put(DatabaseHelper.vo2, vo2Max);
		dbSQL.insert(DatabaseHelper.USER_TABLE_NAME, null, values);
		values.clear();
		
		Toast
        .makeText(FitTestStep2.this, "Test Complete", Toast.LENGTH_LONG)
        .show();
		
		//Should bring us back to the FitnessTest Activity
		FitnessTest.testComplete = true;
		finish();
	}
	
	private double getEnergyCost(){
		
		return ENERGY_COST[beatIndex];
		
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
		
		instructionText.setText("Get Ready for the Next Phase...");
		instructionText.setTextSize(40);
		startTask(paceInterval);
		
	}
	//TODO: This shit does not work.
	public void startTask(Double[] paceI){
		
		Double[] paceInterval = paceI;
		//TODO: Give user 3 seconds to prepare...Check with Sam here, this may not be ok...
		long now = System.currentTimeMillis();
		while(System.currentTimeMillis() <= now + 3000){
			//wait
		}
		instructionText.setText("Step to the beat!");
		instructionText.setTextSize(50);
		afterWarmTask = (ToneTask) new ToneTask().execute(paceInterval);
		
		
	}
	
	
	//Listener for the 'Begin Warmup' button
	public void startWarmup(View view){
		
		Button startButton = (Button)findViewById(R.id.begin_warmup_button);
		startButton.setVisibility(Button.GONE);
		
		//TODO: Should add some sort of graphic or something....
		
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
				long begin = System.currentTimeMillis();
				
				//TODO: A better timing method will eventually be required.
				for(int i=0; i < length; i++){
	        	
				  //This should make it exactly 3 minutes	
				  if(System.currentTimeMillis() >= begin + 180000){
					  break;
				  }
					
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
	        	
	        	//Check to see if they are done the test first...
	        	if(currentPhase == AFTER_SIX){
	        		currentPhase = DONE;	        		
	        	}
	        	
	        	/* TODO: Now that we are done the Warmup, we have to take the pulse again.
	        	 * Since we have actually integrated the HR sensor into our app yet, we will supply
	        	 * a dummy HR for now
	        	 */
	        	int dummyHr = 80;
	        	/***************************************/
	        	
	        	phaseComplete(dummyHr);	
	        	
	        }
	        
	        protected void onCancelled(){
	        	
	        	Toast
                .makeText(FitTestStep2.this, "Step Test Cancelled!", Toast.LENGTH_LONG)
                .show();
	        	
	        }
	    	
	    }
	
}