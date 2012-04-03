package cscece.android.fitformula;

import android.app.Activity;
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
	private static final String[] achievements={
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
		}; //30 achievements so far
	/*TODO: More potential achievements
	 "Finished standard first workout"
	 
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
      
        grid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	selection.setText(achievements[position]);            	
                //Toast.makeText(HcAchievements.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
        
    }//end of onCreate   
    
    @Override
    protected void onResume(){
    	super.onResume();
    	    	
    	/*TODO: change images based on unlocked achievements
    	im.mThumbIds[position]=R.drawable.achievement_unlocked;
    	im.notifyDataSetChanged();*/
    }
}//end of HcAchievements activity

