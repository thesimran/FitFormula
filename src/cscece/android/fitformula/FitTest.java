package cscece.android.fitformula;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.VideoView;

public class FitTest extends Activity {

	private ProgressBar bar;
	private TextView bpmText;
	private int bpm;
	private  int finalBpm;
	private Camera cam;
	private Parameters p;
	private AlertDialog.Builder builder;
	private AlertDialog alertDialog;
	private View dialogLayout;
	private VideoView video;
	private MediaController ctlr;
	private Uri videoPath;
	
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
    	
    	/* TODO: Camera/flash code -- currently does not work
    	cam = Camera.open();
    	cam.startPreview();
    	p = cam.getParameters();
    	p.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
    	p.setFlashMode(Parameters.FLASH_MODE_TORCH);
    	cam.setParameters(p);
    	*/
    	
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
    	super.onPause();
    }//end of onPause
    
    public void onStop(){
    	//cam.release();
    	super.onStop();
    }// end of onStop
    
    public void onDestroy(){
    	//cam.release();
    	super.onDestroy();
    }
    
    /*
    @Override
    public void onBackPressed(){
    	
    	if(video.isPlaying()){
    		
    		//Start next step in fitness test!
    		
    		
    	}else{
    		super.onBackPressed();
    	}
    	
    }*/
    
    /*
     * This method is called when the user presses the 'Cancel' button
     * in the AlertDialog.  All this method does is finish the Activty, which brings the user back
     * to the FitnessTest form.
     */
    private void cancelSelected(){
    	this.finish();
    }
    
    private void showVideo(){
    	
    	

    	
    	//View curView = (View)findViewById(fit)
    	//not sure what this does..we'll find out soon I guess... 
    	getWindow().setFormat(PixelFormat.TRANSLUCENT); 
    	
    
    	
    	//videoPath = Uri.parse("android.resource://cscece/android/fitformula/" + R.raw.step_test);
        //videoPath= Uri.parse("http://www.ooklnet.com/files/368/368007/video.mp4");
    	videoPath = Uri.parse("android.resource://"+getPackageName()+"/"+ R.raw.step_test);	
    	//String thePath = videoPath.getPath();
    	Log.d("TEST",videoPath.toString());
   
    	setContentView(R.layout.hr_video_view);
    	video=(VideoView)findViewById(R.id.hr_video);
    	
    	//video=new VideoView(this);
    	ctlr=new MediaController(this); 
    	//ctlr.setMediaPlayer(video);
    	video.setMediaController(ctlr);
    	video.setVideoURI(videoPath);    	    	     	
    	//video.setVideoPath(thePath);

     	//LinearLayout container = (LinearLayout)findViewById(R.id.hr_video_view1);
        //container.addView(video);
        
    	
    	video.requestFocus(); 
    	video.start();
    	
    	Toast
        .makeText(FitTest.this, "Press the 'Back' button to skip the video." , Toast.LENGTH_LONG)
        .show();
    	
    }
    /*
    public void startVideoActivity (){
    	 Intent i = new Intent(this, FitTestStep2.class);
   		startActivity(i);
    }*/
    
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
        	  SystemClock.sleep(1); 
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
            .makeText(FitTest.this, "Done! BPM: " + bpm , Toast.LENGTH_LONG)
            .show();
          
          /*
           * At this point, we should take this final finalBpm value and use it later.
           * Now, we should show the step test video to the user before they actually do
           * the test.
           */
          
          //Now let's call the displayVideo() method to show the step-test instructional video to the user
          showVideo();     
          //startVideoActivity();
          
        }
    	
    }
}
