package cscece.android.fitformula;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * This version of the HealthCenter is for the "research" version of the FF app.
 * There will not be multiple tabs like there was with the first version of the
 * HealthCenter.  This version, HealthCenter2 is much less detailed and more
 * straight forward.  The user does not really need a record of their program
 * history.  
 * 
 *  There are two possible views here:
 *  1. "Test History" -- where they can go back and see their past stats that 
 *  	spawned from that test and subsequent workouts.  But they cannot go back 
 *  	and resume that program,  they can go back and look that their "stats" though
 *  
 *  2. They will simply see their "Health Report".  This is the view if they have only 
 *  	completed one test so far.
 *  
 *  3. They have completed 0 tests, and the screen will simply inform them of such and
 *  	that they should go fill out the fitness test form!
 * 
 * @author the_simran
 *
 */
public class HealthCenter2 extends Activity implements OnItemClickListener{
	
	private boolean hasCompletedTest;
	private ListView mListView;

	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.health_center2_layout);
	
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		
		hasCompletedTest =  FitFormulaApp.persistence.getHasCompletedTest();
		if(hasCompletedTest){
			mListView = (ListView)findViewById(R.id.hc_listview);
			populateList();
		}else{
			//TODO tell the user that they still need to do a test.
			
			//TODO this may not work right...
			mListView = null;
		}
	}
	
	private void populateList(){
		mListView.setOnItemClickListener(this);
		//TODO this may be wrong..
		//mAdapter.bindView(mListView, this, mCursor);
		
		
	}
	
	public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
		
		
		
	}
	
	
}
