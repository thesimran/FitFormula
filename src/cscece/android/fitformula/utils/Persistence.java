package cscece.android.fitformula.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Persistence {

	private Context mContext;
	
	private SharedPreferences shared_prefs;
	private SharedPreferences.Editor prefs_editor;
	
	public static final String KEY_HAS_COMPLETED_TEST = "HAS_COMPLETED_TEST";
	
	public Persistence(Context context){
		mContext = context;
		shared_prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		prefs_editor = shared_prefs.edit();
	}
	
	public void setHasCompletedTest(boolean complete){
		prefs_editor.putBoolean(KEY_HAS_COMPLETED_TEST, complete);
		prefs_editor.commit();
	}
	
	public boolean getHasCompletedTest(){
		//returns false by default
		return shared_prefs.getBoolean(KEY_HAS_COMPLETED_TEST, false);
	}
}
