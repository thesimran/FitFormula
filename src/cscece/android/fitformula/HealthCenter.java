package cscece.android.fitformula;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TextView;

public class HealthCenter extends TabActivity {
	private TabHost tabHost;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.health_center_layout);
        
        Resources res = getResources(); // Resource object to get Drawables
	    tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, HcFitness.class);
	    
	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("hc_fitness").setIndicator("Fitness\n\n\n")
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    
	 // Do the same for the other tabs
	    intent = new Intent().setClass(this, HcWeight.class);
	    spec = tabHost.newTabSpec("hc_weight").setIndicator("Weight")
	                  .setContent(intent);
	    tabHost.addTab(spec);
	  
	 // Do the same for the other tabs
	    intent = new Intent().setClass(this, HcHeartRate.class);
	    spec = tabHost.newTabSpec("hc_heart_rate").setIndicator("Heart Rate")
	                  .setContent(intent);
	    tabHost.addTab(spec);  
	    
	 // Do the same for the other tabs
	    intent = new Intent().setClass(this, HcAchievements.class);
	    spec = tabHost.newTabSpec("hc_achievements").setIndicator("Achievements")
	                  .setContent(intent);
	    tabHost.addTab(spec);
   
    }//end of onCreate
}
