package cscece.android.fitformula;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyWorkout extends Activity {
	private ImageView myImage;
	private Button myButton;
	private Button viewCalButton;
	private TextView myText;
	private CalendarView myCal;
	public static Context context; 
	
    private Button mPickDate;
    private int mYear;
    private int mMonth;
    private int mDay;
    private long calID;
    static final int DATE_DIALOG_ID = 0;
    
    private int program;
    private int level;
    private int numInterval;
    private int numAerobic;
    private int numCycles;
    private int rest;
    private int altRest;
    private boolean restAlternate;
	
	public static final String PREFS_NAME = "MyPrefs";	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		context = this;
		setContentView(R.layout.my_workout_layout);

		//myImage= (ImageView) findViewById(R.id.running_man);
		myButton= (Button) findViewById(R.id.get_workout);
		myText= (TextView) findViewById(R.id.workout_text);
		viewCalButton=(Button) findViewById(R.id.view_calendar);
		//myCal = (CalendarView) findViewById(R.id.my_calendar);
		
        mPickDate = (Button) findViewById(R.id.make_calendar_event);
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		boolean savedWorkouts = settings.getBoolean("savedNewWorkouts", false);
		boolean gottenWorkout = settings.getBoolean("gottenWorkout", false);

		if (!savedWorkouts) { // if workouts haven't been saved to database yet
			Log.d("db","saving new workouts");
			initiateWorkoutsInDB();

			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("savedNewWorkouts", true);
			editor.commit();
		}

	}// end of onCreate
	
	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {			

			if (year>=mYear && monthOfYear>=mMonth && dayOfMonth>=mDay){ //making sure start date is current day or later
				mYear = year;
				mMonth = monthOfYear;
				mDay = dayOfMonth;			
				Log.d("email",""+mYear+"-"+mMonth+"-"+mDay);
				
				/*Calendar beginTime = Calendar.getInstance();
				beginTime.set(mYear, mMonth, mDay);		
				Log.d("email",""+beginTime.get(Calendar.DAY_OF_MONTH));
				long startMillis = beginTime.getTimeInMillis();
				Calendar output = Calendar.getInstance();
				output.setTimeInMillis(startMillis);
				Log.d("email",""+output.get(Calendar.DAY_OF_MONTH));*/
				//Log.d("email",""+TimeZone.getDefault().getID());
				//Log.d("email",""+TimeZone.getDefault().getOffset(System.currentTimeMillis()));
				//Log.d("email",""+Time.getCurrentTimezone());
				makeWorkoutSchedule(mYear,mMonth,mDay);
			}
			else{
				new AlertDialog.Builder(MyWorkout.this)
				.setTitle("Invalid Date")
				.setMessage("Please select a valid start date.")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface arg0, int arg1) {
				        // Some stuff to do when ok got clicked
				    }
				})				
				.show();
				
				/*Toast.makeText(MyWorkout.this,
						"Please select a valid start date.",
						Toast.LENGTH_LONG).show();*/
			}
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:			
			DatePickerDialog dpd = new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
			dpd.setTitle("What day would you like to begin working out?");
			return dpd;
		}
		return null;
	}

	public static Context getContext() {
		return context;
	}
	public void makeWorkoutSchedule (int myYear, int myMonth, int myDay){
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String calDisplayName = settings.getString ("calDisplayName", null);
		if (calDisplayName == null){
			Log.d("email","calDisplayName is null");
			new AlertDialog.Builder(MyWorkout.this)
			.setTitle("Invalid Google Account")
			.setMessage("Please designate a Google Account and Calendar in the Settings tab.")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface arg0, int arg1) {			    	
			    }
			})				
			.show();					
			return;
		}		
		calID=settings.getLong("calID", -1);
		
		DatabaseHelper dbh;
		dbh = new DatabaseHelper(this);		
		int rowIndex = 1;
		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor c = db.query(DatabaseHelper.USER_TABLE_NAME, new String[] {
				DatabaseHelper.program, DatabaseHelper.level},
				"_id = " + rowIndex, null, null, null, null);

		if (c.moveToNext()) { // table has data (rows) in it
			int columnIndex = c.getColumnIndex(DatabaseHelper.program);
			program = c.getInt(columnIndex);
			Log.d("db", "program " + program);
			
			columnIndex = c.getColumnIndex(DatabaseHelper.level);
			level = c.getInt(columnIndex);
			Log.d("db", "level " + level);												
		}
		
		c = db.query(DatabaseHelper.FREQUENCY_TABLE_NAME, new String[] {
				DatabaseHelper.program, DatabaseHelper.level,DatabaseHelper.numinterval,
				DatabaseHelper.numaerobic,DatabaseHelper.numcycles,DatabaseHelper.rest,DatabaseHelper.restalternate},
				DatabaseHelper.program+" = "+program+" AND "+DatabaseHelper.level+" = "+level, null, null, null, null);
		
		if (c.moveToNext()) { // table has data (rows) in it			
			int columnIndex = c.getColumnIndex(DatabaseHelper.numinterval);
			numInterval= c.getInt(columnIndex);
			Log.d("db", "numinterval " + numInterval);			
			columnIndex = c.getColumnIndex(DatabaseHelper.numaerobic);
			numAerobic = c.getInt(columnIndex);
			Log.d("db", "numaerobic " + numAerobic);
			columnIndex = c.getColumnIndex(DatabaseHelper.numcycles);
			numCycles = c.getInt(columnIndex);
			Log.d("db", "numcycles " + numCycles);
			columnIndex = c.getColumnIndex(DatabaseHelper.rest);
			rest = c.getInt(columnIndex);
			Log.d("db", "rest " + rest);
			if(rest==1){
				altRest=2;
			}else{
				altRest=1;
			}
			Log.d("db", "altRest " + altRest);
			columnIndex = c.getColumnIndex(DatabaseHelper.restalternate);
			restAlternate = (c.getInt(columnIndex)>0);			
			Log.d("db", "restalternate " + restAlternate);
		}
		
		Calendar beginTime = Calendar.getInstance();
		beginTime.set(myYear, myMonth, myDay, 0, 0, 0);		
		
		for (int i = 0; i < numCycles; i++) {
			if (restAlternate) {
				boolean altRestTime=false;
				for (int j = 0; j < numInterval; j++) {
					if (altRestTime){
						makeCalendarEvent(beginTime,dbh, program, level, false, calID);
						if (j<numInterval-1 || numAerobic >=1){ //do not add rest time if the last session of cycle
							beginTime.add(Calendar.DAY_OF_MONTH, altRest);
						}
						altRestTime=!altRestTime;
					}else{ //!altRestTime
						makeCalendarEvent(beginTime,dbh, program, level, false, calID);
						if (j<numInterval-1 || numAerobic >=1){ //do not add rest time if the last session of cycle
							beginTime.add(Calendar.DAY_OF_MONTH, rest);
						}
						altRestTime=!altRestTime;
					}
				}
				for (int k = 0; k < numAerobic; k++) {
					if (altRestTime){
						makeCalendarEvent(beginTime,dbh, program, level, true, calID);
						if (k<numAerobic-1){ //do not add rest time if the last session of cycle
							beginTime.add(Calendar.DAY_OF_MONTH, altRest);
						}
						altRestTime=!altRestTime;
					}else{ //!altRestTime
						makeCalendarEvent(beginTime,dbh, program, level, true, calID);
						if (k<numAerobic-1){ //do not add rest time if the last session of cycle
							beginTime.add(Calendar.DAY_OF_MONTH, rest);
						}
						altRestTime=!altRestTime;
					}										
				}
			} 
			else {
				for (int j = 0; j < numInterval; j++) {
					Log.d("email",""+beginTime.get(Calendar.DAY_OF_MONTH));

					makeCalendarEvent(beginTime,dbh, program, level, false, calID);
					if (j<numInterval-1 || numAerobic >=1){ //do not add rest time if the last session of cycle
						beginTime.add(Calendar.DAY_OF_MONTH, rest);								
					}
				}
				for (int k = 0; k < numAerobic; k++) {
					makeCalendarEvent(beginTime,dbh, program, level, true, calID);
					if (k<numAerobic-1){ //do not add rest time if the last session of cycle
						beginTime.add(Calendar.DAY_OF_MONTH, rest);
					}															
				}
			}
			beginTime.add(Calendar.DAY_OF_MONTH, 2); //one day rest between cycles
		}
		
		
		db.close();
		
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("gottenSchedule", true);
		editor.commit();			
		
		//myImage.setVisibility(View.GONE);
		myButton.setVisibility(View.GONE);
		myText.setVisibility(View.GONE);		
		mPickDate.setVisibility(View.GONE);
		viewCalButton.setVisibility(View.VISIBLE);
		viewCalButton.setGravity(Gravity.CENTER_HORIZONTAL);
        //myCal.setVisibility(View.VISIBLE);*/
        
		viewCalendar(viewCalButton);
		//TODO: ability to delete calendar events and db entries
	}
	
	public void viewCalendar (View theButton){
		long startMillis = System.currentTimeMillis();
		Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
		builder.appendPath("time");
		ContentUris.appendId(builder, startMillis);
		Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
		startActivity(intent);
	}
	
	public void makeCalendarEvent(Calendar beginTime,DatabaseHelper dbh, int myProgram, int myLevel, boolean aerobic, long mCalID){		

		long startMillis = 0;
		// long endMillis = 0;
		// Calendar beginTime = Calendar.getInstance();
		// beginTime.set(myYear, myMonth, myDay); //months start at 0
		startMillis = beginTime.getTimeInMillis();

		// Calendar endTime = Calendar.getInstance();
		// endTime.set(myYear, myMonth, myDay); //months start at 0
		// endMillis = endTime.getTimeInMillis();

		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		values.put(Events.DTSTART, startMillis+TimeZone.getDefault().getOffset(System.currentTimeMillis()));
		values.put(Events.DTEND, startMillis+TimeZone.getDefault().getOffset(System.currentTimeMillis())); 
		// values.put(Events.EVENT_LOCATION, "Gym");		
		if(aerobic==true){
			values.put(Events.TITLE, "Workout Session - Aerobic");
			values.put(Events.DESCRIPTION, "FitFormula Aerobic Workout Session - Program "+ myProgram + " - Level " + myLevel);}
		else{//aerobic==false
			values.put(Events.TITLE, "Workout Session - Interval");
			values.put(Events.DESCRIPTION, "FitFormula Interval Workout Session - Program "+ myProgram + " - Level " + myLevel);}		
		values.put(Events.ALL_DAY, 1);
		values.put(Events.AVAILABILITY, Events.AVAILABILITY_FREE);
		values.put(Events.CALENDAR_ID, mCalID);
		values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
		Uri uri = cr.insert(Events.CONTENT_URI, values);

		// get the event ID that is the last element in the Uri
		long eventID = Long.parseLong(uri.getLastPathSegment());
		//Log.d("email", "eventID:" + eventID);

		ContentValues mValues = new ContentValues();
		SQLiteDatabase db = dbh.getWritableDatabase();

		mValues.put(DatabaseHelper.program, myProgram);
		mValues.put(DatabaseHelper.level, myLevel);
		mValues.put(DatabaseHelper.aerobic, aerobic);
		mValues.put(DatabaseHelper.date, startMillis);
		mValues.put(DatabaseHelper.eventID, eventID);
		db.insert(DatabaseHelper.MY_PROGRAM_TABLE_NAME, null, mValues);
		db.close();
		
/*		Calendar cal = Calendar.getInstance();              
		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra("beginTime", cal.getTimeInMillis());
		intent.putExtra("allDay", true);
		intent.putExtra("rrule", "FREQ=YEARLY");
		intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
		intent.putExtra("title", "A Test Event from android app");
		startActivity(intent);*/

	}

	@Override
    protected void onResume() {
        super.onResume();
        
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);						
		boolean gottenWorkout = settings.getBoolean("gottenWorkout", false);
		boolean gottenSchedule = settings.getBoolean("gottenSchedule", false);
		if (gottenWorkout){
			myText.setVisibility(View.GONE);
	 		myButton.setVisibility(View.GONE);
			if (!gottenSchedule){
				mPickDate.setVisibility(View.VISIBLE);
			}else{
				viewCalButton.setVisibility(View.VISIBLE);
				viewCalButton.setGravity(Gravity.CENTER_HORIZONTAL);
			}
		}								  
		
		/*For Spiral 4 demo
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
     	}*/
    }	
	
	// Method called when the "Get Workout" button is Pushed
	public void getWorkoutPushed(View view) {		

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
