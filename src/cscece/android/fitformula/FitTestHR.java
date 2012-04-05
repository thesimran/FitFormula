package cscece.android.fitformula;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;

import android.view.WindowManager;
import android.widget.Toast;
import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cscece.android.fitformula.FitTest.heartProgressTask;

public class FitTestHR extends Activity {

	private Preview mPreview;
	private DrawOnTop mDrawOnTop;
	Camera mCamera;
	int numberOfCameras;
	int cameraCurrentlyLocked;

	private AlertDialog.Builder builder;
	private AlertDialog alertDialog;
	private View dialogLayout;
	
	public static final int RESULT_OK=1;
	
	// The first rear facing camera
	int defaultCameraId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//Alert Dialog stuff
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        dialogLayout = inflater.inflate(R.layout.custom_hr_dialog,
                                       (ViewGroup) findViewById(R.id.layout_root));
        builder = new AlertDialog.Builder(this);

		// Create a RelativeLayout container that will hold a SurfaceView,
		// and set it as the content of our activity.
		mDrawOnTop = new DrawOnTop(this);
		mPreview = new Preview(this, mDrawOnTop);
		setContentView(mPreview);
		addContentView(mDrawOnTop, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		//mDrawOnTop.startReadingHR=true;
		// Find the total number of cameras available
		numberOfCameras = Camera.getNumberOfCameras();

		// Find the ID of the default camera
		CameraInfo cameraInfo = new CameraInfo();
		for (int i = 0; i < numberOfCameras; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
				defaultCameraId = i;
			} 
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
    	builder.setView(dialogLayout)
        .setCancelable(true)
        .setPositiveButton("Begin!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	mDrawOnTop.startReadingHR=true;
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	cancelSelected();
            }
        });
        alertDialog = builder.create();
    	alertDialog.show();
    							
    	// Open the default i.e. the first rear facing camera.
		mCamera = Camera.open();
		cameraCurrentlyLocked = defaultCameraId;
		mPreview.setCamera(mCamera);
	}

    private void cancelSelected(){
    	finish();
    }
	
	@Override
	protected void onPause() {
		super.onPause();

		// Because the Camera object is a shared resource, it's very
		// important to release it when the activity is paused.
		if (mCamera != null) {			
			mPreview.setCamera(null);
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		finish();
	}

}

// ----------------------------------------------------------------------

/**
 * A simple wrapper around a Camera and a SurfaceView that renders a centered
 * preview of the Camera to the surface. We need to center the SurfaceView
 * because not all devices have cameras that support preview sizes at the same
 * aspect ratio as the device's display.
 */
class Preview extends ViewGroup implements SurfaceHolder.Callback {
	private final String TAG = "Preview";

	SurfaceView mSurfaceView;
	SurfaceHolder mHolder;
	Size mPreviewSize;
	List<Size> mSupportedPreviewSizes;
	Camera mCamera;
	DrawOnTop mDrawOnTop;
	boolean mFinished;

	Preview(Context context, DrawOnTop drawOnTop) {
		super(context);
		mDrawOnTop = drawOnTop;
		mFinished = false;
		
		mSurfaceView = new SurfaceView(context);
		addView(mSurfaceView);

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = mSurfaceView.getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void setCamera(Camera camera) {
		mCamera = camera;
		if (mCamera != null) {
			mSupportedPreviewSizes = mCamera.getParameters()
					.getSupportedPreviewSizes();
			/*for (Size size : mSupportedPreviewSizes) {
				Log.d ("SIZE","width:"+size.width+"height:"+size.height);			
			}*/
			requestLayout();
		}
	}

	public void switchCamera(Camera camera) {
		setCamera(camera);
		try {
			camera.setPreviewDisplay(mHolder);

		} catch (IOException exception) {
			Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
		}
		Camera.Parameters parameters = camera.getParameters();
		parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
		parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH); 
		//parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);  
		// parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY );
		parameters.setExposureCompensation(0);		
		//parameters.setAutoExposureLock(true);
		// parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
		requestLayout();

		camera.setParameters(parameters);
		Log.d("TEST", "setParameters");

		/*
		 * camera.autoFocus(new AutoFocusCallback() { public void
		 * onAutoFocus(boolean success, Camera camera) { } });
		 */
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// We purposely disregard child measurements because act as a
		// wrapper to a SurfaceView that centers the camera preview instead
		// of stretching it.
		final int width = resolveSize(getSuggestedMinimumWidth(),
				widthMeasureSpec);
		final int height = resolveSize(getSuggestedMinimumHeight(),
				heightMeasureSpec);
		setMeasuredDimension(width, height);
				
		if (mSupportedPreviewSizes != null) {
			mPreviewSize=mSupportedPreviewSizes.get(0);
			for (Size size : mSupportedPreviewSizes) {
				if(size.width<mPreviewSize.width){ //getting smallest supported preview size
					mPreviewSize=size;
				}					
			}
			
		/*if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }*/
			Log.d("SIZE",""+mPreviewSize.width);		
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed && getChildCount() > 0) {
			final View child = getChildAt(0);

			final int width = r - l;
			final int height = b - t;

			int previewWidth = width;
			int previewHeight = height;
			if (mPreviewSize != null) {
				previewWidth = mPreviewSize.width;
				previewHeight = mPreviewSize.height;
			}

			// Center the child SurfaceView within the parent.
			if (width * previewHeight > height * previewWidth) {
				final int scaledChildWidth = previewWidth * height
						/ previewHeight;
				child.layout((width - scaledChildWidth) / 2, 0,
						(width + scaledChildWidth) / 2, height);
			} else {
				final int scaledChildHeight = previewHeight * width
						/ previewWidth;
				child.layout(0, (height - scaledChildHeight) / 2, width,
						(height + scaledChildHeight) / 2);
			}
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where
		// to draw.
		try {
			if (mCamera != null) {
				mCamera.setPreviewDisplay(holder);
				Log.d("TEST", "setPreviewDisplay");

				// Preview callback used whenever new viewfinder frame is
				// available
				mCamera.setPreviewCallback(new PreviewCallback() {
					public void onPreviewFrame(byte[] data, Camera camera) {
						if ((mDrawOnTop == null) || mFinished)
							return;

						if (mDrawOnTop.mBitmap == null) { 
							Log.d("TEST", "mdrawontop.mbitmap==null");
							// Initialize the draw-on-top companion
							Camera.Parameters params = camera.getParameters();
							mDrawOnTop.mImageWidth = params.getPreviewSize().width;
							mDrawOnTop.mImageHeight = params.getPreviewSize().height;
							mDrawOnTop.mBitmap = Bitmap.createBitmap(
									mDrawOnTop.mImageWidth,
									mDrawOnTop.mImageHeight,
									Bitmap.Config.RGB_565);
							mDrawOnTop.mRGBData = new int[mDrawOnTop.mImageWidth
									* mDrawOnTop.mImageHeight];
							mDrawOnTop.mYUVData = new byte[data.length];
						}

						// Pass YUV data to draw-on-top companion
						System.arraycopy(data, 0, mDrawOnTop.mYUVData, 0,
								data.length);
						mDrawOnTop.invalidate();
					}
				});
			}
		} catch (IOException exception) {
			Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
		Log.d("TEST","surfaceDestroyed");
		if (mCamera != null) {
			mFinished = true;
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();			
		}
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		//TODO: use this function
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// Now that the size is known, set up the camera parameters and begin
		// the preview.
		Camera.Parameters parameters = mCamera.getParameters();
		//parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
		parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
		//parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);  
		// parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY );
		parameters.setExposureCompensation(0);				
        
		Log.d("TEST","surfaceChanged");
		
		requestLayout();

		mCamera.setParameters(parameters);
		mCamera.startPreview();

		/*
		 * mCamera.autoFocus(new AutoFocusCallback() { public void
		 * onAutoFocus(boolean success, Camera camera) { } });
		 */
	}

}

class DrawOnTop extends View {
	Bitmap mBitmap;
	Paint mPaintBlack;
	Paint mPaintYellow;
	Paint mPaintRed;
	Paint mPaintGreen;
	Paint mPaintBlue;
	byte[] mYUVData;
	int[] mRGBData;
	int mImageWidth, mImageHeight;

	int[][] rgbData;
	ArrayList<Integer> pulseData= new ArrayList<Integer>();	
	boolean first=true;
	int sampleCounter=0;
	int readings=0;
	int offset=0;
	int nSignal=0;
	int fSignal=0;
	int[] Hxv; // these arrays are used in the digital filter
	int[] Hyv; // H for highpass, L for lowpass
	int[] Lxv;
	int[] Lyv;
	ArrayList<Long> beatRecord=new ArrayList<Long>();
	private final static int beatRecordLength = 10; 	
	private final static int pulseDataLength = 256;
	private final static int redMeanMin = 80;
	private final static int greenMeanMax = 30; 
	private final static int blueMeanMax = 20; 
	String imageMeanStr;
	int beatCount=0;
	int peak=0,trough=0;	
	long peakTime=0,lastCheck=0,lastPeakTime=0,rate=0;
	long beatsPerMinute=0;
	long toastTime=0;
	long avgTimePerBeat=0;
	long startTime;
	boolean peakReached=false,troughReached=false;
	boolean firstBeat=false, startBeat=false; 
	public boolean startReadingHR;
	Bitmap redHeart;
	
	public DrawOnTop(Context context) {
		super(context);

		mPaintBlack = new Paint();
		mPaintBlack.setStyle(Paint.Style.FILL);
		mPaintBlack.setColor(Color.BLACK);
		mPaintBlack.setTextSize(25);

		mPaintYellow = new Paint();
		mPaintYellow.setStyle(Paint.Style.FILL);
		mPaintYellow.setColor(Color.YELLOW);
		mPaintYellow.setTextSize(25);

		mPaintRed = new Paint();
		mPaintRed.setStyle(Paint.Style.FILL);
		mPaintRed.setColor(Color.RED);
		mPaintRed.setTextSize(25);

		mPaintGreen = new Paint();
		mPaintGreen.setStyle(Paint.Style.FILL);
		mPaintGreen.setColor(Color.GREEN);
		mPaintGreen.setTextSize(25);

		mPaintBlue = new Paint();
		mPaintBlue.setStyle(Paint.Style.FILL);
		mPaintBlue.setColor(Color.BLUE);
		mPaintBlue.setTextSize(25);

		mBitmap = null;
		mYUVData = null;
		mRGBData = null;
		
		redHeart = BitmapFactory.decodeResource(getResources(), R.drawable.redheart);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mBitmap != null && startReadingHR) {
			int canvasWidth = canvas.getWidth();
			int canvasHeight = canvas.getHeight();
			int newImageWidth = canvasWidth;
			int newImageHeight = canvasHeight; 
			int marginWidth = (canvasWidth - newImageWidth) / 2;
			
			// Convert from YUV to RGB
			decodeYUV420SP(mRGBData, mYUVData, mImageWidth, mImageHeight);	
			
			//w176 h144
			//w720 h480
			//Log.d("width","width"+mImageWidth+"height"+mImageHeight);
			int redMean=0,greenMean=0,blueMean=0;
			int pix;
			//vertically-centered line right side, 3/4 to end
			for (pix = ((mImageWidth*mImageHeight/2)+(mImageWidth*3/4)); pix<((mImageWidth*mImageHeight/2)+mImageWidth); pix ++) {
				redMean += (mRGBData[pix] >> 16) & 0xff;	//red
				greenMean += (mRGBData[pix] >> 8) & 0xff;		//green
	    		blueMean += mRGBData[pix] & 0xff;				//blue
			} // pix			
			redMean/=(mImageWidth/4);
			greenMean/=(mImageWidth/4);
			blueMean/=(mImageWidth/4);						
			
			//Log.d("RED",""+redMean);
			readings+=redMean;
			sampleCounter++;
			if(sampleCounter%10==0){
				offset=readings/10;
				readings=0;
			}
			nSignal=redMean-offset;
			
			Hxv=new int [4]; // these arrays are used in the digital filter
			Hyv=new int [4]; // H for highpass, L for lowpass
			Lxv=new int [4];
			Lyv=new int [4];
			
			if(first==true){
				pulseData.ensureCapacity(pulseDataLength);
				for (int i =0;i<pulseDataLength;i++){
					pulseData.add(0);				
				}
				beatRecord.ensureCapacity(beatRecordLength);
				for (int i =0;i<beatRecordLength;i++){
					beatRecord.add(System.nanoTime());				
				}
				for (int i=0; i<4; i++){
				    Lxv[i] = Lyv[i] = nSignal<<10;  // seed the lowpass filter
				    Hxv[i] = Hyv[i] = nSignal<<10;  // seed the highpass filter
				  }				
				first=false;
			}
					    
			// THIS IS THE BANDPASS FILTER. GENERATED AT www-users.cs.york.ac.uk/~fisher/mkfilter/trad.html
			//  BUTTERWORTH LOWPASS ORDER = 3; SAMPLERATE = 10/s; CORNER = 4Hz
			    Lxv[0] = Lxv[1]; Lxv[1] = Lxv[2]; Lxv[2] = Lxv[3];
			    //Lxv[3] = nSignal*540;    // insert the normalized data into the lowpass filter
			    Lxv[3] = nSignal*383;    // insert the normalized data into the lowpass filter
			    Lyv[0] = Lyv[1]; Lyv[1] = Lyv[2]; Lyv[2] = Lyv[3];
			    //Lyv[3] = ((Lxv[0] + Lxv[3]) + 3 * (Lxv[1] + Lxv[2])+ (-285  * Lyv[0]) + (-1211 * Lyv[1]) + (-1802 * Lyv[2]));
			    Lyv[3] = ((Lxv[0] + Lxv[3]) + 3 * (Lxv[1] + Lxv[2])+ (-141  * Lyv[0]) + (-713 * Lyv[1]) + (-1190 * Lyv[2]));
			//  Butterworth; Highpass; Order = 3; Sample Rate = 1mS; Corner = .8Hz
			    Hxv[0] = Hxv[1]; Hxv[1] = Hxv[2]; Hxv[2] = Hxv[3];
			    Hxv[3] = (int)(Lyv[3]*616); // insert lowpass result into highpass filter
			    Hyv[0] = Hyv[1]; Hyv[1] = Hyv[2]; Hyv[2] = Hyv[3];
			    Hyv[3] = ((Hxv[3]-Hxv[0]) + 3 * (Hxv[1] - Hxv[2])+ (370  * Hyv[0]) + (-1482  * Hyv[1]) + (2052 * Hyv[2]));
			    //Log.d("TEST","Hyv[3]:"+Hyv[3]);
			    fSignal = Hyv[3]>>18;  // result of highpass shift-scaled 4055/8110
			    //fSignal = Hyv[3];  // result of highpass shift-scaled 4055
			    //Log.d("Filter","Lxv"+Lxv[3]+" Lyv"+Lyv[3]+" Hxv"+Hxv[3]+" Hyv"+Hyv[3]+" fSignal"+fSignal);
				  // Log.d("Filter","fSignal"+fSignal+" redMean"+redMean);
				    Log.d("Filter","fSignal"+fSignal+" redMean"+redMean+" greenMean"+greenMean+" blueMean"+blueMean);

			//pulseData.add((float)redMean);
		    if(redMean<redMeanMin){
		    	pulseData.add(0);
				pulseData.remove(0);
			}
		    else{
		    	pulseData.add(fSignal);
		    	
				pulseData.remove(0);
		    }
			//Log.d("TEST","fSignal:"+fSignal+" redMean:"+redMean);
			
			// Draw red intensity
			float barMaxHeight = 600;
			float barWidth = ((float) newImageWidth) / 256;
			float barMarginHeight = 2;
			float scale=10;
			RectF barRect = new RectF();
			barRect.bottom = canvasHeight-50;
			barRect.left = marginWidth;
			barRect.right = barRect.left + barWidth;
			for (int bin = 0; bin < 256; bin++) {								
				barRect.top = barRect.bottom-pulseData.get(bin)*scale;
				//Log.d("TEST","barrect.top:"+barRect.top);
				if(barRect.top<=barMaxHeight){
					barRect.top=barRect.bottom-100;
				}				
				canvas.drawRect(barRect, mPaintBlack);
				barRect.top += barMarginHeight;
				canvas.drawRect(barRect, mPaintGreen);
				barRect.left += barWidth;
				barRect.right += barWidth;
			} // bin
			
						
			if(redMean<= redMeanMin || greenMean>=greenMeanMax || blueMean>=blueMeanMax){ //colours not consistant with finger on camera
				if((System.currentTimeMillis()-toastTime) >= 2000){ //show toast alert
					toastTime=System.currentTimeMillis();
					Toast 
					.makeText(getContext(), "Please place finger on camera", Toast.LENGTH_SHORT)
					.show();	
				}							
			}
			else{
				if (fSignal >= peak && troughReached==false){ // finding the moment when the peak is reached
					peak = fSignal;                        	
					peakReached=true;
				}
				else if(fSignal<peak && peakReached==true){ //finding the moment when declining from peak
					if(firstBeat==false){
						peakTime = System.nanoTime();
						startTime=System.currentTimeMillis();
						firstBeat=true;
					}						    
					else if (((System.nanoTime()-peakTime)/1000000)>=250){ //omitting HRs higher than 240 bpm
						if(((System.nanoTime()-peakTime)/1000000)<=1500){ //omitting HRs lower than 40 bpm
							beatCount++;
							//canvas.drawCircle(100, 100, 50, mPaintGreen);//draw green circle
				            canvas.drawBitmap(redHeart, 100, 80, null);
							if(beatCount>=3 && startBeat==false){ //omit first 2 beats as time for user to settle
								beatCount=1;
								rate += ((System.nanoTime()-peakTime)/1000000);								
								startBeat=true;
								Log.d("time","hit 3rd beat");

							}else if(startBeat==true){
								rate += ((System.nanoTime()-peakTime)/1000000);															
							}
							//Log.d("time",""+(System.nanoTime()-peakTime)/1000000);
						}									     					  				     
						peakTime = System.nanoTime();
					}
					trough = fSignal;								
					peakReached=false;				
				}
			
				if(fSignal<=trough){ //finding the moment when the trough is reached
					trough = fSignal;
					troughReached=true;
				}
				else if(fSignal>trough && troughReached==true){ //finding the moment when increasing from trough
					peak = fSignal;                        	
					//peakTime = System.nanoTime();
					troughReached=false;
					peakReached=true;
				}						           			
									
				if(beatCount>=1 && startBeat==true){
					avgTimePerBeat= rate/ beatCount;             // averaging time between beats
					beatsPerMinute = 60000/avgTimePerBeat;                // how many beats can fit into a minute?
				}
									
			}
				
			int timeElapsed= (int)((System.currentTimeMillis()-startTime)/1000);
			if(timeElapsed>=15 && startBeat){ //stop reading HR after 15 sec
				startReadingHR=false;
				doneHR((int)beatsPerMinute);
				//((Activity)getContext()).finish();
			}
			if(timeElapsed>=50){ //catching when startTime not initialized
				timeElapsed=0;
			}
			if (startBeat){
				imageMeanStr = "Time: "+timeElapsed+ "s Beats: " +beatCount+" Heart Rate: "+beatsPerMinute+ " beats/min";
			}else{
				imageMeanStr = "Time: "+timeElapsed+ "s Beats: 0 Heart Rate: "+beatsPerMinute+ " beats/min";
			}
			canvas.drawText(imageMeanStr, marginWidth + 10, 30, mPaintYellow);
									
		} // end if statement

		super.onDraw(canvas);

	} // end onDraw method
	
	public void doneHR(int myHR){
		final int thisHR=myHR;
		new AlertDialog.Builder(getContext())
				.setTitle("Heart Rate Complete")
				.setMessage(Html.fromHtml("Your resting heart rate is <u><b><i>"+myHR+" beats/minute</i></b></u>. If you would like to take " +
						"another reading, please click 'Retry'. Otherwise click 'Continue'"))
				.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						leavingHR(thisHR);
					}
				})
				.setNegativeButton("Retry",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int whichButton) {
						/* User clicked Cancel so do some stuff */
						beatCount=0;
						peak=0; trough=0;	
						peakTime=0; lastCheck=0; lastPeakTime=0; rate=0;
						beatsPerMinute=0;
						toastTime=0;
						avgTimePerBeat=0;
						startTime=0;
						peakReached=false; troughReached=false;
						firstBeat=false; startBeat=false;
						
						startReadingHR=true;						
					}
				})
				.show();
	}
	
	public void leavingHR(int myHR){
		String nextActivity = ((Activity) getContext()).getIntent().getExtras().getString("nextactivity");
		
		if(nextActivity.equals("FitTestStep2") || nextActivity.equals("HcHeartRate")){ //Taking HR from step test requires finish and HR sent back
			Log.d("hr","nextActivity");
			Intent i=new Intent();
			i.putExtra("hr",myHR);
			((Activity) getContext()).setResult(FitTestHR.RESULT_OK,i);
	        ((Activity) getContext()).finish();
		} 
		else {
			String className = getContext().getPackageName() + "."
					+ nextActivity;
			Log.d("intent", className);
			Class<?> c = null;
			if (className != null) {
				try {
					c = Class.forName(className);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			Intent in = new Intent(getContext(), c);
			in.putExtra("hr", myHR);
			getContext().startActivity(in);
			((Activity) getContext()).finish();
		}
	}
	
	public void rgbToDoubleArray(int[] rgb, int[][] rgbDoubleArray, int width,int height) {
		
		int heightCount=0;
		int widthCount=0;
		for (int i = 0; i < rgb.length; i++,widthCount++) {				
			rgbDoubleArray[heightCount][widthCount]=rgb[i];
			
			if(widthCount>=width-1){
				//Log.d("TEST","i:"+i+" heightCount:"+heightCount+" widthCount:"+widthCount);

				widthCount=-1;
				heightCount++;
			}	
		}
	}

	public double[] rgbToHsl(double r, double g, double b){
		
		double red = r;
		double green = g;
		double blue = b;
		double max = 0;
		double min = 0;
		double h = 0;
		double s = 0;
		double l = 0;
		double d;
		double[] hsl = new double[3];
		
		red /= 255;
		green /= 255;
		blue /= 255;
		
		double tempMax = Math.max(red, blue);
		max = Math.max(tempMax, green);
		
		double tempMin = Math.min(red, blue);
		min = Math.min(tempMin, green);
		
		h = (max + min) / 2;
		s = (max + min) / 2;
		l = (max + min) / 2;
		
		if( max == min){
			h = s = 0;
		}else{
			d = max - min;
			if(l > 0.5){
				s = d / (2 - max - min);
			}else{
				s = d / (max + min);
			}
			if(max == red){
			
				h = (green - blue) / d + (g < b ? 6 : 0);
			}else if(max == green){
				
				h = (blue - red) / d + 2;
			}else{
				
				h = (red - green) / d + 4;
			}
			h /= 6;					
		}
		
		hsl[0] = h;
		hsl[1] = s;
		hsl[2] = l;
		
		return hsl;
		
	}//end of rgbToHsL
	
	static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width,
			int height) {
		final int frameSize = width * height;

		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0)
					y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}				
				//Log.d("TEST",""+y);
				
				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);
				
				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;

				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
						| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}		
	}

	static public void decodeYUV420SPGrayscale(int[] rgb, byte[] yuv420sp,
			int width, int height) {
		final int frameSize = width * height;

		for (int pix = 0; pix < frameSize; pix++) {
			int pixVal = (0xff & ((int) yuv420sp[pix])) - 16;
			if (pixVal < 0)
				pixVal = 0;
			if (pixVal > 255)
				pixVal = 255;
			rgb[pix] = 0xff000000 | (pixVal << 16) | (pixVal << 8) | pixVal;
		} // pix
	}

	static public void calculateIntensityHistogram(int[] rgb, int[] histogram,
			int width, int height, int component) {
		for (int bin = 0; bin < 256; bin++) {
			histogram[bin] = 0;
		} // bin
		if (component == 0) // red
		{
			for (int pix = 0; pix < width * height; pix += 3) {
				int pixVal = (rgb[pix] >> 16) & 0xff;
				histogram[pixVal]++;
			} // pix
		} else if (component == 1) // green
		{
			for (int pix = 0; pix < width * height; pix += 3) {
				int pixVal = (rgb[pix] >> 8) & 0xff;
				histogram[pixVal]++;
			} // pix
		} else // blue
		{
			for (int pix = 0; pix < width * height; pix += 3) {
				int pixVal = rgb[pix] & 0xff;
				histogram[pixVal]++;
			} // pix
		}
	}
}