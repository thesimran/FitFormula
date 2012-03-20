package cscece.android.fitformula;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class HcFitness extends Activity {

	TextView myTextView;
	int gender;
	int age;
	int height;
	int weight;
	int hypertension;
	int smoking;
	int diabetes;
	int bloodPressure;
	
	double myBMI;
	double risk;
	double myCVDRisk;
	double heartAge;	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);        
        //TextView textview = new TextView(this);
        //textview.setText("This is the HcFitness tab");
        setContentView(R.layout.hc_fitness_layout);
        myTextView = (TextView) findViewById(R.id.do_fitness_test_note);
                
    }//end of onCreate
    
    @Override
    protected void onStart() {
        super.onStart();
     Log.d("db","onStart");
     getUserData();
    }
    
    public void getUserData (){
    	DatabaseHelper dbh;
		dbh = new DatabaseHelper(this);		
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
			int columnIndex = c.getColumnIndex(DatabaseHelper.gender);
			gender = c.getInt(columnIndex);
			Log.d("db", "gender " + gender);
			
			columnIndex = c.getColumnIndex(DatabaseHelper.age);
			age = c.getInt(columnIndex);
			Log.d("db", "age " + age);
			
			columnIndex = c.getColumnIndex(DatabaseHelper.height);
			height = c.getInt(columnIndex);
			Log.d("db", "height " + height);
			
			columnIndex = c.getColumnIndex(DatabaseHelper.weight);
			weight = c.getInt(columnIndex);
			Log.d("db", "weight " + weight);
			
			columnIndex = c.getColumnIndex(DatabaseHelper.hypertension);
			hypertension = c.getInt(columnIndex);
			Log.d("db", "hypertension " + hypertension);
			
			columnIndex = c.getColumnIndex(DatabaseHelper.smoking);
			smoking = c.getInt(columnIndex);
			Log.d("db", "smoking " + smoking);
			
			columnIndex = c.getColumnIndex(DatabaseHelper.diabetes);
			diabetes = c.getInt(columnIndex);
			Log.d("db", "diabetes " + diabetes);
						
			columnIndex = c.getColumnIndex(DatabaseHelper.bloodpressure);
			//bloodPressure = c.getInt(columnIndex);
			//******************************************REMOVE BP********************************************//
			bloodPressure= 130;
			Log.d("db", "bloodpressure " + bloodPressure);
			
			
			myBMI=weight/(height*height/10000); //kg/m^2
			
			risk= (Math.log(age)*3.11296)+(Math.log(bloodPressure)*1.85508)+(smoking*0.70953)+(Math.log(myBMI)*0.79277)+(diabetes*0.5316);
			myCVDRisk=(1 - Math.pow(0.88431,Math.exp(risk-23.9388)));
			//heartAge= (Math.exp(-myCVDRisk/3.11296)) / (Math.pow(-Math.log(0.8843),(1/3.11296))) * (Math.pow(-Math.log(1-myCVDRisk),(1/3.11296)));					
			 myTextView.setText("Age: "+age+"\nHeight: "+height+" cm\nWeight: "+weight+" kg\nBMI: "+String.format("%.1f",myBMI)+"\nRisk: "+String.format("%.2f",risk)+"\nCVD Risk: "+String.format("%.2f",100*myCVDRisk)+"%\n\nRecommended Workout Program:\nSlim & Trim");
			/*
			if(gender==0){ //male==0
				if (hypertension ==1){ //yes==1
					
				}else{ //no==0
					risk= (Math.log(age)*3.11296)+(Math.log(bloodPressure)*1.85508)+(smoking*0.70953)+(Math.log(myBMI)*0.79277)+(diabetes*0.5316);
					myCVDRisk=(1 - Math.pow(0.88431,Math.exp(risk-23.9388)));
					//heartAge= (Math.exp(-myCVDRisk/3.11296)) / (Math.pow(-Math.log(0.8843),(1/3.11296))) * (Math.pow(-Math.log(1-myCVDRisk),(1/3.11296)));					
					 myTextView.setText("Age: "+age+"\nHeight: "+height+" cm\nWeight: "+weight+" kg\nBMI: "+String.format("%.1f",myBMI)+"\nRisk: "+String.format("%.2f",risk)+"\nCVD Risk: "+String.format("%.2f",100*myCVDRisk)+"%");
				}
				
			}else{ //female==1
				if (hypertension ==1){ //yes==1
					
				}else{ //no==0
					
				}
			}*/
			
			
		}else{
	        myTextView.setText("You have not yet done a Fitness Test, therefore your Health Center contains no data.  Please do the Fitness Test and then select a workout to begin your journey towards ultimate fitness prowess.");
		}
		db.close();
    }
}
