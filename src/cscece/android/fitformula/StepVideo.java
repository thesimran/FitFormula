package cscece.android.fitformula;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.MediaController;
import android.media.AudioManager;
import android.media.MediaPlayer;


public class StepVideo extends Activity implements MediaPlayer.OnCompletionListener {

	private VideoView mVideoView;
	private Uri videoPath;
	private AudioManager audio;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		long date;
    	//get the current date/time
    	date = System.currentTimeMillis();
		int myHR = 65;//TODO: debugging on Atrix -Simran -- getIntent().getExtras().getInt("hr");
    	//Save heart rate to DB
    	ContentValues values = new ContentValues();
		DatabaseHelper dbh;
		dbh = new DatabaseHelper(this);
		SQLiteDatabase db = dbh.getWritableDatabase();
    	values.put(DatabaseHelper.heartrate, myHR);
    	values.put(DatabaseHelper.date, date);
    	values.put(DatabaseHelper.resting, true);
    	db.insert(DatabaseHelper.HR_TABLE_NAME, null, values);

    	values.clear();		
    	int achieveNum=1;  //update achievement db to unlock achievement 1, (Got a resting heart rate)
    	values.put(DatabaseHelper.achieved, true);
		values.put(DatabaseHelper.date, System.currentTimeMillis());
		db.update(DatabaseHelper.ACHIEVEMENTS_TABLE_NAME, values, DatabaseHelper.achievementnumber+" = "
				+ achieveNum, null);
		
		db.close();
		
		SharedPreferences settings = getSharedPreferences(MyWorkout.PREFS_NAME, 0);
    	SharedPreferences.Editor editor = settings.edit();		
		editor.putBoolean("hrUpdated",true);
		editor.putBoolean("achievementsUpdated", true);
		editor.commit();
		
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.step_vid_layout);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle);
		mVideoView = (VideoView) findViewById(R.id.surface_view1);

		videoPath = Uri.parse("android.resource://"+getPackageName()+"/"+ R.raw.steps);		
        //videoPath= Uri.parse("http://www.ooklnet.com/files/368/368007/video.mp4");

		mVideoView.setVideoURI(videoPath);
		MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mediaController);
		mVideoView.requestFocus();
		mVideoView.setOnCompletionListener(this);
		
		//Turn Audio off
		audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		audio.setStreamMute(AudioManager.STREAM_MUSIC,true);
				
	}
	
	@Override
	public void onResume(){
    	super.onResume();

    	new AlertDialog.Builder(this)
		.setTitle("Step Test")
		.setMessage(R.string.st_inst_string)
		.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				// Start Video!
				mVideoView.start();

				Toast.makeText(StepVideo.this, "Please observe the proper stepping motion.",
						Toast.LENGTH_LONG).show();

				Toast.makeText(StepVideo.this, "Press the 'Back' button to skip the video.",
						Toast.LENGTH_LONG).show();
			}
		})
		.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int whichButton) {
				finish();
			}
		})
		.show();		
	}
	
	/*
	private void cancelSelected(){
    	finish();
    }*/
	
	@Override
	public void onPause(){
		
		mVideoView.pause();
		audio.setStreamMute(AudioManager.STREAM_MUSIC,false);
		super.onPause();
	}
	
	@Override
	public void onStop(){
		mVideoView.stopPlayback();
		audio.setStreamMute(AudioManager.STREAM_MUSIC,false);
		super.onStop();
	}
	
	@Override
	public void onDestroy(){
		mVideoView.stopPlayback();
		audio.setStreamMute(AudioManager.STREAM_MUSIC,false);
		super.onDestroy();
	}
	
	public void startStep2Activity(){
		
		Intent i = new Intent(this, FitTestStep2.class);
   		startActivity(i);
   		audio.setStreamMute(AudioManager.STREAM_MUSIC,false);
   		finish();
		
	}

	//Figure this out!!!!!
	public void onCompletion(MediaPlayer arg0) {
		mVideoView.stopPlayback();
		startStep2Activity();
		
		
	}
	
	
	@Override
	public void onBackPressed(){
		
		//go to next step
		startStep2Activity();

	}//end of onBackPressed
	
}
