package cscece.android.fitformula;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;
import android.widget.MediaController;


public class FitTestStep2 extends Activity {

	private VideoView mVideoView;
	private Uri videoPath;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fit_test_step2);
		mVideoView = (VideoView) findViewById(R.id.surface_view1);

		videoPath = Uri.parse("android.resource://"+getPackageName()+"/"+ R.raw.step_test);		
        //videoPath= Uri.parse("http://www.ooklnet.com/files/368/368007/video.mp4");

		mVideoView.setVideoURI(videoPath);
		MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mediaController);
		mVideoView.requestFocus();
		mVideoView.start();
		
		

	}
	
	@Override
	public void onBackPressed(){
		
		if(mVideoView.isPlaying() == true){
			
			mVideoView.stopPlayback();
			
		}
		super.onBackPressed();

		
	}

}
