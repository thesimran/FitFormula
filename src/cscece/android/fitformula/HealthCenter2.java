package cscece.android.fitformula;

import java.util.ArrayList;

import cscece.android.fitformula.adapters.HcAdapter;
import cscece.android.fitformula.objects.HcListItem;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
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
public class HealthCenter2 extends Activity implements OnItemClickListener, OnItemSelectedListener {
	
	private boolean hasCompletedTest;
	private ListView mListView;
	private HcAdapter listAdapter;
	private ArrayList<HcListItem> hcList;
	private int regularExercise;
	private int age;
	private int heartAge;
	private String bmiClass;
	private String cvdRiskClass;
	private Spinner hcSpinner;
	
	private static final String[] yesNo = {"Yes", "No"};
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.health_center2_layout);
		
		//init spinner yo
		hcSpinner= (Spinner)findViewById(R.id.hc_confirm_spinner);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, yesNo);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hcSpinner.setAdapter(aa);
        hcSpinner.setSelection(0);
	
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		
		hasCompletedTest =  FitFormulaApp.persistence.getHasCompletedTest();
		if(hasCompletedTest){
			mListView = (ListView)findViewById(R.id.hc_listview);
			populateList();
		}else{
			//TODO maybe make this a dialog box instead of a Toast...
			Toast.makeText(this, "You have not yet completed a Fitness Test.  You must do so in order to" +
					" get a health report.", Toast.LENGTH_LONG).show();
			
			//TODO this may not work right...
			mListView = null;
		}
	}
	
	
	@Override
	public void onPause(){
		super.onPause();
		//TODO this may not be necessary..
		//overridePendingTransition(R.anim.push_up_in, R.anim.keep_still);
	}
	
	private void populateList(){
		mListView.setOnItemClickListener(this);
        mListView.setOnItemSelectedListener(this);
        
        hcList = new ArrayList<HcListItem>();
        //get values from DB
        extractFromDB();
        //Fill hcListItems
        HcListItem physActLevel = new HcListItem(HcListItem.TYPE_PAL, regularExercise);
        hcList.add(physActLevel);
        HcListItem cvdRisk = new HcListItem(HcListItem.TYPE_CARDIO, cvdRiskClass, age, heartAge);
        hcList.add(cvdRisk);
        HcListItem bmi = new HcListItem(HcListItem.TYPE_BMI, bmiClass);
        hcList.add(bmi);
        
        listAdapter = new HcAdapter(hcList, this);
        mListView.setAdapter(listAdapter);
        mListView.setVisibility(View.VISIBLE);
	
	}
	
	private void extractFromDB(){
		DatabaseHelper dbh;
		dbh = new DatabaseHelper(this);
		int rowIndex = 1;
		int columnIndex = 0;
		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor c = db.query(DatabaseHelper.USER_TABLE_NAME, new String[] {
				DatabaseHelper.regularExercise, DatabaseHelper.age,
				DatabaseHelper.heartage, DatabaseHelper.bmiclass,
				DatabaseHelper.cvdriskclass },
				"_id = " + rowIndex, null, null, null, null);
		
		c.moveToFirst();
		
		columnIndex = c.getColumnIndex(DatabaseHelper.regularExercise);
		regularExercise = c.getInt(columnIndex);
		columnIndex = c.getColumnIndex(DatabaseHelper.age);
		age = c.getInt(columnIndex);
		columnIndex = c.getColumnIndex(DatabaseHelper.heartage);
		heartAge = c.getInt(columnIndex);
		columnIndex = c.getColumnIndex(DatabaseHelper.bmiclass);
		bmiClass = c.getString(columnIndex);
		columnIndex = c.getColumnIndex(DatabaseHelper.cvdriskclass);
		cvdRiskClass = c.getString(columnIndex);
		
		dbh.close();
		
	}
	
	//Listener for the "Get my Workout now" button
	public void getMyWorkout(View v){
		//TODO some shit
	}
	
	public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
		Intent i = null;
		if(pos == 0){
			//Physical Activity Level
			i = new Intent(this, PhysActLevel.class);
		}else /*if(pos == 1)*/{
			//Cardiovascular risk
			i =  new Intent(this, CardiovasRisk.class);
		}/*else{
			//i = new Intent(this, Bmi.class);
		}*/
		
		startActivity(i);
		//TODO transitions not working!
		//overridePendingTransition(R.anim.slide_to_left, R.anim.keep_still);
		overridePendingTransition(R.anim.push_up_in, R.anim.keep_still);
	}
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		
		
		//TODO Also do some stuff here...
		
		//ImageView img_arrow = (ImageView) view.findViewById(R.id.img_arrow);
		//img_arrow.forceLayout();
		
	}
	
	public void onNothingSelected(AdapterView<?> parent) {
		//nothing for now
	}
	
	
}
