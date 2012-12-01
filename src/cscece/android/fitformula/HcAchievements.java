package cscece.android.fitformula;

import java.util.ArrayList;

import cscece.android.fitformula.utils.DatabaseHelper;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.Toast;

public class HcAchievements extends Activity{

	private TextView selection;
	private GridView grid;
	private ImageAdapter im;
	/*private static final String[] achievements={
		"Entered biometrics",
		"Got a resting heart rate",
		"Finished the step test",
		"Got a workout schedule",
		"Completed 1 workout session",		
		"Completed 10 workout sessions",
		"Completed 100 workout sessions",
		"Completed 1000 workout sessions",
		"Completed 1 workout cycle",
		"Completed 10 workout cycles",
		"Completed 100 workout cycles",
		"Advanced a workout level",
		"Reached the maximum workout level (level 5)",
		"Completed a workout program",
		"Reduced cardiovascular risk by 1%",
		"Reduced cardiovascular risk by 2%",
		"Reduced cardiovascular risk by 5%",
		"Lost 1 kg",
		"Lost 2 kg",
		"Lost 5 kg",
		"Increased aerobic fitness & VO2 max by 0.5 ml/kg/min",
		"Increased aerobic fitness & VO2 max by 1 ml/kg/min",
		"Increased aerobic fitness & VO2 max by 2 ml/kg/min",
		"Worked out for 100 minutes",
		"Worked out for 500 minutes",
		"Worked out for 1000 minutes",
		"Worked out for 5000 minutes",
		"Reduced resting heart rate by 1",
		"Reduced resting heart rate by 2",
		"Reduced resting heart rate by 5"
		}; *///30 achievements so far
	
	private ArrayList<Integer> achievementNumber;
	private ArrayList<String> achievementDescription;
	private ArrayList<Boolean> achieved;
	private ArrayList<Long> dateAchieved;
	
	/*TODO: More potential achievements	 	 
	 */
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);      
        setContentView(R.layout.hc_achievements_layout);
        
        selection=(TextView)findViewById(R.id.selected_achievement);                               
        
        im=new ImageAdapter(this);
        grid=(GridView) findViewById(R.id.grid);
        grid.setAdapter(im); 
    }//end of onCreate       

    @Override
    protected void onStart(){
    	super.onStart();           
        
        getAchievementInfo();
		updateImages();
		
    	SharedPreferences settings = getSharedPreferences(MyWorkout.PREFS_NAME, 0);						
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("achievementsUpdated", false); // make achievementsUpdated false so don't reload achievements grid every time
		editor.commit();

        grid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	selection.setText(achievementDescription.get(position));            	
                //Toast.makeText(HcAchievements.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }//end of onStart
    
    @Override
    protected void onResume(){
    	super.onResume();
    	    	
    	SharedPreferences settings = getSharedPreferences(MyWorkout.PREFS_NAME, 0);						
		boolean achievementsUpdated = settings.getBoolean("achievementsUpdated", false);		

		if (achievementsUpdated) { // if user has unlocked new achievements, reload achievements grid
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("achievementsUpdated", false); // make achievementsUpdated false so don't reload achievements grid every time
			editor.commit();
			
			getAchievementInfo();
			updateImages();
		}    	
    	
    	/*TODO: link more achievements
    	im.mThumbIds[position]=R.drawable.achievement_unlocked;
    	im.notifyDataSetChanged();*/
    }//end of onResume
    
    public void getAchievementInfo(){
        DatabaseHelper dbh;
		dbh = new DatabaseHelper(this);		
		SQLiteDatabase db = dbh.getReadableDatabase();
		
		Cursor c = db.query(DatabaseHelper.ACHIEVEMENTS_TABLE_NAME, new String[] {
				DatabaseHelper.achievementnumber, DatabaseHelper.achievementdescription,DatabaseHelper.achieved,
				DatabaseHelper.date},
				null, null, null, null, null, null);

		achievementNumber = new ArrayList<Integer>();
		achievementDescription = new ArrayList<String>();
		achieved = new ArrayList<Boolean>();
		dateAchieved = new ArrayList<Long>();		
		
		while (c.moveToNext()) { // table has data (rows) in it			
			int columnIndex = c.getColumnIndex(DatabaseHelper.achievementnumber);
			achievementNumber.add(c.getInt(columnIndex));
			
			columnIndex = c.getColumnIndex(DatabaseHelper.achievementdescription);
			achievementDescription.add(c.getString(columnIndex));
			
			columnIndex = c.getColumnIndex(DatabaseHelper.achieved);
			achieved.add(c.getInt(columnIndex)>0);
			
			columnIndex = c.getColumnIndex(DatabaseHelper.date);
			dateAchieved.add(c.getLong(columnIndex));
		}
		db.close();
    }
    
    public void updateImages(){
    	for (int i=0; i<achieved.size(); i++){
    		if(achieved.get(i)){
    			im.mThumbIds[i]=R.drawable.achievement_unlocked;
    			im.notifyDataSetChanged();
    		}
    	}
    }
    
}//end of HcAchievements activity

