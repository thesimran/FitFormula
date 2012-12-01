package cscece.android.fitformula;

import com.markupartist.android.widget.ActionBar;

import com.markupartist.android.widget.ActionBar.Action;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PlanOverviewActivity extends Activity {

	//Constants
	public static final String KEY_VIEW = "VIEW"; 
	public static final	int	VIEW_OVERVIEW = 1;
	public static final	int	VIEW_INSTRUCTION = 2;
	
	private int whichView;
	
	@Override
	public void onCreate(Bundle icicle){
		super.onCreate(icicle);
		Intent viewIntent = getIntent();
		Bundle bund = viewIntent.getExtras();
		int v = bund.getInt(KEY_VIEW);
		whichView = v;
		init();
		if(v == VIEW_OVERVIEW){
			whichView = VIEW_OVERVIEW;
		}else{
			whichView = VIEW_INSTRUCTION;
		}
		setUpActionBar();
	}
	
	private void init(){
		if(whichView == VIEW_OVERVIEW){
			setContentView(R.layout.plan_overview_layout);
		}else{
			setContentView(R.layout.workout_inst_layout);
		}
	}
	
	private void setUpActionBar() {
    	
    	final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
    	if(whichView == VIEW_OVERVIEW){
    		actionBar.setTitle(getString(R.string.plan_overview_title));
    	}else{
    		actionBar.setTitle(getString(R.string.workout_inst_title));
    	}
        
    	final BackAction backAction = new BackAction(R.drawable.navigation_back);
    	actionBar.addBackAction(backAction);
    	
    }
	
	/**
	 * Listener for the 'See Workout' button
	 * 
	 * @param v - View
	 */
	public void seeWorkout(View v){
		//TODO Slide right transition?
		Intent i = new Intent(this, PlanOverviewActivity.class);
		i.putExtra(KEY_VIEW, VIEW_INSTRUCTION);
		startActivity(i);
	}
	
	/**
	 * Listener for the 'Next' button
	 * 
	 * @param v - View
	 */
	public void next(View v){
		//TODO - start workout!
	}
	
	/**
	 * This class allows us to utilize the 'Back' functionality of the ActionBar
	 * 
	 * @author fitzgeraldsimran
	 *
	 */
	public class BackAction implements Action {
    	
    	private int mDrawable;
    	
    	public BackAction(int resId){
    		mDrawable = resId;
    		
    	}

		@Override
		public void performAction(View view) {
			
			finish();
		}

		@Override
		public int getDrawable() {
			return mDrawable;
		}
    	
    }

}
