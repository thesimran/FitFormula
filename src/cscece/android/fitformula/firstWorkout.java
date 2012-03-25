package cscece.android.fitformula;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class firstWorkout extends Activity {
	
	//constant workout data
	public final int durationMins = 20;
	public final double[] DURATION_PERCENT = {0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8, 0.9,1};
	public final Integer[] firstPhaseDuration = {480000}; //in milliseconds
	private TextView instructionText;
	private TextView timeRemaining;
	
	@Override
	public void onCreate(Bundle icicle){
		super.onCreate(icicle); 
		setContentView(R.layout.first_workout);
		instructionText =(TextView)findViewById(R.id.first_workout_inst);
		timeRemaining =(TextView)findViewById(R.id.time_remaining);
	}
	
	//Listener for the 'Begin Warmup' button
	public void startWorkout(View view){
		//TODO: First, take heart rate, hard-coded for now
		int heartRate = 65;
		/***********************/
		//TODO: We will show a dynamically updating chartView here
		instructionText.setText("Run/Bike at 50-55% of your max.");
		
	}
	
	//This task is in charge of playing the tone at a given interval
	class WorkoutTimeTask extends AsyncTask<Integer, Void, Void> {
	    	
	    	
			@Override
	        protected Void doInBackground(Integer...millis ) {
				
				int milliDur = millis[0];
				
				return(null);
			}
	}//end of WorkoutTimeTask		

}
