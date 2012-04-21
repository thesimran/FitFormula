package cscece.android.fitformula;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;


public class FitTest extends Activity {

	private ProgressBar bar;
	private TextView bpmText;
	private int bpm;
	private  int finalBpm;
	//private Camera cam;
	private Parameters p;
	private AlertDialog.Builder builder;
	private AlertDialog.Builder stepBuilder;
	private AlertDialog alertDialog;
	private View dialogLayout;
	private View stepDialogLayout;
	private DatabaseHelper db;
	//private VideoView video;
	//private MediaController ctlr;
	//private Uri videoPath;

	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.fit_test_step1);
        bar = (ProgressBar)findViewById(R.id.resting_heart_progress);
        bpmText = (TextView)findViewById(R.id.current_hr);
        bpm = 0;
        
        //Alert Dialog stuff
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        dialogLayout = inflater.inflate(R.layout.custom_hr_dialog,
                                       (ViewGroup) findViewById(R.id.layout_root));
        builder = new AlertDialog.Builder(this);
        
    }//end of onCreate
	
    public void onResume(){
    	bar.setProgress(0);
    	
    	
    	
    	builder.setView(dialogLayout)
        .setCancelable(true)
        .setPositiveButton("Begin!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	/*Toast
                .makeText(FitTest.this, "Begin!", Toast.LENGTH_LONG)
                .show();*/
            	new heartProgressTask().execute();
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	/*Toast
                .makeText(FitTest.this, "Cancel!", Toast.LENGTH_LONG)
                .show();*/
            	cancelSelected();
            }
        });
        alertDialog = builder.create();
    	alertDialog.show();
    	
    	
    	super.onResume();
    	
    }//end of onResume
    
    public void onPause(){
    	//cam.release();
    	try{
    		db.close();
    	}catch(Exception e){
    		
    	}
    	
    	super.onPause();
    	
    }//end of onPause
    
    public void onStop(){
    	//cam.release();
    	try{
    		db.close();
    	}catch(Exception e){
    		
    	}
    	super.onStop();
    	
    }// end of onStop
    
    public void onDestroy(){
    	//cam.release();
    	try{
    		db.close();
    	}catch(Exception e){
    		
    	}
    	
    	super.onDestroy();
    }
    
    
    /*
     * This method is called when the user presses the 'Cancel' button
     * in the AlertDialog.  All this method does is finish the Activty, which brings the user back
     * to the FitnessTest form.
     */
    private void cancelSelected(){
    	finish();
    }
   
    
    public void startVideoActivity (){
    	
    	long date;
    	//get the current date/time
    	date = System.currentTimeMillis();
    	
    	//Save to DB
    	ContentValues values = new ContentValues();
		DatabaseHelper dbh;
		dbh = new DatabaseHelper(this);
		SQLiteDatabase db = dbh.getWritableDatabase();
    	values.put(DatabaseHelper.heartrate, finalBpm);
    	values.put(DatabaseHelper.date, date);
    	db.insert(DatabaseHelper.HR_TABLE_NAME, null, values);
		values.clear();
		db.close();
		
		//lets go to the next step and finish() this Activity
    	Intent i = new Intent(this, StepVideo.class);
   		startActivity(i);
   		finish();
    }
    
    class heartProgressTask extends AsyncTask<Void, Integer, Void> {
    	
    	
		@Override
        protected Void doInBackground(Void... unused) {
			int second = 7;
          for(int i=0;i < 100; i+=7){
        	  /*The ProressBar will increment by 6.66% each second.
        	   * This is due to the fact that we only want this HR sensor to
        	   * run for 15 seconds max for this part of the Fitness Test.
        	   * For the active HR in step 3 (I think), we will want 10 seconds max! 
        	   */
        	  SystemClock.sleep(100); 
        	  publishProgress(second);
          }
          
          return(null);
        }
        
        @SuppressWarnings("unchecked")
        @Override
        protected void onProgressUpdate(Integer... progress) {
          
        	bar.incrementProgressBy(progress[0]);
        	bpm =(int)((Math.random() * 20) + 60);
        	//Log.d("TEST", "bpm: " + bpm);
        	String guydude = Integer.toString(bpm);
        	bpmText.setText(guydude);
        }
        
        @Override
        protected void onPostExecute(Void unused) {
        	
        	finalBpm = bpm;
        	
          Toast
            .makeText(FitTest.this, "Done! BPM: " + finalBpm , Toast.LENGTH_LONG)
            .show();
          
          /*
           * At this point, we should take this final finalBpm value and use it later.
           * Now, we should show the step test video to the user before they actually do
           * the test.
           */
          
          //Alert Dialog stuff
          LayoutInflater inflater = (LayoutInflater) FitTest.this.getSystemService(LAYOUT_INFLATER_SERVICE);
          stepDialogLayout = inflater.inflate(R.layout.custom_step,
                                         (ViewGroup) findViewById(R.id.layout_root));
          stepBuilder = new AlertDialog.Builder(FitTest.this);
          stepBuilder.setView(stepDialogLayout)
          .setCancelable(false)
          .setCancelable(true)
          .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
              	/*Go to video*/
            	 startVideoActivity(); 
              	
              }
          })
          .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
              	/*Peace*/
              	cancelSelected();
              }
          });
          alertDialog = stepBuilder.create();
      	  alertDialog.show();
          
          
        }
    	
    }
}
