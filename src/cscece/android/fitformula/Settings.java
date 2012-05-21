package cscece.android.fitformula;

import java.util.ArrayList;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView; 
import android.widget.Toast;
//import android.provider.CalendarContract.Calendars;

public class Settings extends Activity //TODO: ICS version. implements AdapterView.OnItemSelectedListener
{
	
	/*TODO: ICS version
	private Spinner emailSpinner;
	private Spinner calendarSpinner;

	ArrayList<String> emailArray;
	ArrayList<String> calendarDisplayNameArray;
	ArrayList<String> calendarAccountNameArray;
	ArrayList<Long> calendarIDArray;	
	
	// Projection array. Creating indices for this array instead of doing
	  // dynamic lookups improves performance.
	  public static final String[] EVENT_PROJECTION = new String[] {
	    Calendars._ID,                           // 0
	    Calendars.ACCOUNT_NAME,                  // 1
	    Calendars.CALENDAR_DISPLAY_NAME          // 2
	  };

	  // The indices for the projection array above.
	  private static final int PROJECTION_ID_INDEX = 0;
	  private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
	  private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
	*/
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);              
        setContentView(R.layout.setting_layout);
   
        /*TODO:
         emailArray = new ArrayList<String>();
         

        Account[] accounts = AccountManager.get(this).getAccountsByType("com.google");
		for (Account account : accounts) {
		  // TODO: Check possibleEmail against an email regex or treat
		  // account.name as an email address only for certain account.type values.
		  String possibleEmail = account.name;
		  emailArray.add(possibleEmail);
		  Log.d("email",possibleEmail);
		}
		
		if(emailArray.size() >=1){
		 emailSpinner= (Spinner)findViewById(R.id.email_spinner);
	     ArrayAdapter<String> aa=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,emailArray);
	     aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     emailSpinner.setAdapter(aa);	
	     emailSpinner.setOnItemSelectedListener(this);
		}else{
			Toast 
			.makeText(this, "There are no Google accounts associated with this device. Please add a Google account to your device to get a workout schedule.", Toast.LENGTH_LONG)
			.show();
		}
	   */  
    }//end of onCreate

    /*TODO: ICS version
     
	public void onItemSelected(AdapterView<?> parent, View v, int position,long id) {
		Log.d("email","onItemSelected"+parent.getId());
		if(parent.getId() == R.id.email_spinner){
			if(emailArray.size() >=1){
				SharedPreferences settings = getSharedPreferences(MyWorkout.PREFS_NAME, 0);
		    	SharedPreferences.Editor editor = settings.edit();
				editor.putString("googleAccount", emailArray.get(position));
				editor.commit();
				
			    getCalendars(position);
			}
		}else{ //(parent.getId() == R.id.calendar_spinner)
			SharedPreferences settings = getSharedPreferences(MyWorkout.PREFS_NAME, 0);
	    	SharedPreferences.Editor editor = settings.edit();
	    	editor.putLong("calID", calendarIDArray.get(position));
	    	editor.putString("calDisplayName", calendarDisplayNameArray.get(position));
			editor.putString("calAccountName", calendarAccountNameArray.get(position));
			editor.commit();
		}		
	}

	public void onNothingSelected(AdapterView<?> parent) {
		
	}
    
    public void getCalendars (int position){
        calendarDisplayNameArray = new ArrayList<String>();
        calendarAccountNameArray = new ArrayList<String>();
        calendarIDArray = new ArrayList<Long>();
        
    	// Run query
	     Cursor cur = null;
	     ContentResolver cr = getContentResolver();
	     Uri uri = Calendars.CONTENT_URI;   
	     String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND (" 
	                             + Calendars.ACCOUNT_TYPE + " = ?))";
	     String[] selectionArgs = new String[] {emailArray.get(position), "com.google"}; 
	     // Submit the query and get a Cursor object back. 
	     cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
       
	     // Use the cursor to step through the returned records
	     while (cur.moveToNext()) {
	         long calID = 0;
	         String displayName = null;
	         String accountName = null;        
	           
	         // Get the field values
	         calID = cur.getLong(PROJECTION_ID_INDEX);
	         calendarIDArray.add(calID);
	         displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
	         calendarDisplayNameArray.add(displayName);
	         accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
	         calendarAccountNameArray.add(accountName);

			// Do something with the values...
			Log.d("email", "calID:" + calID + " dispName:" + displayName
					+ " accName:" + accountName);
		}

		if (calendarDisplayNameArray.size() >= 1) {
			calendarSpinner = (Spinner) findViewById(R.id.calendar_spinner);
			ArrayAdapter<String> aa = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item,
					calendarDisplayNameArray);
			aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			calendarSpinner.setAdapter(aa);
			calendarSpinner.setOnItemSelectedListener(this);

		} else {
			Toast.makeText(
					this,
					"There are no calendars on this Google account. Please add a calendar to this Google account to get a workout schedule.",
					Toast.LENGTH_LONG).show();
		}

	}
	*/

}
