package cscece.android.fitformula;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FitnessTest extends Activity {
	
	//Constants
	public final int MALE = 1;
	public final int FEMALE = 2;
	public final int YES = 3;
	public final int NO = 4;
	public final int MIN_AGE = 15;
	public final int MAX_AGE = 69;
	
	//Private
	private RadioGroup genderRadio;
	private RadioGroup hyperRadio;
	private RadioGroup smokingRadio;
	private RadioGroup diabetesRadio;
	private RadioGroup bloodRadio;
	private EditText ageText;
	private EditText heightText;
	private EditText weightText;
	
	
	//Public
	public int gender;
	public int age;
	public int height;
	public int weight;
	public int hypertension;
	public int smoking;
	public int diabetes;
	public int bloodPressure;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.fitness_test_layout);
        //set title
        //FitFormula.title.setText("Fitness Test"); -- still a work in progress
        
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
        
    }//end of onCreate

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
    	
    	//Sys. Blood Pressure:
    	if(bloodRadio.getCheckedRadioButtonId() == R.id.blood_yes){
    		
    		//TODO: If they know their blood pressure, then we actually want
    		//to get the actual value, not the constant YES.
    		
    	}else{
    		bloodPressure = NO;
    	}
    	
    	/*Commented out for now, for debuging purposes:
    	//Now lets get and check the numerical values from the EditTexts
    	//Age:
    	age = Integer.parseInt(ageText.getText().toString());
    	if(age < MIN_AGE || age > MAX_AGE){
    		
    		age = 0;
    		Toast
            .makeText(this, "You must enter an age between 15 and 96 in order to continue.  Please try again.", Toast.LENGTH_LONG)
            .show();
    		return;
    	}
    	
    	//Height:
    	height = Integer.parseInt(heightText.getText().toString());
    	if(height == 0){
    		
    		
    		Toast
            .makeText(this, "You must enter a valid height. Please try again.", Toast.LENGTH_LONG)
            .show();
    		return;
    	}
    	
    	//Weight:
    	weight = Integer.parseInt(weightText.getText().toString());
    	if(weight == 0){
    		
    		
    		Toast
            .makeText(this, "You must enter a valid weight. Please try again.", Toast.LENGTH_LONG)
            .show();
    		return;
    	}*/
    	
    	
    	//TODO: Save biometrics into DB here! 
    	Intent i = new Intent(this, FitTest.class);
    	startActivity(i);
    }//end of startTestPushed
}
