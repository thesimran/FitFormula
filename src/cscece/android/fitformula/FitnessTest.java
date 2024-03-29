package cscece.android.fitformula;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class FitnessTest extends Activity implements RadioGroup.OnCheckedChangeListener{
	
	//Constants
	public static final int MALE = 0;
	public static final int FEMALE = 1;
	public static final int YES = 1;
	public static final int NO = 0;
	public static final int MIN_AGE = 15;
	public static final int MAX_AGE = 69;
	
	public static final int RESULT_OK=1;
	
	//Private
	private RadioGroup genderRadio;
	private RadioGroup hyperRadio;
	private RadioGroup smokingRadio;
	private RadioGroup diabetesRadio;
	private RadioGroup bloodRadio;
	private RadioGroup hrRadio;
	private EditText ageText;
	private EditText heightText;
	private EditText weightText;
	private Spinner bloodSpinner;
	private EditText bloodEntry;
	private EditText hrEntry;
	private TextView bloodLabel;
	private TextView restingHrLabel;
	
	private static final String[] bloodRanges={"Low", "Normal", "High"};
	
	//Public
	public int gender;
	public int age;
	public int height;
	public int weight;
	public int hypertension;
	public int smoking;
	public int diabetes;
	public int bloodPressure;
	public int restingHr;
	private boolean takeHr;
	
	//Static to indicate that the test has just been completed!
	public static boolean testComplete = false;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.fitness_test_layout);
        Log.d("db","oncreate");
        init();
        
    }//end of onCreate
    
    private void init(){
    	//set title
        //FitFormula.title.setText("Fitness Test"); -- still a work in progress
        takeHr = false;
        //TODO: Pull biometric data from database if exists
        //Gender
        //no gender initially
        gender = 0;
        genderRadio = (RadioGroup)findViewById(R.id.gender_radio);
        
        //Age
        age = 0;
        ageText = (EditText)findViewById(R.id.age);
        
        //Height
        height = 0;
        heightText = (EditText)findViewById(R.id.height);
        
        //Weight
        weight = 0;
        weightText = (EditText)findViewById(R.id.weight);
        
        //Hypertension
        hypertension = 0;
        hyperRadio = (RadioGroup)findViewById(R.id.hypertension_radio);
        
        //Smoking
        smoking = 0;
        smokingRadio = (RadioGroup)findViewById(R.id.smoking_radio);
        
        //Diabetes
        diabetes = 0;
        diabetesRadio = (RadioGroup)findViewById(R.id.diabetes_radio);
        
        //Sys. Blood Pressure
        bloodPressure = 0;
        bloodRadio = (RadioGroup)findViewById(R.id.blood_radio);
        bloodRadio.setOnCheckedChangeListener(this);        
        bloodEntry=(EditText)findViewById(R.id.blood_entry);
        bloodLabel=(TextView)findViewById(R.id.blood_label_no);
        bloodSpinner= (Spinner)findViewById(R.id.blood_spinner);
        ArrayAdapter<String> aa=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,bloodRanges);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodSpinner.setAdapter(aa);
        bloodSpinner.setSelection(1);
        
        //Heart Rate
        restingHr = 0;
        restingHrLabel = (TextView)findViewById(R.id.resting_hr);
        hrRadio = (RadioGroup)findViewById(R.id.hr_radio);
        hrEntry = (EditText)findViewById(R.id.hr_entry);
        hrRadio.setOnCheckedChangeListener(this);
    }
    
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.blood_no:
			bloodLabel.setText(R.string.blood_string_no);
			bloodSpinner.setVisibility(View.VISIBLE);
			bloodEntry.setVisibility(View.GONE);
			//bloodSpinner.setLayoutParams(new TableRow.LayoutParams(0,LayoutParams.WRAP_CONTENT,0.5f));
			//bloodEntry.setLayoutParams(new TableRow.LayoutParams(0,LayoutParams.WRAP_CONTENT,0.0f));	
			break;
		case R.id.blood_yes:
			bloodLabel.setText(R.string.blood_string_yes);
			bloodSpinner.setVisibility(View.GONE);
			bloodEntry.setVisibility(View.VISIBLE);
			//bloodSpinner.setLayoutParams(new TableRow.LayoutParams(0,LayoutParams.WRAP_CONTENT,0.0f));
			//bloodEntry.setLayoutParams(new TableRow.LayoutParams(0,LayoutParams.WRAP_CONTENT,0.5f));			    
			break;
		case R.id.hr_no:
			takeHr = true;
			restingHrLabel.setVisibility(View.GONE);
			hrEntry.setVisibility(View.GONE);
			break;
		case R.id.hr_yes:
			takeHr = false;
			restingHrLabel.setVisibility(View.VISIBLE);
			hrEntry.setVisibility(View.VISIBLE);
			break;
		}
		
	}

    @Override
    public void onStart(){
    	super.onStart();
    	
    	DatabaseHelper dbh;
		dbh = new DatabaseHelper(this);		
		int data;
		int rowIndex = 1;
		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor c = db.query(DatabaseHelper.USER_TABLE_NAME, new String[] {
				DatabaseHelper.gender, DatabaseHelper.age,
				DatabaseHelper.height, DatabaseHelper.weight,
				DatabaseHelper.hypertension, DatabaseHelper.smoking,
				DatabaseHelper.diabetes, DatabaseHelper.bloodpressure },
				"_id = " + rowIndex, null, null, null, null);
		
		if (c.moveToNext()) { // table has data (rows) in it
			Log.d("db", "userinfo table not empty");
			// loading data from database
			int columnIndex = c.getColumnIndex(DatabaseHelper.gender);
			data = c.getInt(columnIndex);
			Log.d("db", "gender " + data);
			if(data==MALE){ //0
				genderRadio.check(R.id.male);
			}else{ //data==FEMALE //1
				genderRadio.check(R.id.female);}			
			
			columnIndex = c.getColumnIndex(DatabaseHelper.age);
			data = c.getInt(columnIndex);
			Log.d("db", "age " + data);
			ageText.setText(String.valueOf(data));					
			
			columnIndex = c.getColumnIndex(DatabaseHelper.height);
			data = c.getInt(columnIndex); 
			Log.d("db", "height " + data);
			heightText.setText(String.valueOf(data));
			
			columnIndex = c.getColumnIndex(DatabaseHelper.weight);
			data = c.getInt(columnIndex);
			Log.d("db", "weight " + data);
			weightText.setText(String.valueOf(data));
			
			columnIndex = c.getColumnIndex(DatabaseHelper.hypertension);
			data = c.getInt(columnIndex);
			Log.d("db", "hypertension " + data);
			if(data==YES){ //1
				hyperRadio.check(R.id.hyp_yes);
			}else{ //data==NO //0
				hyperRadio.check(R.id.hyp_no);}	

			columnIndex = c.getColumnIndex(DatabaseHelper.smoking);
			data = c.getInt(columnIndex);
			Log.d("db", "smoking " + data);
			if(data==YES){ //1
				smokingRadio.check(R.id.smoke_yes);
			}else{ //data==NO //0
				smokingRadio.check(R.id.smoke_no);}	
			
			columnIndex = c.getColumnIndex(DatabaseHelper.diabetes);
			data = c.getInt(columnIndex);
			Log.d("db", "diabetes " + data);
			if(data==YES){ //1
				diabetesRadio.check(R.id.diabetes_yes);
			}else{ //data==NO //0
				diabetesRadio.check(R.id.diabetes_no);}	
			
			columnIndex = c.getColumnIndex(DatabaseHelper.bloodpressure);
			data = c.getInt(columnIndex);
			Log.d("db", "bloodpressure " + data);			
			bloodRadio.check(R.id.blood_yes);
			bloodLabel.setText(R.string.blood_string_yes);
			bloodSpinner.setVisibility(View.GONE);
			bloodEntry.setVisibility(View.VISIBLE);
			bloodEntry.setText(String.valueOf(data));			
			
		}//else{ // table is empty			
		//}
		db.close();
    }
    
    @Override
    public void onResume(){
    	
    	if(testComplete){
    		testComplete = false;
    		//switch to HealthCenter Tab
    		FitFormula ParentActivity;
    		ParentActivity = (FitFormula) this.getParent();
    		ParentActivity.switchTab(2);
    	}
    	
    	super.onResume();
    }

    //Called when the "Start Fitness Test Now" button is pushed
    public void startTestPushed(View view){
    	//We now have to check all the user entered values to see
    	// if they comply. And then we can store them for later use in the step test
    	// and eventually into the workoutManager
    	
    	
    	//First lets collect the radio button selections
    	//Gender:
    	
    	if(genderRadio.getCheckedRadioButtonId() == R.id.male){
    		
    		gender = MALE;
    	}else{
    		gender = FEMALE;
    	}
    	
    	//Hypertension:
    	if(hyperRadio.getCheckedRadioButtonId() == R.id.hyp_yes){
    		
    		hypertension = YES;
    	}else{
    		hypertension = NO;
    	}
    	
    	//Smoking:
    	if(smokingRadio.getCheckedRadioButtonId() == R.id.smoke_yes){
    		
    		smoking = YES;
    	}else{
    		smoking = NO;
    	}
    
    	//Diabetes:
    	if(diabetesRadio.getCheckedRadioButtonId() == R.id.diabetes_yes){
    		
    		diabetes = YES;
    	}else{
    		diabetes = NO;
    	}    	    	
    	
    	//Commented out for now, for debuging purposes:
    	//Now lets get and check the numerical values from the EditTexts
    	//Age:
    	try{
    		age = Integer.parseInt(ageText.getText().toString());
    	}catch(Exception e){
    		
    		pleaseEnterTextAlert();
    		return;
    		
    	}
    	if(age < MIN_AGE || age > MAX_AGE ){
    		
    		age = 0;
    		Toast
            .makeText(this, "You must enter an age between 15 and 69 in order to continue.  Please try again.", Toast.LENGTH_LONG)
            .show();
    		return;
    	}
    	
    	//Sys. Blood Pressure:
    	if(bloodRadio.getCheckedRadioButtonId() == R.id.blood_yes){
    		try{
        		bloodPressure = Integer.parseInt(bloodEntry.getText().toString());
        	}catch(Exception e){        		
        		pleaseEnterTextAlert();
        		return;        		
        	}
    		
    	}else{ //User selects low, normal, or high BP
    		String myBP=bloodRanges[bloodSpinner.getSelectedItemPosition()];
    		if (age<35){
    			if (myBP.equalsIgnoreCase("Low")){
    				bloodPressure=115;
    			}else if (myBP.equalsIgnoreCase("Normal")){
    				bloodPressure=120;
    			}else{ //myBP.equalsIgnoreCase("High")
    				bloodPressure=140;
    			}    			
    		}else if (age>=35 && age<=49){
    			if (myBP.equalsIgnoreCase("Low")){
    				bloodPressure=125;
    			}else if (myBP.equalsIgnoreCase("Normal")){
    				bloodPressure=130;
    			}else{ //myBP.equalsIgnoreCase("High")
    				bloodPressure=150;
    			}
    		}else if (age>=50 && age<=69){
    			if (myBP.equalsIgnoreCase("Low")){
    				bloodPressure=130;
    			}else if (myBP.equalsIgnoreCase("Normal")){
    				bloodPressure=135;
    			}else{ //myBP.equalsIgnoreCase("High")
    				bloodPressure=155;
    			}
    		}else { //age>=70
    			if (myBP.equalsIgnoreCase("Low")){
    				bloodPressure=135;
    			}else if (myBP.equalsIgnoreCase("Normal")){
    				bloodPressure=140;
    			}else{ //myBP.equalsIgnoreCase("High")
    				bloodPressure=160;
    			}
    		}
    	}    	
    	
    	//Height:
    	try{
    		height = Integer.parseInt(heightText.getText().toString());
    	}catch(Exception e){
    		
    		pleaseEnterTextAlert();
    		return;
    		
    	}	
    	if(height == 0 ){
    		
    		
    		Toast
            .makeText(this, "You must enter a valid height. Please try again.", Toast.LENGTH_LONG)
            .show();
    		return;
    	}
    	
    	//Weight:
    	try{
    		weight = Integer.parseInt(weightText.getText().toString());
    	}catch(Exception e){
    		
    		pleaseEnterTextAlert();
    		return;
    		
    	}	
    	if(weight <= 0 ){    		    		
    		Toast
            .makeText(this, "You must enter a valid weight. Please try again.", Toast.LENGTH_LONG)
            .show();
    		return;
    	}
    	
    	//HR
    	if(!takeHr){
    		//Skip HRActivity and save value in DB
    		
    		try{
    			restingHr = Integer.parseInt(hrEntry.getText().toString());
    		}catch(Exception e){
    			pleaseEnterTextAlert();
    			return;
    		}
    		//TODO Are these good constraints?
    		if(restingHr < 30 || restingHr > 160){
    			Toast
                .makeText(this, "You must enter a valid resting HR. Please try again.", Toast.LENGTH_LONG)
                .show();
        		return;
    		}
    		
    		
    	}
    	
    	//Save biometrics into DB here! 
    	addUserInfoToDB();    	

    	//dismiss keyboard
    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(ageText.getWindowToken(), 0);
    	imm.hideSoftInputFromWindow(heightText.getWindowToken(), 0);
    	imm.hideSoftInputFromWindow(weightText.getWindowToken(), 0);
		
		SharedPreferences settings = getSharedPreferences(MyWorkout.PREFS_NAME, 0);
    	SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("biometricsUpdated", true);
		editor.putBoolean("weightUpdated",true);
		editor.putBoolean("firstProgram",true);
		editor.putBoolean("achievementsUpdated",true);
		editor.commit();	
    	
		if(takeHr){
	    	//Intent i = new Intent(this, FitTest.class);
			//Intent i = new Intent(this, StepVideo.class);
	    	Intent i = new Intent(this, FitTestHR.class); 
			i.putExtra("nextactivity", "StepVideo"); //Telling HR Class what is next activity
	    	startActivity(i);
		}else{
			//Manual HR entry, skip HRActivity
			//TODO All I have to do for now?
			Intent i=new Intent(this, FitTestStep2.class);
			startActivity(i);
		
		}
    }//end of startTestPushed
    
    public void pleaseEnterTextAlert(){
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("You must fill out all text fields in the form in order to continue with the test.  Please try again.")
    	       .setCancelable(true)
    	       .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
    }
    
	public void addUserInfoToDB() {
		DatabaseHelper dbh;
		dbh = new DatabaseHelper(this);
		ContentValues values = new ContentValues();
		int data;
		int rowIndex = 1;
		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor c = db.query(DatabaseHelper.USER_TABLE_NAME, new String[] {
				DatabaseHelper.gender, DatabaseHelper.age,
				DatabaseHelper.height, DatabaseHelper.weight,
				DatabaseHelper.hypertension, DatabaseHelper.smoking,
				DatabaseHelper.diabetes, DatabaseHelper.bloodpressure },
				"_id = " + rowIndex, null, null, null, null);

		if (c.moveToNext()) { // table has data (rows) in it
			Log.d("db", "userinfo table not empty");
			// checking if any database columns need updating
			int columnIndex = c.getColumnIndex(DatabaseHelper.gender);
			data = c.getInt(columnIndex);
			Log.d("db", "gender " + data);			
			if (!(data == gender)) {
				Log.d("db", "gender is different");
				values.put(DatabaseHelper.gender, gender);
			}
			columnIndex = c.getColumnIndex(DatabaseHelper.age);
			data = c.getInt(columnIndex);
			Log.d("db", "age " + data);
			if (!(data == age)) {
				Log.d("db", "age is different");
				values.put(DatabaseHelper.age, age);
			}
			columnIndex = c.getColumnIndex(DatabaseHelper.height);
			data = c.getInt(columnIndex);
			Log.d("db", "height " + data);
			if (!(data == height)) {
				Log.d("db", "height is different");
				values.put(DatabaseHelper.height, height);
			}
			columnIndex = c.getColumnIndex(DatabaseHelper.weight);
			data = c.getInt(columnIndex);
			Log.d("db", "weight " + data);
			if (!(data == weight)) {
				Log.d("db", "weight is different");
				values.put(DatabaseHelper.weight, weight);
			}
			columnIndex = c.getColumnIndex(DatabaseHelper.hypertension);
			data = c.getInt(columnIndex);
			Log.d("db", "hypertension " + data);
			if (!(data == hypertension)) {
				Log.d("db", "hypertension is different");
				values.put(DatabaseHelper.hypertension, hypertension);
			}
			columnIndex = c.getColumnIndex(DatabaseHelper.smoking);
			data = c.getInt(columnIndex);
			Log.d("db", "smoking " + data);
			if (!(data == smoking)) {
				Log.d("db", "smoking is different");
				values.put(DatabaseHelper.smoking, smoking);
			}
			columnIndex = c.getColumnIndex(DatabaseHelper.diabetes);
			data = c.getInt(columnIndex);
			Log.d("db", "diabetes " + data);
			if (!(data == diabetes)) {
				Log.d("db", "diabetes is different");
				values.put(DatabaseHelper.diabetes, diabetes);
			}
			columnIndex = c.getColumnIndex(DatabaseHelper.bloodpressure);
			data = c.getInt(columnIndex);
			Log.d("db", "bloodpressure " + data);
			if (!(data == bloodPressure)) {
				Log.d("db", "bloodpressure is different");
				values.put(DatabaseHelper.bloodpressure, bloodPressure);
			}
			if (values.size() >= 1) {
				Log.d("db", "values have updated");
				db = dbh.getWritableDatabase();
				db.update(DatabaseHelper.USER_TABLE_NAME, values, "_id = "
						+ rowIndex, null);
			}
		} else { // table is empty
			Log.d("db", "userinfo empty table");
			values.put(DatabaseHelper.gender, gender);
			values.put(DatabaseHelper.age, age);
			values.put(DatabaseHelper.height, height);
			values.put(DatabaseHelper.weight, weight);
			values.put(DatabaseHelper.hypertension, hypertension);
			values.put(DatabaseHelper.smoking, smoking);
			values.put(DatabaseHelper.diabetes, diabetes);
			values.put(DatabaseHelper.bloodpressure, bloodPressure);
			
			/*values.put(DatabaseHelper.bmi, 0);
			values.put(DatabaseHelper.bmiclass, "");
			values.put(DatabaseHelper.risk, 0); 
			values.put(DatabaseHelper.normalrisk, 0);
			values.put(DatabaseHelper.cvdrisk, 0);
			values.put(DatabaseHelper.normalcvdrisk, 0);
			values.put(DatabaseHelper.cvdriskclass, "");
			values.put(DatabaseHelper.heartage, 0);
			values.put(DatabaseHelper.vo2, 0);
			values.put(DatabaseHelper.vo2class, "");*/

			db = dbh.getWritableDatabase();
			db.insert(DatabaseHelper.USER_TABLE_NAME, null, values);
		}
		
		int achieveNum=0; //update achievement db to unlock achievement 0, (Entered biometrics)
		db = dbh.getWritableDatabase();		
		values.clear();
		values.put(DatabaseHelper.achieved, true);
		values.put(DatabaseHelper.date, System.currentTimeMillis());
		db.update(DatabaseHelper.ACHIEVEMENTS_TABLE_NAME, values, DatabaseHelper.achievementnumber+" = "
				+ achieveNum, null);
			
		values.clear();		//add weight and date to weightdata db
		values.put(DatabaseHelper.weight, weight);
		values.put(DatabaseHelper.date, System.currentTimeMillis());
		
		db.insert(DatabaseHelper.WEIGHT_TABLE_NAME, null, values);
		
		//Add HR value if supplied by user manually
		if(!takeHr){
			values.clear();
			values.put(DatabaseHelper.heartrate, restingHr);
			values.put(DatabaseHelper.resting, true);
			values.put(DatabaseHelper.date, System.currentTimeMillis());
			
			db.insert(DatabaseHelper.HR_TABLE_NAME, null, values);
		}	

		db.close();
	}	
}
