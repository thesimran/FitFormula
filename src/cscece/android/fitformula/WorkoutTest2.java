package cscece.android.fitformula;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import cscece.android.fitformula.utils.ChartRenderer;
import cscece.android.fitformula.utils.DatabaseHelper;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.CountDownTimer;


public class WorkoutTest2 extends Activity implements View.OnClickListener{
	
	//Constants
	public final static int X_AXIS_LENGTH = 11;
	private final int DEFAULT_COLOR =  Color.argb(250, 0, 210, 250);
	private final long INTERVAL = 1000; //in milliseconds
		//ratings
	public final int EASY = 1;
	public final int JUST_RIGHT = 2;
	public final int TOO_HARD = 3;
	
	//Fields that we have to extract from the DB
	private long workoutDate;
	private boolean isAerobic;
	private long eventID;
	private boolean completed;
	//TODO: intro field? -- ask John
	private int program;
	private int level;
	private int duration; //in minutes
	private String intensityLowStr;
	private String intensityHighStr;
	private double[] intensityLow = new double[X_AXIS_LENGTH];
	private double[] intensityHigh = new double[X_AXIS_LENGTH];
	
	//Other Fields -- workout state preservation
	private long todaysDate; //check to see if it matches the assigned Workout date
	private static boolean isPaused; //this bool will be true if the user has paused the workout
	private static long totalTimeRemaining;
	private RelativeLayout mRelative;
	TextView timeRemainingView;
	public Context context;
	private static double percentComplete;
	private static double currentIntensity;
	private static double[] theValues =  new double[X_AXIS_LENGTH];
	private static int theColor;
	private static boolean isFirstRender;
	public View chartView;
	public ViewGroup.LayoutParams params;
	public AlertDialog alert;
	private long durationMillis;
	private Button pauseButton;
	private boolean hasBegun;
	
	
	//CountDownTimer
	public WorkoutCountDownTimer timer;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ready_workout);
        context = this;
        hasBegun = false;
        //TODO: get fields from DB
        pullFromDB();
        
        processIntensity();
    }
    
    @Override
    public void onResume(){
    	
    	super.onResume();
    }
    
    @Override
    public void onStop(){
    	
    	if(hasBegun){
    	
	    	if(!isPaused){
	    		
	    		timer.cancel();
	        	//call workoutPaused
	        	pauseWorkout();
	    		
	    	}
    	}
    	super.onStop();
    	
    }
    
    @Override
    public void onPause(){
    	
    	if(hasBegun){
    	
	    	if(!isPaused){
	    		
	    		timer.cancel();
	        	//call workoutPaused
	        	pauseWorkout();
	    		
	    	}
    	}
    	
    	super.onPause();
    }
    
    @Override
    public void onDestroy(){
    	workoutCancelled();
    	super.onDestroy();
    }
    
    private void workoutCancelled(){

    	finish();
    	
    }
    
    private void areYouSure(){
    	
    	//Housekeeping
    	timer.cancel();
    	isPaused = true;
    	
    	//Bring up a dialog asking the user if they're sure they want to cancel the workout
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure that you would like to cancel the workout?" +
				" If you do, you will not be able to resume from this point, you will have to restart the workout from the beginning")
		       .setCancelable(false)
		       .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		              workoutCancelled();
		           }
		       })
		       .setNegativeButton("Resume Workout", new DialogInterface.OnClickListener() {
		    	   public void onClick(DialogInterface dialog, int id) {
		    		   resumeWorkout();
		    	   }
		       });
		alert = builder.create();
		alert.show();
    }
    
    @Override
    public void onBackPressed(){
    	
    	if(hasBegun){
    		areYouSure();
    	}else{
    		super.onBackPressed();
    	}
    	
    }
    
    //Listener for 'Pause Workout' button
    public void onClick(View view) {
    	
    	//so first we have to cancel the timer
    	timer.cancel();
    	//call workoutPaused
    	pauseWorkout();
    	
    }
    
    private void pauseWorkout(){
    	
    	
    	isPaused = true;
    	
    	//show an alert that stays up until the user is ready to resume
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("You have paused the workout.")
		       .setCancelable(false)
		       .setNeutralButton("Resume", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		              resumeWorkout();
		           }
		       });
		alert = builder.create();
		alert.show();
    }
    
    //called when the user resumes the workout after pausing it
    private void resumeWorkout(){
    	
    	isPaused = false;
    	
    	//start WorkoutTimer with totalTimeLeft as first parameter
        timer = new WorkoutCountDownTimer(totalTimeRemaining, INTERVAL);
        timer.start();
    }
    
    //Listener for 'Begin' button
    public void startWorkout(View v){
    	//new layout!
    	setContentView(R.layout.workout_started2);
    	mRelative = (RelativeLayout)findViewById(R.id.workout_relative);
    	
    	//time left TextView
    	timeRemainingView = new TextView(context);
        timeRemainingView.setId(2);
        timeRemainingView.setText(Integer.toString(duration) +  ":00");
        timeRemainingView.setTextSize(30);
        
        // Pause Button widget
        pauseButton = new Button(context);
        pauseButton.setId(3);
        pauseButton.setText("Pause Workout");
        pauseButton.setOnClickListener(this);
        
        durationMillis = duration * 60 * 1000;
        totalTimeRemaining = durationMillis;
        // initialize progress variables
        percentComplete = 0;
        currentIntensity = intensityLow[0]; 
        
        double[] temp = {currentIntensity,0,0,0,0,0,0,0,0,0,0};
        theValues = temp;
        theColor = DEFAULT_COLOR;
        isPaused = false;
        
      //and render
      reRender(theColor,theValues);
      
      //convert duration to milliseconds
      long durationMillis = duration * 60 * 1000;
      totalTimeRemaining = durationMillis;
      hasBegun = true;
      //start WorkoutTimer
      timer = new WorkoutCountDownTimer(durationMillis, INTERVAL);
      timer.start();
    }
    
    /* v[] must be length of 11 (X_AXIS_LENGTH), a value of 0 indicated lack of progress	
	 * 
	 */
	public void reRender(int c, double[] v){ 
		
		int color = c;
		
		List<double[]> values = new ArrayList<double[]>();
		//dummy values for line series that is not used
		double[] dummyVals = {0,0,0,0,0,0,0,0,0,0,0};
		values.add(dummyVals);
		
		double[] realValues = v;
		
		List<double[]> x = new ArrayList<double[]>();
	    x.add(new double[] { 0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 });		
	    String[] titles = new String[] { "Progress" };
       
        //These values mean nothing, but are necessary for the renderer method
	    // Color is transparent, so you just can't see the line graph
        int[] colors = new int[] { Color.argb(0, 0, 210, 250)};
        PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE };
        
        XYMultipleSeriesRenderer renderer = ChartRenderer.buildRenderer(colors, styles);
        int length = renderer.getSeriesRendererCount();
        for (int i = 0; i < length; i++) {
          ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
        }
        ChartRenderer.setChartSettings(renderer, "Workout Progress", "Percent Complete", "Intensity", -2, 100, 0, 100,
            Color.LTGRAY, Color.LTGRAY);
        renderer.setXLabels(12);
        renderer.setYLabels(10);
        renderer.setShowGrid(true);
        renderer.setXLabelsAlign(Align.CENTER);
        renderer.setYLabelsAlign(Align.RIGHT);
        renderer.setZoomEnabled(false, false);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(false, false);
        
        XYSeries progressSeries = new XYSeries("Progress");
       
        //add values to the chart
        for(int i = 0; i < X_AXIS_LENGTH; i++){
        	if(realValues[i] != 0){
        		progressSeries.add(i * 10, realValues[i]);
        	}else{
        		//indicates lack of progress
        		progressSeries.add(i * 10,0);
        	}
        }
        
        XYSeriesRenderer progressRenderer = new XYSeriesRenderer();
        
        progressRenderer.setColor(color);

        XYMultipleSeriesDataset dataset = ChartRenderer.buildDataset(titles, x, values);
        dataset.addSeries(0, progressSeries);
        renderer.addSeriesRenderer(0, progressRenderer);
        progressRenderer.setDisplayChartValues(false);
        progressRenderer.setChartValuesTextSize(10);
        renderer.setBarSpacing(0.5);
        
        String[] types = new String[] {BarChart.TYPE, LineChart.TYPE };
        //Add the view!
        chartView = ChartFactory.getCombinedXYChartView(context, dataset, renderer, types);
        
        
        if(!isFirstRender){
        
        	mRelative.removeAllViews();
        }
	    // now we have to manually adjust the layout so that the user can see the time remaining
	    // and the graph at the same time
	    params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 600);
	   
	    chartView.setId(1);
	    
	    //chart
	    RelativeLayout.LayoutParams relParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 550);	        
	    relParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
	    mRelative.addView(chartView,relParams);	
	    
	    //time remaining TextView
	    relParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);	        
	    //relParams.addRule(RelativeLayout.BELOW,chartView.getId());
	    //relParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
	    relParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    relParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	    mRelative.addView(timeRemainingView,relParams);
	    
	    //Now lets add the pause button
	    relParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
	    relParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    relParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    mRelative.addView(pauseButton,relParams);
	    
        
	    isFirstRender = false;
	}
	
	//used to switch the color of the bars every second
	public void switchColor(int c){
		int theColor = c;
		reRender(theColor,theValues);
	}
    
    //TODO: Let's get this thing working first, then we'll deal with the getting the data from the DB
    private void pullFromDB(){
    	
    	//todaysDate = System.currentTimeMillis();
    	
    	// get the current date
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        
        //Log.d("today", "" + mYear + " " + mMonth + " " + mDay);
    	   
        
        DatabaseHelper dbh;
		dbh = new DatabaseHelper(this);		
		int rowIndex = 1;
		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor workoutCursor = db.query(DatabaseHelper.MY_PROGRAM_TABLE_NAME, new String[] {
				DatabaseHelper.program, DatabaseHelper.level, DatabaseHelper.aerobic,
				DatabaseHelper.date}, null, null, null, null, null, null);
		
    	while(workoutCursor.moveToNext()){
    		int columnIndex = workoutCursor.getColumnIndex(DatabaseHelper.date);
    		long theDate = workoutCursor.getLong(columnIndex);
    		c.setTimeInMillis(theDate);
    		Log.d("test","movetonext");

    		if(mYear == c.get(Calendar.YEAR) && mMonth == c.get(Calendar.MONTH) && mDay == c.get(Calendar.DAY_OF_MONTH)){
    			columnIndex = workoutCursor.getColumnIndex(DatabaseHelper.program);
    			program = workoutCursor.getInt(columnIndex);
    			columnIndex = workoutCursor.getColumnIndex(DatabaseHelper.level);
    			level = workoutCursor.getInt(columnIndex);
    			columnIndex = workoutCursor.getColumnIndex(DatabaseHelper.aerobic);
    			isAerobic = workoutCursor.getInt(columnIndex) > 0;
    			
    		}
    	}
		Log.d("test","program"+program);

		int aerobicValue=0;
		if(isAerobic){
			aerobicValue=1;
		}else{
			aerobicValue=0;
		}
    	//Now we have to go into the program table and extract the correct metrics
    	if(program == 1){ 
    		
    		workoutCursor = db.query(DatabaseHelper.PROGRAM1_TABLE_NAME, new String[] {
    				DatabaseHelper.level, DatabaseHelper.intensitylow, DatabaseHelper.intensityhigh,
    				DatabaseHelper.duration,
    				DatabaseHelper.aerobic},DatabaseHelper.level+" = "+level+" AND "+DatabaseHelper.aerobic+" = "+aerobicValue, 
    				null, null, null, null, null);
    		
    		/*workoutCursor = db.getReadableDatabase().rawQuery("SELECT _ID, level, intensitylow, intensityhigh, duration " +
    				"FROM "+DatabaseHelper.PROGRAM1_TABLE_NAME+ " WHERE level == "  + level, null);*/
    	}else if(program == 2){
    		workoutCursor = db.query(DatabaseHelper.PROGRAM2_TABLE_NAME, new String[] {
    				DatabaseHelper.level, DatabaseHelper.intensitylow, DatabaseHelper.intensityhigh,
    				DatabaseHelper.duration,
    				DatabaseHelper.aerobic},DatabaseHelper.level+" = "+level+" AND "+DatabaseHelper.aerobic+" = "+aerobicValue, 
    				null, null, null, null, null);
    	}else{
    		workoutCursor = db.query(DatabaseHelper.PROGRAM2_TABLE_NAME, new String[] {
    				DatabaseHelper.level, DatabaseHelper.intensitylow, DatabaseHelper.intensityhigh,
    				DatabaseHelper.duration,
    				DatabaseHelper.aerobic},DatabaseHelper.level+" = "+level+" AND "+DatabaseHelper.aerobic+" = "+aerobicValue, 
    				null, null, null, null, null);
    	}

    	if (workoutCursor.moveToNext()) { // table has data (rows) in it
        	Log.d("test", "gettingworkout");    

			int columnIndex = workoutCursor.getColumnIndex(DatabaseHelper.intensitylow);
			intensityLowStr = workoutCursor.getString(columnIndex);
			
			columnIndex = workoutCursor.getColumnIndex(DatabaseHelper.intensityhigh);
			intensityHighStr = workoutCursor.getString(columnIndex);
			
			columnIndex = workoutCursor.getColumnIndex(DatabaseHelper.duration);
			duration = workoutCursor.getInt(columnIndex);
		}
    	//TODO: dummy data
    	//workoutDate = System.currentTimeMillis();
    	//isAerobic = true;
    	//eventID = 1;
    	completed = false;
    	//program = 1;
    	//level = 1;
    	//intensityLowStr = "0.55";
    	//intensityHighStr = "0.60";
    	//duration = 20;
    	
    	db.close();
    	workoutCursor.close();
    	
    }
    
    //Called when the workout is done
    private void workoutComplete(){
    	
    	
    	/*Lets bring up a dialog box to inform the user that they are 
    	 * done and can continue on to the cool down phase/activity
    	 * 
    	 * TODO: Create cooldown (and warmup) classes/activities. Not in this Activity though
    	 */
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Congratulations! You have completed the workout successfully!  " +
				"Please rate the workout based on difficulty.")
		       .setCancelable(false)
		       .setPositiveButton("Easy", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               
		        	   moveOntoCoolDown(EASY);
		        	   
		           }
		       })
		       .setNegativeButton("Too Hard", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               
		        	   moveOntoCoolDown(TOO_HARD);
		        	
		        	   
		           }
		       })
		       .setNeutralButton("Just Right", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		              
		        	   moveOntoCoolDown(JUST_RIGHT);
		        	  
		           }
		       });
		alert = builder.create();
		alert.show();
		
		
    	
    }
    
    private void moveOntoCoolDown(int r){
    	
    	//TODO: push values to DB
    	int rating = r;
    	completed = true;
    	
    	
    	//TODO: Start cooldown activity (does not yet exist)
    	finish();
    }
    
    /*We need this function to process the intensity vectors
	 * because they are in CSV String format.  They need to be parsed into
	 * double values so that aChartEngine can deal with them.
	 */
    private void processIntensity(){
    	
    	if(isAerobic){
    		//easier to deal with
    		intensityLow[0] = Double.parseDouble(intensityLowStr) * 100;
    		for(int i=1;i < X_AXIS_LENGTH; i++){
    			intensityLow[i] = intensityLow[0] * 100;
    		}
    		intensityHigh[0] = Double.parseDouble(intensityHighStr);
    		for(int i=1;i < X_AXIS_LENGTH; i++){
    			intensityHigh[i] = intensityLow[0] * 100;
    		}	
    	}else{ //interval workout, a little more difficult
    		
    		//CSV values, we gotta run a split(',') on it
    		String[] temp = intensityLowStr.split(",");
    		for(int i=0;i < X_AXIS_LENGTH; i++){
    			intensityLow[i] = Double.parseDouble(temp[i]) * 100;
    		}
    		temp = intensityHighStr.split(",");
    		for(int i=0;i < X_AXIS_LENGTH; i++){
    			intensityHigh[i] = Double.parseDouble(temp[i]) * 100;
    		}
    	}
    	
    }
    
    // CountDownTimer inner class
 	public class WorkoutCountDownTimer extends CountDownTimer
 	{
 		
		public WorkoutCountDownTimer(long startTime, long interval)
		{
			super(startTime, interval);
			//May not even have to put anything else in here, we'll see...
		}


		@Override
		public void onTick(long millisUntilFinished)
		{
			long millisLeft = millisUntilFinished;
			//update global/total time left variable
			totalTimeRemaining = millisLeft;
			
			//Convert milliseconds left to minutes and seconds left to show the user
			int secondsTotal = (int) millisLeft / 1000;
			int seconds = secondsTotal % 60;
			int minutes = secondsTotal / 60;
			if(seconds > 9){
				timeRemainingView.setText(minutes + ":" + seconds);
			}else{
				timeRemainingView.setText(minutes + ":0" + seconds);
			}
			
			long millisElapsed = durationMillis - millisLeft;
			percentComplete = (double)millisElapsed /(double)durationMillis;
			
			//Log.d("percentComplete:",percentComplete + "");
			
			// if an interval workout
			if(!isAerobic){
			
				if(percentComplete > .1){
					currentIntensity = intensityLow[1];
				}else if(percentComplete > .2){
					currentIntensity = intensityLow[2];
				}else if(percentComplete > .3){
					currentIntensity = intensityLow[3];
				}else if(percentComplete > .4){
					currentIntensity = intensityLow[4];
				}else if(percentComplete > .5){
					currentIntensity = intensityLow[5];
				}else if(percentComplete > .6){
					currentIntensity = intensityLow[6];
				}else if(percentComplete > .7){
					currentIntensity = intensityLow[7];
				}else if(percentComplete > .8){
					currentIntensity = intensityLow[8];
				}else if(percentComplete > .9){
					currentIntensity = intensityLow[9];
				}
			}
			
			
			//lets work out the values
			if(percentComplete > .1 && theValues[1] == 0){
				theValues[1] = currentIntensity;
			}
			if(percentComplete > .2 && theValues[2] == 0){
				theValues[2] = currentIntensity;
			}
			if(percentComplete > .3 && theValues[3] == 0){
				theValues[3] = currentIntensity;
			}
			if(percentComplete > .4 && theValues[4] == 0){
				theValues[4] = currentIntensity;	
			}
			if (percentComplete > .5 && theValues[5] == 0){
				theValues[5] = currentIntensity;
			}
			if(percentComplete > .6 && theValues[6] == 0){
				theValues[6] = currentIntensity;
			}
			if(percentComplete > .7 && theValues[7] == 0){
				theValues[7] = currentIntensity;
			}
			if(percentComplete > .8 && theValues[8] == 0){
				theValues[8] = currentIntensity;
			}
			if(percentComplete > .8 && theValues[8] == 0){
				theValues[8] = currentIntensity;
			}
			if(percentComplete > .9 && theValues[9] == 0){
				theValues[9] = currentIntensity;
			}
			// I think that's all we need here
			
			//change the color of the current bar each second
			if(theColor == DEFAULT_COLOR){
				theColor = Color.YELLOW; //for now at least...
			}else{
				theColor = DEFAULT_COLOR;
			}
			switchColor(theColor);
			
		}
		
		@Override
		public void onFinish()
		{
			//When we get here, that means that we are over 100% done, meaning the workout is complete
			
			if(!isAerobic){
				currentIntensity = intensityLow[10];
			}
			
			theValues[10] = currentIntensity;
			workoutComplete();
					
		}
		
		
 	}
    
}


	
