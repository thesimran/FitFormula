package cscece.android.fitformula;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;

public class FitTest extends Activity {

	private ProgressBar bar;
	private TextView bpmText;
	private int bpm; 
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.fit_test_step1);
        bar = (ProgressBar)findViewById(R.id.resting_heart_progress);
        bpmText = (TextView)findViewById(R.id.current_hr);
        bpm = 0;
    }//end of onCreate
	
    public void onResume(){
    	bar.setProgress(0);
    	new heartProgressTask().execute();
    	super.onResume();
    	
    }//end of onResume
    
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
        	  SystemClock.sleep(1000); 
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
          Toast
            .makeText(FitTest.this, "Done!", Toast.LENGTH_LONG)
            .show();
        }
    	
    }
}
