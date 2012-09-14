package cscece.android.fitformula.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Persistence {

	private Context mContext;
	
	private SharedPreferences shared_prefs;
	private SharedPreferences.Editor prefs_editor;
	
	public static final String KEY_HAS_COMPLETED_TEST = "HAS_COMPLETED_TEST";
	public static final String KEY_BIOMETRICS_UPDATED = "biometricsUpdated";
	public static final String KEY_WEIGHT_UPDATED = "weightUpdated";
	public static final String KEY_FIRST_PROGRAM = "firstProgram";
	public static final String KEY_ACHIEVEMENTS_UPDATED = "achievementsUpdated";
	
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
	
	public void setBiometricsUpdated(boolean updated){
		prefs_editor.putBoolean(KEY_BIOMETRICS_UPDATED, updated);
		prefs_editor.commit();
	}
	
	public boolean getBiometricsUpdated(){
		//returns false by default
		return shared_prefs.getBoolean(KEY_BIOMETRICS_UPDATED, false);
	}
	
	public void setWeightUpdated(boolean updated){
		prefs_editor.putBoolean(KEY_WEIGHT_UPDATED, updated);
		prefs_editor.commit();
	}
	
	public boolean getWeightUpdated(){
		//returns false by default
		return shared_prefs.getBoolean(KEY_WEIGHT_UPDATED, false);
	}
	
	public void setFirstProgram(boolean first){
		prefs_editor.putBoolean(KEY_FIRST_PROGRAM, first);
		prefs_editor.commit();
	}
	
	public boolean getFirstProgram(){
		//returns false by default
		return shared_prefs.getBoolean(KEY_FIRST_PROGRAM, false);
	}
	
	public void setAchievementsUpdated(boolean updated){	
		prefs_editor.putBoolean(KEY_ACHIEVEMENTS_UPDATED, updated);
		prefs_editor.commit();
	}
	
	public boolean getAchievementsUpdated(){
		//returns false by default
		return shared_prefs.getBoolean(KEY_ACHIEVEMENTS_UPDATED, false);
	}
}
