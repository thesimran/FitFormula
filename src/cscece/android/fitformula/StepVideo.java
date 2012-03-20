package cscece.android.fitformula;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.step_vid_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle);
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
		
		//Start Video!
		mVideoView.start();
		
		Toast
        .makeText(this, "Please observe the proper stepping motion." , Toast.LENGTH_LONG)
        .show();
		
		Toast
        .makeText(this, "Press the 'Back' button to skip the video." , Toast.LENGTH_LONG)
        .show();

	}
	
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
