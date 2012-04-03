package cscece.android.fitformula;

import java.util.ArrayList;
import java.util.List;
import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FirstWorkout extends Activity {
	//Constants
	public final int DURATION_MINS = 20;
	public final int X_AXIS_LENGTH = 11;
	public final Long[] WORKOUT_DURATION = {(long) 1200000}; //in milliseconds
	public final int PHASE_ONE = 1;
	public final int PHASE_TWO = 2;
	private final int DEFAULT_INTENSITY = 53; //50-55%
	
	private TextView instructionText;
	private TextView timeRemaining;
	private WorkoutTimeTask firstPhase;
	private WorkoutTimeTask secondPhase;
	public Context context;
	public View chartView;
	public ViewGroup.LayoutParams params;
	private RelativeLayout mRelative;
	public AlertDialog alert;
	
	//global values -- to be used to preserve progress state (and color) in the chartView
	private double[] theValues =  new double[X_AXIS_LENGTH];
	private boolean isFirstRender;
	private int theColor;
	private final int DEFAULT_COLOR =  Color.argb(250, 0, 210, 250);
	private double percentComplete;
	private int currentIntensity;
	private static int currentPhase;
	
	/*Required to preserve the total time remaining when changing phases
	 * For now, this is a workaround for the problem of not being able
	 * to pause an AsyncTask.  Although, to implement the 'Pause Workout'
	 * feature, an alternate process must be used.
	 */
	private long globalTimeLeft;
	
	
	@Override
	public void onCreate(Bundle icicle){
		super.onCreate(icicle); 
		setContentView(R.layout.first_workout);
		context = this;
		instructionText =(TextView)findViewById(R.id.first_workout_inst);
		
	}
	
	public void cancelWorkout (View view){
		finish();
	}
		
	//Listener for the 'Begin Warmup' button
	public void startWorkout(View view){
		//TODO: First, take heart rate, hard-coded for now
		int heartRate = 65;
		/***********************/
		
		//new layout!
		setContentView(R.layout.workout_started);
		mRelative = (RelativeLayout)findViewById(R.id.workout_relative);
		
		
		timeRemaining = new TextView(context);//(TextView)findViewById(R.id.time_remaining);
        timeRemaining.setId(2);
        //20 minutes left -- start
        timeRemaining.setText("20:00");
        timeRemaining.setTextSize(30);
        
        // initialize progress variables
        percentComplete = 0;
        currentIntensity = DEFAULT_INTENSITY; 
        currentPhase = PHASE_ONE;
        double[] test = {currentIntensity,0,0,0,0,0,0,0,0,0,0};
        theValues = test;
        theColor = DEFAULT_COLOR;
        
        //and render
		reRender(theColor,theValues);
		

		//start time/progress task
		firstPhase =(WorkoutTimeTask) new WorkoutTimeTask().execute(WORKOUT_DURATION);
		
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
        
        XYMultipleSeriesRenderer renderer = RendererClass.buildRenderer(colors, styles);
        int length = renderer.getSeriesRendererCount();
        for (int i = 0; i < length; i++) {
          ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
        }
        RendererClass.setChartSettings(renderer, "Workout Progress", "Percent Complete", "Intensity", 0, 100, 0, 100,
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

        XYMultipleSeriesDataset dataset = RendererClass.buildDataset(titles, x, values);
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
	    relParams.addRule(RelativeLayout.BELOW,chartView.getId());
	    relParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
	    mRelative.addView(timeRemaining,relParams);
        
	    isFirstRender = false;
	}
	
	
	
	public void switchColor(int c){
		int theColor = c;
		reRender(theColor,theValues);
	}
	
	public void finishedFirstPhase(int hr){
		
		
		
		//Gotta give the user an AlertDialog to give feedback on how they feel
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("How is the workout so far?")
		       .setCancelable(false)
		       .setPositiveButton("Easy", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                //Up it by 10%
		        	   currentIntensity += 10; // 60-65%
		        	   
		        	   startSecondPhase();
		        	   //dialog.cancel();
		           }
		       })
		       .setNegativeButton("Too Hard", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                //Keep current intensity
		        	   currentIntensity -= 10; 
		        	   
		        	   startSecondPhase();
		        	   //dialog.cancel();
		        	   
		           }
		       })
		       .setNeutralButton("A Challenge", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                //Bring it down 10%
		        	   currentIntensity = DEFAULT_INTENSITY; // did nothing/no change
		        	   startSecondPhase();
		        	   //dialog.cancel();
		           }
		       });
		alert = builder.create();
		alert.show();
		
		
	}
	
	public void startSecondPhase(){
		
		currentPhase = PHASE_TWO;
		
		//start time/progress task -- 2nd phase
		secondPhase = (WorkoutTimeTask) new WorkoutTimeTask().execute(globalTimeLeft);
		
		
		Toast
        .makeText(FirstWorkout.this, "Continue at the indicated intensity...", Toast.LENGTH_LONG)
        .show();
		return;

	}
	
	class WorkoutTimeTask extends AsyncTask<Long, Long, Void> {
	    	
	    	
			@Override
	        protected Void doInBackground(Long... millis ) {
				
				long millisLeft= millis[0];
				long rightNow = System.currentTimeMillis();
				Long[] mLeft =  new Long[1];
				
				while(millisLeft > 0){
					while(System.currentTimeMillis() < rightNow+1000){
						// chill... for one thousand milliseconds 
					}
					millisLeft -= 1000;
					mLeft[0] = millisLeft;
					publishProgress(mLeft);
					if(isCancelled() == true){
		        		  break;
		        	  }
					rightNow = System.currentTimeMillis();
				}	
				return(null);
			}
			
			@SuppressWarnings("unchecked")
	        @Override
	        protected void onProgressUpdate(Long... m) {
	          
				long millLeft = m[0];
				//update global/total time left variable
				globalTimeLeft = millLeft;
				Log.d("globalTimeLeft:",""+ globalTimeLeft);
				//Convert milliseconds left to minutes and seconds left to show the user
				int secondsTotal = (int) millLeft / 1000;
				int seconds = secondsTotal % 60;
				int minutes = secondsTotal / 60;
				if(seconds > 9){
					timeRemaining.setText(minutes + ":" + seconds);
				}else{
					timeRemaining.setText(minutes + ":0" + seconds);
				}
				
				long millisElapsed = WORKOUT_DURATION[0] - millLeft;
				percentComplete = (double)millisElapsed /(double)WORKOUT_DURATION[0];
				
				//Log.d("percentComplete",percentComplete + "");
				
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
				//dependent on user's feedback after this point
				if (percentComplete > .5 && theValues[5] == 0 && currentPhase == PHASE_ONE){
					
		        	
		        	//theValues[5] = currentIntensity;
					
					
					this.cancel(true);
				}
				if (percentComplete > .5 && theValues[5] == 0 && currentPhase == PHASE_TWO){
					theValues[5] = currentIntensity;
				}
				if(percentComplete > .6 && theValues[6] == 0){
					theValues[6] = currentIntensity;
				}
				if(percentComplete > .7 && theValues[7] == 0){
					theValues[7] = currentIntensity;
				}
				//TODO: chill here for a bit
				Log.d("here", "");
				
					
					
				//change the color of the current bar each second
				if(theColor == DEFAULT_COLOR){
					theColor = Color.YELLOW; //for now at least...
				}else{
					theColor = DEFAULT_COLOR;
				}
				switchColor(theColor);
				
	        	
	        }
	        
	        @Override
	        protected void onPostExecute(Void unused) {
	        	
	        	return;
	
	        }
	        
	        protected void onCancelled(){
	        	
	        	//TODO: this is garbage. 
	        	if(currentPhase == PHASE_ONE){
	        		//TODO: Dummy HR -- insert HR code here
		        	int heartRate = 80;
		        	/********************/
	        		Toast.makeText(FirstWorkout.this, "First Phase Complete", Toast.LENGTH_LONG).show();
	        		finishedFirstPhase(heartRate);
	        	}
	        	
	        	
	        }
	}//end of WorkoutTimeTask		

}
