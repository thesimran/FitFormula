package cscece.android.fitformula;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MyWorkout extends Activity {
	ImageView myImage;
	Button myButton;
	TextView myText;
	public static Context context; 
	
	public static final String PREFS_NAME = "MyPrefs";	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		context = this;
		setContentView(R.layout.my_workout_layout);
		//For Spiral 4 demo
		/*myImage= (ImageView) findViewById(R.id.running_man);
		myButton= (Button) findViewById(R.id.get_workout);
		myText= (TextView) findViewById(R.id.workout_text);		*/		
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		boolean savedWorkouts = settings.getBoolean("savedNewWorkouts", false);
		

		if (!savedWorkouts) { // if workouts haven't been saved to database yet
			Log.d("db","saving new workouts");
			initiateWorkoutsInDB();

			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("savedNewWorkouts", true);
			editor.commit();
		}

	}// end of onCreate
	
	public static Context getContext(){
		return context;
	}

	//For Spiral 4 demo
	/*@Override
    protected void onResume() {
        super.onResume();
     Log.d("db","onStart");

     SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
     	boolean gottenWorkout = settings.getBoolean("gottenWorkout", false);
     
     	if(gottenWorkout==true){
     	myImage.setImageResource(R.drawable.calendar);
 		myText.setVisibility(View.INVISIBLE);
 		//myButton.setVisibility(View.INVISIBLE);
     	}
     	else{
     		myImage.setImageResource(R.drawable.logo_transparent);
     		myText.setVisibility(View.VISIBLE);
     	}
    }	*/
	
	// Method called when the "Get Workout" button is Pushed
	public void getWorkoutPushed(View view) {
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    	SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("gottenWorkout", false);
		editor.commit();

		//Switch Tabs
		FitFormula ParentActivity;
		ParentActivity = (FitFormula) this.getParent();
		ParentActivity.switchTab(1);

	}// end of getWorkoutPushed

	public void initiateWorkoutsInDB() {
		ContentValues values = new ContentValues();
		String data = "";
		DatabaseHelper dbh;
		dbh = new DatabaseHelper(this);
		SQLiteDatabase db = dbh.getWritableDatabase();

		// Frequency		
		db.delete(DatabaseHelper.FREQUENCY_TABLE_NAME, null, null);
		// Program 1 - Level 1
		values.put(DatabaseHelper.program, 1);
		values.put(DatabaseHelper.level, 1);
		values.put(DatabaseHelper.numinterval, 3);
		values.put(DatabaseHelper.numaerobic, 1);
		values.put(DatabaseHelper.numcycles, 2);
		values.put(DatabaseHelper.rest, 2);
		values.put(DatabaseHelper.restalternate, false);		
		db.insert(DatabaseHelper.FREQUENCY_TABLE_NAME, null, values);
		// Program 1 - Level 2
		values.put(DatabaseHelper.program, 1);
		values.put(DatabaseHelper.level, 2);
		values.put(DatabaseHelper.numinterval, 1);
		values.put(DatabaseHelper.numaerobic, 1);
		values.put(DatabaseHelper.numcycles, 4);
		values.put(DatabaseHelper.rest, 2);
		values.put(DatabaseHelper.restalternate, false);
		db.insert(DatabaseHelper.FREQUENCY_TABLE_NAME, null, values);
		// Program 1 - Level 3
		values.put(DatabaseHelper.program, 1);
		values.put(DatabaseHelper.level, 3);
		values.put(DatabaseHelper.numinterval, 1);
		values.put(DatabaseHelper.numaerobic, 3);
		values.put(DatabaseHelper.numcycles, 3);
		values.put(DatabaseHelper.rest, 2);
		values.put(DatabaseHelper.restalternate, true);
		db.insert(DatabaseHelper.FREQUENCY_TABLE_NAME, null, values);
		// Program 1 - Level 4
		values.put(DatabaseHelper.program, 1);
		values.put(DatabaseHelper.level, 4);
		values.put(DatabaseHelper.numinterval, 1);
		values.put(DatabaseHelper.numaerobic, 2);
		values.put(DatabaseHelper.numcycles, 3);
		values.put(DatabaseHelper.rest, 2);
		values.put(DatabaseHelper.restalternate, true);
		db.insert(DatabaseHelper.FREQUENCY_TABLE_NAME, null, values);
		// Program 1 - Level 5
		values.put(DatabaseHelper.program, 1);
		values.put(DatabaseHelper.level, 5);
		values.put(DatabaseHelper.numinterval, 1);
		values.put(DatabaseHelper.numaerobic, 1);
		values.put(DatabaseHelper.numcycles, 5);
		values.put(DatabaseHelper.rest, 1);
		values.put(DatabaseHelper.restalternate, false);
		db.insert(DatabaseHelper.FREQUENCY_TABLE_NAME, null, values);
		// Program 2 - Level 1
		values.put(DatabaseHelper.program, 2);
		values.put(DatabaseHelper.level, 1);
		values.put(DatabaseHelper.numinterval, 1);
		values.put(DatabaseHelper.numaerobic, 0);
		values.put(DatabaseHelper.numcycles, 7);
		values.put(DatabaseHelper.rest, 2);
		values.put(DatabaseHelper.restalternate, false);
		db.insert(DatabaseHelper.FREQUENCY_TABLE_NAME, null, values);
		// Program 2 - Level 2
		values.put(DatabaseHelper.program, 2);
		values.put(DatabaseHelper.level, 2);
		values.put(DatabaseHelper.numinterval, 1);
		values.put(DatabaseHelper.numaerobic, 1);
		values.put(DatabaseHelper.numcycles, 4);
		values.put(DatabaseHelper.rest, 2);
		values.put(DatabaseHelper.restalternate, false);
		db.insert(DatabaseHelper.FREQUENCY_TABLE_NAME, null, values);
		// Program 2 - Level 3
		values.put(DatabaseHelper.program, 2);
		values.put(DatabaseHelper.level, 3);
		values.put(DatabaseHelper.numinterval, 2);
		values.put(DatabaseHelper.numaerobic, 1);
		values.put(DatabaseHelper.numcycles, 3);
		values.put(DatabaseHelper.rest, 2);
		values.put(DatabaseHelper.restalternate, true);
		db.insert(DatabaseHelper.FREQUENCY_TABLE_NAME, null, values);
		// Program 2 - Level 4
		values.put(DatabaseHelper.program, 2);
		values.put(DatabaseHelper.level, 4);
		values.put(DatabaseHelper.numinterval, 1);
		values.put(DatabaseHelper.numaerobic, 3);
		values.put(DatabaseHelper.numcycles, 3);
		values.put(DatabaseHelper.rest, 2);
		values.put(DatabaseHelper.restalternate, true);
		db.insert(DatabaseHelper.FREQUENCY_TABLE_NAME, null, values);
		// Program 2 - Level 5
		values.put(DatabaseHelper.program, 2);
		values.put(DatabaseHelper.level, 5);
		values.put(DatabaseHelper.numinterval, 1);
		values.put(DatabaseHelper.numaerobic, 4);
		values.put(DatabaseHelper.numcycles, 2);
		values.put(DatabaseHelper.rest, 1);
		values.put(DatabaseHelper.restalternate, false);
		db.insert(DatabaseHelper.FREQUENCY_TABLE_NAME, null, values);
		// Program 3 - Level 1
		values.put(DatabaseHelper.program, 3);
		values.put(DatabaseHelper.level, 1);
		values.put(DatabaseHelper.numinterval, 1);
		values.put(DatabaseHelper.numaerobic, 0);
		values.put(DatabaseHelper.numcycles, 7);
		values.put(DatabaseHelper.rest, 2);
		values.put(DatabaseHelper.restalternate, false);
		db.insert(DatabaseHelper.FREQUENCY_TABLE_NAME, null, values);
		// Program 3 - Level 2
		values.put(DatabaseHelper.program, 3);
		values.put(DatabaseHelper.level, 2);
		values.put(DatabaseHelper.numinterval, 2);
		values.put(DatabaseHelper.numaerobic, 1);
		values.put(DatabaseHelper.numcycles, 3);
		values.put(DatabaseHelper.rest, 2);
		values.put(DatabaseHelper.restalternate, false);
		db.insert(DatabaseHelper.FREQUENCY_TABLE_NAME, null, values);
		// Program 3 - Level 3
		values.put(DatabaseHelper.program, 3);
		values.put(DatabaseHelper.level, 3);
		values.put(DatabaseHelper.numinterval, 1);
		values.put(DatabaseHelper.numaerobic, 2);
		values.put(DatabaseHelper.numcycles, 3);
		values.put(DatabaseHelper.rest, 2);
		values.put(DatabaseHelper.restalternate, true);
		db.insert(DatabaseHelper.FREQUENCY_TABLE_NAME, null, values);
		// Program 3 - Level 4
		values.put(DatabaseHelper.program, 3);
		values.put(DatabaseHelper.level, 4);
		values.put(DatabaseHelper.numinterval, 1);
		values.put(DatabaseHelper.numaerobic, 1);
		values.put(DatabaseHelper.numcycles, 5);
		values.put(DatabaseHelper.rest, 1);
		values.put(DatabaseHelper.restalternate, false);
		db.insert(DatabaseHelper.FREQUENCY_TABLE_NAME, null, values);
		// Program 3 - Level 5
		values.put(DatabaseHelper.program, 3);
		values.put(DatabaseHelper.level, 5);
		values.put(DatabaseHelper.numinterval, 2);
		values.put(DatabaseHelper.numaerobic, 1);
		values.put(DatabaseHelper.numcycles, 4);
		values.put(DatabaseHelper.rest, 1);
		values.put(DatabaseHelper.restalternate, false);
		db.insert(DatabaseHelper.FREQUENCY_TABLE_NAME, null, values);
		values.clear();
				
		// Warmup
		db.delete(DatabaseHelper.WARMUP_TABLE_NAME, null, null);
		data = "0.40,0.40,0.50,0.50,0.50,0.50,0.50,0.55,0.55,0.55,0.55";
		values.put(DatabaseHelper.intensitylow, data);
		values.put(DatabaseHelper.intensityhigh, data);
		data = "0.0,0.10,0.20,0.30,0.40,0.50,0.60,0.70,0.80,0.90,1.00";
		values.put(DatabaseHelper.progresspercentage, data);
		values.put(DatabaseHelper.duration, 8);
		db.insert(DatabaseHelper.WARMUP_TABLE_NAME, null, values);
		values.clear();
		
		// Cooldown
		db.delete(DatabaseHelper.COOLDOWN_TABLE_NAME, null, null);
		data = "0.55,0.55,0.55,0.55,0.50,0.50,0.50,0.40,0.40,0.40,0.40";
		values.put(DatabaseHelper.intensitylow, data);
		values.put(DatabaseHelper.intensityhigh, data);
		data = "0.0,0.10,0.20,0.30,0.40,0.50,0.60,0.70,0.80,0.90,1.00";
		values.put(DatabaseHelper.progresspercentage, data);
		values.put(DatabaseHelper.duration, 8);
		db.insert(DatabaseHelper.COOLDOWN_TABLE_NAME, null, values);
		values.clear();
		
		// Program 1
		db.delete(DatabaseHelper.PROGRAM1_TABLE_NAME, null, null);
		// Program 1 - Level 1, Aerobic
		values.put(DatabaseHelper.level, 1);
		data = "0.55";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.60";
		values.put(DatabaseHelper.intensityhigh, data);
		values.put(DatabaseHelper.progresspercentage, 0);
		values.put(DatabaseHelper.duration, 20);
		values.put(DatabaseHelper.aerobic, true);
		db.insert(DatabaseHelper.PROGRAM1_TABLE_NAME, null, values);
		// Program 1 - Level 1, Interval
		values.put(DatabaseHelper.level, 1);
		data = "0.50,0.50,0.55,0.55,0.55,0.45,0.45,0.55,0.55,0.55,0.55";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.55,0.55,0.60,0.60,0.60,0.55,0.55,0.60,0.60,0.60,0.60";
		values.put(DatabaseHelper.intensityhigh, data);
		data = "0.0,0.10,0.20,0.30,0.40,0.50,0.60,0.70,0.80,0.90,1.00";
		values.put(DatabaseHelper.progresspercentage, data);
		values.put(DatabaseHelper.duration, 25);
		values.put(DatabaseHelper.aerobic, false);
		db.insert(DatabaseHelper.PROGRAM1_TABLE_NAME, null, values);
		//Program 1 - Level 2, Aerobic
		values.put(DatabaseHelper.level, 2);
		data = "0.60";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.65";
		values.put(DatabaseHelper.intensityhigh, data);
		values.put(DatabaseHelper.progresspercentage, 0);
		values.put(DatabaseHelper.duration, 25);
		values.put(DatabaseHelper.aerobic, true);
		db.insert(DatabaseHelper.PROGRAM1_TABLE_NAME, null, values);
		// Program 1 - Level 2, Interval
		values.put(DatabaseHelper.level, 2);
		data = "0.60,0.60,0.60,0.60,0.60,0.50,0.50,0.60,0.60,0.60,0.60";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.65,0.65,0.65,0.65,0.65,0.55,0.55,0.65,0.65,0.65,0.65";
		values.put(DatabaseHelper.intensityhigh, data);
		data = "0.0,0.10,0.20,0.30,0.40,0.50,0.60,0.70,0.80,0.90,1.00";
		values.put(DatabaseHelper.progresspercentage, data);
		values.put(DatabaseHelper.duration, 30);
		values.put(DatabaseHelper.aerobic, false);
		db.insert(DatabaseHelper.PROGRAM1_TABLE_NAME, null, values);
		//Program 1 - Level 3, Aerobic
		values.put(DatabaseHelper.level, 3);
		data = "0.60";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.65";
		values.put(DatabaseHelper.intensityhigh, data);
		values.put(DatabaseHelper.progresspercentage, 0);
		values.put(DatabaseHelper.duration, 30);
		values.put(DatabaseHelper.aerobic, true);
		db.insert(DatabaseHelper.PROGRAM1_TABLE_NAME, null, values);
		// Program 1 - Level 3, Interval
		values.put(DatabaseHelper.level, 3);
		data = "0.70,0.60,0.70,0.60,0.70,0.60,0.70,0.60,0.70,0.60,0.70";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.75,0.65,0.75,0.65,0.75,0.65,0.75,0.65,0.75,0.65,0.75";
		values.put(DatabaseHelper.intensityhigh, data);
		data = "0.0,0.10,0.20,0.30,0.40,0.50,0.60,0.70,0.80,0.90,1.00";
		values.put(DatabaseHelper.progresspercentage, data);
		values.put(DatabaseHelper.duration, 35);
		values.put(DatabaseHelper.aerobic, false);
		db.insert(DatabaseHelper.PROGRAM1_TABLE_NAME, null, values);
		//Program 1 - Level 4, Aerobic
		values.put(DatabaseHelper.level, 4);
		data = "0.65";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.70";
		values.put(DatabaseHelper.intensityhigh, data);
		values.put(DatabaseHelper.progresspercentage, 0);
		values.put(DatabaseHelper.duration, 40);
		values.put(DatabaseHelper.aerobic, true);
		db.insert(DatabaseHelper.PROGRAM1_TABLE_NAME, null, values);
		// Program 1 - Level 4, Interval
		values.put(DatabaseHelper.level, 4);
		data = "0.70,0.65,0.70,0.65,0.70,0.65,0.70,0.65,0.70,0.65,0.70";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.75,0.70,0.75,0.70,0.75,0.70,0.75,0.70,0.75,0.70,0.75";
		values.put(DatabaseHelper.intensityhigh, data);
		data = "0.0,0.10,0.20,0.30,0.40,0.50,0.60,0.70,0.80,0.90,1.00";
		values.put(DatabaseHelper.progresspercentage, data);
		values.put(DatabaseHelper.duration, 40);
		values.put(DatabaseHelper.aerobic, false);
		db.insert(DatabaseHelper.PROGRAM1_TABLE_NAME, null, values);
		//Program 1 - Level 5, Aerobic
		values.put(DatabaseHelper.level, 5);
		data = "0.70";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.75";
		values.put(DatabaseHelper.intensityhigh, data);
		values.put(DatabaseHelper.progresspercentage, 0);
		values.put(DatabaseHelper.duration, 45);
		values.put(DatabaseHelper.aerobic, true);
		db.insert(DatabaseHelper.PROGRAM1_TABLE_NAME, null, values);
		// Program 1 - Level 5, Interval
		values.put(DatabaseHelper.level, 5);
		data = "0.80,0.70,0.80,0.70,0.80,0.70,0.80,0.70,0.80,0.70,0.80";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.85,0.75,0.85,0.75,0.85,0.75,0.85,0.75,0.85,0.75,0.85";
		values.put(DatabaseHelper.intensityhigh, data);
		data = "0.0,0.10,0.20,0.30,0.40,0.50,0.60,0.70,0.80,0.90,1.00";
		values.put(DatabaseHelper.progresspercentage, data);
		values.put(DatabaseHelper.duration, 40);
		values.put(DatabaseHelper.aerobic, false);
		db.insert(DatabaseHelper.PROGRAM1_TABLE_NAME, null, values);
		values.clear();
		
		// Program 2
		db.delete(DatabaseHelper.PROGRAM2_TABLE_NAME, null, null);
		//Program 2 - Level 1, Aerobic -- N/A
		//Program 2 - Level 1, Interval
		values.put(DatabaseHelper.level, 1);
		data = "0.50,0.50,0.50,0.40,0.40,0.50,0.50,0.50,0.50,0.40,0.40";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.55,0.55,0.55,0.55,0.55,0.55,0.55,0.55,0.55,0.55,0.55";
		values.put(DatabaseHelper.intensityhigh, data);
		data = "0.0,0.10,0.20,0.30,0.40,0.50,0.60,0.70,0.80,0.90,1.00";
		values.put(DatabaseHelper.progresspercentage, data);
		values.put(DatabaseHelper.duration, 20);
		values.put(DatabaseHelper.aerobic, false);
		db.insert(DatabaseHelper.PROGRAM2_TABLE_NAME, null, values);
		//Program 2 - Level 2, Aerobic
		values.put(DatabaseHelper.level, 2);
		data = "0.55";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.60";
		values.put(DatabaseHelper.intensityhigh, data);
		values.put(DatabaseHelper.progresspercentage, 0);
		values.put(DatabaseHelper.duration, 25);
		values.put(DatabaseHelper.aerobic, true);
		db.insert(DatabaseHelper.PROGRAM2_TABLE_NAME, null, values);
		//Program 2 - Level 2, Interval
		values.put(DatabaseHelper.level, 2);
		data = "0.60,0.60,0.60,0.40,0.40,0.60,0.60,0.60,0.60,0.40,0.40";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.65,0.65,0.65,0.55,0.55,0.65,0.65,0.65,0.65,0.55,0.55";
		values.put(DatabaseHelper.intensityhigh, data);
		data = "0.0,0.10,0.20,0.30,0.40,0.50,0.60,0.70,0.80,0.90,1.00";
		values.put(DatabaseHelper.progresspercentage, data);
		values.put(DatabaseHelper.duration, 25);
		values.put(DatabaseHelper.aerobic, false);
		db.insert(DatabaseHelper.PROGRAM2_TABLE_NAME, null, values);
		//Program 2 - Level 3, Aerobic
		values.put(DatabaseHelper.level, 3);
		data = "0.60";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.65";
		values.put(DatabaseHelper.intensityhigh, data);
		values.put(DatabaseHelper.progresspercentage, 0);
		values.put(DatabaseHelper.duration, 30);
		values.put(DatabaseHelper.aerobic, true);
		db.insert(DatabaseHelper.PROGRAM2_TABLE_NAME, null, values);
		//Program 2 - Level 3, Interval
		values.put(DatabaseHelper.level, 3);
		data = "0.65,0.65,0.65,0.65,0.55,0.55,0.65,0.65,0.65,0.65,0.55";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.70,0.70,0.70,0.70,0.60,0.60,0.70,0.70,0.70,0.70,0.60";
		values.put(DatabaseHelper.intensityhigh, data);
		data = "0.0,0.10,0.20,0.30,0.40,0.50,0.60,0.70,0.80,0.90,1.00";
		values.put(DatabaseHelper.progresspercentage, data);
		values.put(DatabaseHelper.duration, 30);
		values.put(DatabaseHelper.aerobic, false);
		db.insert(DatabaseHelper.PROGRAM2_TABLE_NAME, null, values);
		//Program 2 - Level 4, Aerobic
		values.put(DatabaseHelper.level, 4);
		data = "0.65";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.70";
		values.put(DatabaseHelper.intensityhigh, data);
		values.put(DatabaseHelper.progresspercentage, 0);
		values.put(DatabaseHelper.duration, 35);
		values.put(DatabaseHelper.aerobic, true);
		db.insert(DatabaseHelper.PROGRAM2_TABLE_NAME, null, values);
		//Program 2 - Level 4, Interval
		values.put(DatabaseHelper.level, 4);
		data = "0.70,0.70,0.70,0.70,0.55,0.55,0.70,0.70,0.70,0.70,0.70";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.75,0.75,0.75,0.75,0.60,0.60,0.75,0.75,0.75,0.75,0.75";
		values.put(DatabaseHelper.intensityhigh, data);
		data = "0.0,0.10,0.20,0.30,0.40,0.50,0.60,0.70,0.80,0.90,1.00";
		values.put(DatabaseHelper.progresspercentage, data);
		values.put(DatabaseHelper.duration, 35);
		values.put(DatabaseHelper.aerobic, false);
		db.insert(DatabaseHelper.PROGRAM2_TABLE_NAME, null, values);
		//Program 2 - Level 5, Aerobic
		values.put(DatabaseHelper.level, 5);
		data = "0.70";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.75";
		values.put(DatabaseHelper.intensityhigh, data);
		values.put(DatabaseHelper.progresspercentage, 0);
		values.put(DatabaseHelper.duration, 40);
		values.put(DatabaseHelper.aerobic, true);
		db.insert(DatabaseHelper.PROGRAM2_TABLE_NAME, null, values);
		//Program 2 - Level 5, Interval
		values.put(DatabaseHelper.level, 5);
		data = "0.75,0.75,0.75,0.75,0.60,0.60,0.75,0.75,0.75,0.75,0.75";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.80,0.80,0.80,0.80,0.65,0.65,0.80,0.80,0.80,0.80,0.80";
		values.put(DatabaseHelper.intensityhigh, data);
		data = "0.0,0.10,0.20,0.30,0.40,0.50,0.60,0.70,0.80,0.90,1.00";
		values.put(DatabaseHelper.progresspercentage, data);
		values.put(DatabaseHelper.duration, 40);
		values.put(DatabaseHelper.aerobic, false);
		db.insert(DatabaseHelper.PROGRAM2_TABLE_NAME, null, values);
		values.clear();
		
		// Program 3
		db.delete(DatabaseHelper.PROGRAM3_TABLE_NAME, null, null);
		//Program 3 - Level 1, Aerobic -- N/A
		//Program 3 - Level 1, Interval
		values.put(DatabaseHelper.level, 1);
		data = "0.55,0.55,0.55,0.40,0.40,0.55,0.55,0.55,0.40,0.40,0.55";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.60,0.60,0.60,0.55,0.55,0.60,0.60,0.60,0.55,0.55,0.60";
		values.put(DatabaseHelper.intensityhigh, data);
		data = "0.0,0.10,0.20,0.30,0.40,0.50,0.60,0.70,0.80,0.90,1.00";
		values.put(DatabaseHelper.progresspercentage, data);
		values.put(DatabaseHelper.duration, 25);
		values.put(DatabaseHelper.aerobic, false);
		db.insert(DatabaseHelper.PROGRAM3_TABLE_NAME, null, values);
		//Program 3 - Level 2, Aerobic
		values.put(DatabaseHelper.level, 2);
		data = "0.70";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.75";
		values.put(DatabaseHelper.intensityhigh, data);
		values.put(DatabaseHelper.progresspercentage, 0);
		values.put(DatabaseHelper.duration, 25);
		values.put(DatabaseHelper.aerobic, true);
		db.insert(DatabaseHelper.PROGRAM3_TABLE_NAME, null, values);
		//Program 3 - Level 2, Interval
		values.put(DatabaseHelper.level, 2);
		data = "0.70,0.70,0.60,0.40,0.55,0.55,0.55,0.40,0.60,0.60,0.60";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.75,0.75,0.65,0.55,0.60,0.60,0.60,0.55,0.65,0.65,0.65";
		values.put(DatabaseHelper.intensityhigh, data);
		data = "0.0,0.10,0.20,0.30,0.40,0.50,0.60,0.70,0.80,0.90,1.00";
		values.put(DatabaseHelper.progresspercentage, data);
		values.put(DatabaseHelper.duration, 25);
		values.put(DatabaseHelper.aerobic, false);
		db.insert(DatabaseHelper.PROGRAM3_TABLE_NAME, null, values);
		//Program 3 - Level 3, Aerobic
		values.put(DatabaseHelper.level, 3);
		data = "0.70";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.75";
		values.put(DatabaseHelper.intensityhigh, data);
		values.put(DatabaseHelper.progresspercentage, 0);
		values.put(DatabaseHelper.duration, 30);
		values.put(DatabaseHelper.aerobic, true);
		db.insert(DatabaseHelper.PROGRAM3_TABLE_NAME, null, values);
		//Program 3 - Level 3, Interval
		values.put(DatabaseHelper.level, 3);
		data = "0.70,0.65,0.75,0.65,0.80,0.65,0.80,0.65,0.70,0.65,0.70";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.75,0.70,0.80,0.70,0.85,0.70,0.85,0.70,0.75,0.70,0.75";
		values.put(DatabaseHelper.intensityhigh, data);
		data = "0.0,0.10,0.20,0.30,0.40,0.50,0.60,0.70,0.80,0.90,1.00";
		values.put(DatabaseHelper.progresspercentage, data);
		values.put(DatabaseHelper.duration, 30);
		values.put(DatabaseHelper.aerobic, false);
		db.insert(DatabaseHelper.PROGRAM3_TABLE_NAME, null, values);
		//Program 3 - Level 4, Aerobic
		values.put(DatabaseHelper.level, 4);
		data = "0.75";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.80";
		values.put(DatabaseHelper.intensityhigh, data);
		values.put(DatabaseHelper.progresspercentage, 0);
		values.put(DatabaseHelper.duration, 40);
		values.put(DatabaseHelper.aerobic, true);
		db.insert(DatabaseHelper.PROGRAM3_TABLE_NAME, null, values);
		//Program 3 - Level 4, Interval
		values.put(DatabaseHelper.level, 4);
		data = "0.75,0.75,0.65,0.75,0.75,0.65,0.80,0.80,0.65,0.80,0.80,0.65,0.80,0.80,0.65,0.80,0.80,0.65,0.75,0.75,0.75";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.80,0.80,0.70,0.80,0.80,0.70,0.85,0.85,0.70,0.85,0.85,0.70,0.85,0.85,0.70,0.85,0.85,0.70,0.80,0.80,0.80";
		values.put(DatabaseHelper.intensityhigh, data);
		data = "0.0,0.05,0.10,0.15,0.20,0.25,0.30,0.35,0.40,0.45,0.50,0.55,0.60,0.65,0.70,0.75,0.80,0.85,0.90,0.95,1.00";
		values.put(DatabaseHelper.progresspercentage, data);
		values.put(DatabaseHelper.duration, 40);
		values.put(DatabaseHelper.aerobic, false);
		db.insert(DatabaseHelper.PROGRAM3_TABLE_NAME, null, values);
		//Program 3 - Level 5, Aerobic
		values.put(DatabaseHelper.level, 5);
		data = "0.75";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.85";
		values.put(DatabaseHelper.intensityhigh, data);
		values.put(DatabaseHelper.progresspercentage, 0);
		values.put(DatabaseHelper.duration, 40);
		values.put(DatabaseHelper.aerobic, true);
		db.insert(DatabaseHelper.PROGRAM3_TABLE_NAME, null, values);
		//Program 3 - Level 5, Interval
		values.put(DatabaseHelper.level, 5);
		data = "0.75,0.75,0.65,0.80,0.80,0.65,0.80,0.80,0.65,0.85,0.65,0.85,0.65,0.85,0.65,0.80,0.80,0.65,0.80,0.80,0.65";
		values.put(DatabaseHelper.intensitylow, data);
		data = "0.80,0.80,0.70,0.85,0.85,0.70,0.85,0.85,0.70,0.90,0.70,0.90,0.70,0.90,0.70,0.85,0.85,0.70,0.85,0.85,0.70";
		values.put(DatabaseHelper.intensityhigh, data);
		data = "0.0,0.05,0.10,0.15,0.20,0.25,0.30,0.35,0.40,0.45,0.50,0.55,0.60,0.65,0.70,0.75,0.80,0.85,0.90,0.95,1.00";
		values.put(DatabaseHelper.progresspercentage, data);
		values.put(DatabaseHelper.duration, 40);
		values.put(DatabaseHelper.aerobic, false);
		db.insert(DatabaseHelper.PROGRAM3_TABLE_NAME, null, values);
		values.clear();
		
		db.close();
	}

}
