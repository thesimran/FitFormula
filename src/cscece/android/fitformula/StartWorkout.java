package cscece.android.fitformula;

import java.text.DateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartWorkout extends Activity {

	private TextView workoutDesc;
	private TextView workoutAvail;
	private Button startWorkoutButton;
	
	private int program;
	private int level;
	private boolean aerobic;
	private long date;
	private long eventID;
	private String programName;
	private boolean intro;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
		setContentView(R.layout.start_workout_layout);

		program = getIntent().getExtras().getInt("program");
		level = getIntent().getExtras().getInt("level");
		aerobic= getIntent().getExtras().getBoolean("aerobic");
		date = getIntent().getExtras().getLong("date");
		eventID = getIntent().getExtras().getLong("eventID");		
		programName = getIntent().getExtras().getString("programname");
		intro = getIntent().getExtras().getBoolean("intro");
		
		workoutAvail=(TextView)findViewById(R.id.workout_availability);
		startWorkoutButton=(Button)findViewById(R.id.start_workout_button);
		// get the current date
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
            	
        c.setTimeInMillis(date);
        Log.d("date","year"+mYear+" month"+mMonth+" day"+mDay);
    	Log.d("date","year"+c.get(Calendar.YEAR)+" month"+c.get(Calendar.MONTH)+" day"+c.get(Calendar.DAY_OF_MONTH));
    	//Allow user to do workout only if scheduled for current day
		if (mYear==c.get(Calendar.YEAR) && mMonth==c.get(Calendar.MONTH) && mDay==c.get(Calendar.DAY_OF_MONTH)){
			startWorkoutButton.setEnabled(true);		
			workoutAvail.setText(Html.fromHtml("<br><i><b>Start this workout today!</b></i>"));
		}else{
			startWorkoutButton.setEnabled(false);
			workoutAvail.setText(Html.fromHtml("<br><i>Please wait until the scheduled date to complete this workout.</i>"));
		}
        
		workoutDesc=(TextView)findViewById(R.id.start_workout_desc);
		String dateString = (DateFormat.getDateInstance(DateFormat.LONG).format(date));
		if(intro){
			workoutDesc.setText(Html.fromHtml("<big>"+dateString+"</big><br><b><i><u>"+programName+" Program "+program
					+" Level "+level+"</u></i></b><br>Intro Session"));
		}else if(aerobic){
			workoutDesc.setText(Html.fromHtml("<big>"+dateString+"</big><br><b><i><u>"+programName+" Program "+program
				+" Level "+level+"</u></i></b><br>Aerobic Session"));	    	
		}else{
			workoutDesc.setText(Html.fromHtml("<big>"+dateString+"</big><br><b><i><u>"+programName+" Program "+program
					+" Level "+level+"</u></i></b><br>Interval Session"));	    	
		}		
		
	}
	
	public void startWorkout (View theButton){
	    // TODO: launch correct workout
		Intent i = new Intent(this, FirstWorkout.class);
		//Intent i = new Intent(this, WorkoutTest2.class);
    	startActivity(i);
	}

	public void cancelWorkout (View theButton){
		finish(); 
	}


}
