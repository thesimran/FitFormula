package cscece.android.fitformula;

import cscece.android.fitformula.FitTestStep2.ToneTask;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class firstWorkout extends Activity {
	
	public final int durationMins = 20;
	//public final double[] DURATION_PERCENT = {0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8, 0.9,1};
	public final Long[] firstPhaseDuration = {(long) 480000}; //in milliseconds
	private TextView instructionText;
	private TextView timeRemaining;
	private WorkoutTimeTask firstPhase;
	
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
		firstPhase =(WorkoutTimeTask) new WorkoutTimeTask().execute(firstPhaseDuration);
		
	}
	
	public void finishedFirstPhase(int hr){
		//Gotta give the user an AlertDialog to give feedback on how they feel
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("How is the workout so far?")
		       .setCancelable(false)
		       .setPositiveButton("Easy", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                //Up it by 10%
		           }
		       })
		       .setNegativeButton("Challening", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                //Keep current intensity
		           }
		       })
		       .setNeutralButton("Too Hard", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                //Bring it down 10%
		           }
		       });
		AlertDialog alert = builder.create();
	}
	
	class WorkoutTimeTask extends AsyncTask<Long, Long, Void> {
	    	
	    	
			@Override
	        protected Void doInBackground(Long... millis ) {
				
				long millisLeft= millis[0];
				long rightNow = System.currentTimeMillis();
				Long[] mLeft =  new Long[1];
				
				while(millisLeft > 0){
					while(System.currentTimeMillis() < rightNow+1000){
						
					}
					millisLeft -= 1000;
					mLeft[0] = millisLeft;
					publishProgress(mLeft);
				}	
				return(null);
			}
			
			@SuppressWarnings("unchecked")
	        @Override
	        protected void onProgressUpdate(Long... m) {
	          
				long millLeft = m[0];
				//Convert milliseconds left to minutes and seconds left to show the user
				int secondsTotal = (int) millLeft / 1000;
				int seconds = secondsTotal % 60;
				int minutes = secondsTotal / 60;
				timeRemaining.setText(minutes + ":" + seconds);
				
	        	
	        }
	        
	        @Override
	        protected void onPostExecute(Void unused) {
	        	
	        	//Done phase!
	        	//TODO: Take HR again
	        	int heartRate = 80;
	        	/*************************/
	        	finishedFirstPhase(heartRate);
	
	        }
	}//end of WorkoutTimeTask		

}
