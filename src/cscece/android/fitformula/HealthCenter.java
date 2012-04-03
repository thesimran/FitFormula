package cscece.android.fitformula;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
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

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabs_text);
		tv.setText(text);
		return view;
	}
    
}
