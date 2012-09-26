package cscece.android.fitformula;

import cscece.android.fitformula.utils.Persistence;
import android.app.Application;
import android.util.Log;

public class FitFormulaApp extends Application {

	public static final String TAG = "FitFormula";
	
	private static FitFormulaApp instance;
	
	public static Persistence persistence;
	
	public FitFormulaApp() {
        instance = this;
      
    }
	
	@Override
    public void onCreate() {
       super.onCreate();
       Log.d(TAG, "APPLICATION FIT FORMULA HAS STARTED");
       init();
       
    }
	
	@Override
    public void onTerminate() {
       super.onTerminate();      
       Log.d(TAG, "APPLICATION FIT FORMULA HAS BEEN TERMINATED");
       
    }
	
	public static FitFormulaApp getInstance() {
		
    	return instance;
    }
	
	public void init() {
		
		persistence = new Persistence(this);
		
	}
	
}
