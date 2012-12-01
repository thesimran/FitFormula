package cscece.android.fitformula;

import cscece.android.fitformula.utils.DatabaseHelper;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.TabHost.TabContentFactory;

public class HealthCenter extends TabActivity {
	private TabHost tabHost;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.health_center_layout);
        
        //Resources res = getResources(); // Resource object to get Drawables
	    tabHost = (TabHost) findViewById(android.R.id.tabhost);  // The activity TabHost
	    //TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab
	    
	    tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
	    intent = new Intent().setClass(this, HcFitness.class);
	    setupTab(new TextView(this), "Fitness",intent);
	    intent = new Intent().setClass(this, HcWeight.class);
	    setupTab(new TextView(this), "Weight", intent);
	    intent = new Intent().setClass(this, HcHeartRate.class);
	    setupTab(new TextView(this), "Heart Rate", intent);
	    intent = new Intent().setClass(this, HcAchievements.class);
	    setupTab(new TextView(this), "Achievements", intent);	    
	    
    }//end of onCreate
    
    private void setupTab(final View view, final String tag, Intent i) {
		View tabview = createTabView(tabHost.getContext(), tag);

		TabSpec setContent = tabHost.newTabSpec(tag).setIndicator(tabview).setContent(i);
				
		tabHost.addTab(setContent);

	}
    
    @Override
	public void onActivityResult(int requestCode,int resultCode,Intent data){
	     super.onActivityResult(requestCode, resultCode, data);
	     
	     Log.d("hr","healthcenter onActivityResult");
	     if(requestCode==HcHeartRate.HCHEARTRATE_REQUEST_CODE && resultCode==FitTestHR.RESULT_OK){
	    	int lastHeartRate = data.getIntExtra("hr", 200);
	    	 DatabaseHelper dbh= new DatabaseHelper(HealthCenter.this);
				ContentValues values = new ContentValues();
				SQLiteDatabase db = dbh.getWritableDatabase();						    
				values.clear();				
				values.put(DatabaseHelper.heartrate, lastHeartRate);
				values.put(DatabaseHelper.date, System.currentTimeMillis());
				values.put(DatabaseHelper.resting, true);
				db.insert(DatabaseHelper.HR_TABLE_NAME, null, values);

		    	values.clear();
				int achieveNum=1; //update achievement db to unlock achievement 1, (Got a resting heart rate)
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
		    	Toast.makeText(HealthCenter.this, "Heart Rate Saved", Toast.LENGTH_LONG).show();
		    	
	    	 new AlertDialog.Builder(getParent())
	 		.setTitle("Heart Rate Saved")
	 		.setMessage(Html.fromHtml("Your new resting heart rate of <i>"+lastHeartRate+" bpm</i> was saved."))
	 		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	 		    public void onClick(DialogInterface arg0, int arg1) {
	 		    	//Switch Tabs	 				
	 				//HealthCenter.this.switchTab(2); 				
	 		    }
	 		})	 		
	 		.show();  
	     }else{
	    	 Toast
	         .makeText(this, "Error getting heart rate", Toast.LENGTH_LONG)
	         .show();	    	 
	     }
	}

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabs_text);
		tv.setText(text);
		return view;
	}
    
	public void switchTab (int tab ){
		tabHost.setCurrentTab(tab);
	}
}
