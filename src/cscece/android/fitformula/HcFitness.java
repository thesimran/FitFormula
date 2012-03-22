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
	double normalRisk;
	double normalCVDRisk;
	double heartAge;	
	
	String bmiClass;
	String cvdRiskClass;
	String vo2Class;
	
	int program;
	String programName;
	
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
			determineHealthFactors(dbh);								
			determineHealthClass(dbh);
			determineProgram();

			myTextView.setText("Height: "+height+" cm\nWeight: "+weight+" kg\nBMI: "
					 +String.format("%.1f",myBMI)+"\nRisk/Normal Risk: "+String.format("%.2f",risk)+"/"+String.format("%.2f",normalRisk)
					 +"\nCVD Risk/Normal CVD Risk: "+String.format("%.2f",100*myCVDRisk)+"/"+String.format("%.2f",100*normalCVDRisk)
					 +"%\nHeart Age/Age: "+String.format("%.0f",heartAge)+"/"+age);
			
		}else{
	        myTextView.setText("You have not yet done a Fitness Test, therefore your Health Center contains no data.  Please do the Fitness Test and then select a workout to begin your journey towards ultimate fitness prowess.");
		}
		db.close();
    }
    
    public void determineProgram(){
    	boolean bmiProblem=false;
    	boolean cvdRiskProblem=false;
    	boolean vo2Problem=false;
    	
    	if (myBMI>28){
    		bmiProblem=true;
    	}
    	if (myCVDRisk>=0.10 || heartAge>=(age+5)){
    		cvdRiskProblem=true;
    	}
    	if (vo2Class.equals("average") || vo2Class.equals("below average") || vo2Class.equals("poor") || vo2Class.equals("very poor")){
    		vo2Problem=true;
    	}
    	
    	if (bmiProblem && !cvdRiskProblem && !vo2Problem){
    		//Program 1 - Slim & Trim
    		program = 1;
    		programName = "Slim & Trim";
    	}else if (bmiProblem && !cvdRiskProblem && vo2Problem){    		    		
    		//Program 1 - Fit & Tone
    		program = 1;
    		programName = "Fit & Tone";
    	}else if (!bmiProblem && !cvdRiskProblem && vo2Problem){
    		//Program 3 - Fit Boost
    		program = 3;
    		programName = "Fit Boost";
    	}else if (!bmiProblem && !cvdRiskProblem && !vo2Problem){    		
    		//Program 3 - Fit Ultimate
    		program = 3;
    		programName = "Fit Ultimate";
    	}else if (bmiProblem && cvdRiskProblem && !vo2Problem){    		
    		//Program 2 - Cardio Metabolic Boost
    		program = 2;
    		programName = "Cardio Metabolic Boost";
    	}else if (bmiProblem && cvdRiskProblem && vo2Problem){
    		//Program 2 - Health Makeover
    		program = 2;
    		programName = "Health Makeover";
    	}else if (!bmiProblem && cvdRiskProblem && vo2Problem){
    		//Program 2 - Fit Heart
    		program = 2;
    		programName = "Fit Heart";
    	}else { //(!bmiProblem && cvdRiskProblem && !vo2Problem)
    		//Program 2 - Cardiac Wellness
    		program = 2;
    		programName = "Cardiac Wellness";
    	}
    }
    
    public void determineHealthFactors(DatabaseHelper dbh){    					
		ContentValues values = new ContentValues();		
		int rowIndex = 1;
		SQLiteDatabase db = dbh.getWritableDatabase();		
   	
    	myBMI=weight/(height*height/10000); //kg/m^2					
		Log.d("db","gender"+gender+"hyp"+hypertension); 
		if(gender==0){ //male==0
			if (hypertension ==1){ //yes==1
				risk= (Math.log(age)*3.11296)+(Math.log(bloodPressure)*1.92672)+(smoking*0.70953)+(Math.log(myBMI)*0.79277)+(diabetes*0.5316);
				myCVDRisk=(1 - Math.pow(0.88431,Math.exp(risk-23.9388)));					
				
				double numerator= Math.exp(-((Math.log(bloodPressure)*1.85508)+(smoking*0.70953)+(Math.log(myBMI)*0.79277)+(diabetes*0.5316)-23.9388)/3.11296);					
				double denominator= Math.pow(-Math.log(0.8843),(1/3.11296));
				double term= Math.pow(-Math.log(1-myCVDRisk),(1/3.11296));
				//Log.d("db","num"+numerator+"denom"+denominator+"coeff"+coefficient);
				heartAge = numerator/denominator*term;
				 
			}else{ //no==0
				risk= (Math.log(age)*3.11296)+(Math.log(bloodPressure)*1.85508)+(smoking*0.70953)+(Math.log(myBMI)*0.79277)+(diabetes*0.5316);
				myCVDRisk=(1 - Math.pow(0.88431,Math.exp(risk-23.9388)));
				
				double numerator= Math.exp(-((Math.log(bloodPressure)*1.85508)+(smoking*0.70953)+(Math.log(myBMI)*0.79277)+(diabetes*0.5316)-23.9388)/3.11296);					
				double denominator= Math.pow(-Math.log(0.8843),(1/3.11296));
				double term= Math.pow(-Math.log(1-myCVDRisk),(1/3.11296));
				//Log.d("db","num"+numerator+"denom"+denominator+"coeff"+coefficient);
				heartAge = numerator/denominator*term;			
			}
			normalRisk= (Math.log(age)*3.11296) + (Math.log(125)*1.85508) + (Math.log(22.5)*0.79277);
			normalCVDRisk = 1-Math.pow(0.88431,Math.exp(normalRisk-23.9388));
			
		}else{ //female==1
			if (hypertension ==1){ //yes==1
				risk= (Math.log(age)*2.72107)+(Math.log(bloodPressure)*2.88267)+(smoking*0.61868)+(Math.log(myBMI)*0.51125)+(diabetes*0.77763);
				myCVDRisk=(1 - Math.pow(0.94833,Math.exp(risk-26.0145)));
				
				double numerator= Math.exp(-((Math.log(bloodPressure)*2.81291)+(smoking*0.61868)+(Math.log(myBMI)*0.51125)+(diabetes*0.77763)-26.0145)/2.72107);					
				double denominator= Math.pow(-Math.log(0.94833),(1/2.72107));
				double term= Math.pow(-Math.log(1-myCVDRisk),(1/2.72107));
				//Log.d("db","num"+numerator+"denom"+denominator+"coeff"+coefficient);
				heartAge = numerator/denominator*term;										 	
				 
			}else{ //no==0
				risk= (Math.log(age)*2.72107)+(Math.log(bloodPressure)*2.81291)+(smoking*0.61868)+(Math.log(myBMI)*0.51125)+(diabetes*0.77763);
				myCVDRisk=(1 - Math.pow(0.94833,Math.exp(risk-26.0145)));
				
				double numerator= Math.exp(-((Math.log(bloodPressure)*2.81291)+(smoking*0.61868)+(Math.log(myBMI)*0.51125)+(diabetes*0.77763)-26.0145)/2.72107);					
				double denominator= Math.pow(-Math.log(0.94833),(1/2.72107));
				double term= Math.pow(-Math.log(1-myCVDRisk),(1/2.72107));
				//Log.d("db","num"+numerator+"denom"+denominator+"coeff"+coefficient);
				heartAge = numerator/denominator*term;
									 
			}
			normalRisk= (Math.log(age)*2.72107) + (Math.log(125)*2.81291) + (Math.log(22.5)*0.51125);
			normalCVDRisk = 1-Math.pow(0.94833,Math.exp(normalRisk-26.0145));
			
		}	
		values.put(DatabaseHelper.bmi, myBMI);
		values.put(DatabaseHelper.risk, risk);
		values.put(DatabaseHelper.normalrisk, normalRisk);
		values.put(DatabaseHelper.cvdrisk, myCVDRisk);
		values.put(DatabaseHelper.normalcvdrisk, normalCVDRisk);
		values.put(DatabaseHelper.heartage, heartAge);
		db.update(DatabaseHelper.USER_TABLE_NAME, values, "_id = "
				+ rowIndex, null);
    }
    
	public void determineHealthClass(DatabaseHelper dbh) {
		ContentValues values = new ContentValues();		
		int rowIndex = 1;
		SQLiteDatabase db = dbh.getWritableDatabase();								
		
		//BMI
		if (myBMI < 18.5) {
			bmiClass="underweight";
		}else if (myBMI >= 18.5 && myBMI < 25) {
			bmiClass="normal weight";
		}else if (myBMI>= 25 && myBMI < 30){
			bmiClass="overweight";
		}else{ //myBMI>=30
			bmiClass="obese";
		}
		
		//CVD Risk
		if (myCVDRisk < 0.10){
			cvdRiskClass="low";
		}else if (myCVDRisk >= 0.10 && myCVDRisk < 0.20){
			cvdRiskClass="intermediate";
		}else { //myCVDRisk>=0.20
			cvdRiskClass="high";
		}
		
		//VO2
		double vo2=50;
		if (gender==0){ //male==0
			if (age>=15 && age<=25){
				if(vo2>60){
					vo2Class="excellent";
				}else if (vo2>=52 && vo2<=60){
					vo2Class="good";
				}else if (vo2>=47 && vo2<=51){
					vo2Class="above average";
				}else if (vo2>=42 && vo2<=46){
					vo2Class="average";
				}else if (vo2>=37 && vo2<=41){
					vo2Class="below average";
				}else if (vo2>=30 && vo2<=36){
					vo2Class="poor";
				}else{ //vo2<30
					vo2Class="very poor";
				}
			}else if (age>=26 && age<=35){
				if(vo2>56){
					vo2Class="excellent";
				}else if (vo2>=49 && vo2<=56){
					vo2Class="good";
				}else if (vo2>=43 && vo2<=48){
					vo2Class="above average";
				}else if (vo2>=40 && vo2<=42){
					vo2Class="average";
				}else if (vo2>=35 && vo2<=39){
					vo2Class="below average";
				}else if (vo2>=30 && vo2<=34){
					vo2Class="poor";
				}else{ //vo2<30
					vo2Class="very poor";
				}
			}else if (age>=36 && age<=45){
				if(vo2>51){
					vo2Class="excellent";
				}else if (vo2>=43 && vo2<=51){
					vo2Class="good";
				}else if (vo2>=39 && vo2<=42){
					vo2Class="above average";
				}else if (vo2>=35 && vo2<=38){
					vo2Class="average";
				}else if (vo2>=31 && vo2<=34){
					vo2Class="below average";
				}else if (vo2>=26 && vo2<=30){
					vo2Class="poor";
				}else{ //vo2<26
					vo2Class="very poor";
				}
			}else if (age>=46 && age<=55){
				if(vo2>45){
					vo2Class="excellent";
				}else if (vo2>=39 && vo2<=45){
					vo2Class="good";
				}else if (vo2>=36 && vo2<=38){
					vo2Class="above average";
				}else if (vo2>=32 && vo2<=35){
					vo2Class="average";
				}else if (vo2>=29 && vo2<=31){
					vo2Class="below average";
				}else if (vo2>=25 && vo2<=28){
					vo2Class="poor";
				}else{ //vo2<25
					vo2Class="very poor";
				}
			}else if (age>=56 && age<=65){
				if(vo2>41){
					vo2Class="excellent";
				}else if (vo2>=36 && vo2<=41){
					vo2Class="good";
				}else if (vo2>=32 && vo2<=35){
					vo2Class="above average";
				}else if (vo2>=30 && vo2<=31){
					vo2Class="average";
				}else if (vo2>=26 && vo2<=29){
					vo2Class="below average";
				}else if (vo2>=22 && vo2<=25){
					vo2Class="poor";
				}else{ //vo2<22
					vo2Class="very poor";
				}
			}else { //age>65
				if(vo2>37){
					vo2Class="excellent";
				}else if (vo2>=33 && vo2<=37){
					vo2Class="good";
				}else if (vo2>=29 && vo2<=32){
					vo2Class="above average";
				}else if (vo2>=26 && vo2<=28){
					vo2Class="average";
				}else if (vo2>=22 && vo2<=25){
					vo2Class="below average";
				}else if (vo2>=20 && vo2<=21){
					vo2Class="poor";
				}else{ //vo2<20
					vo2Class="very poor";
				}
			}
		} else{ //female==1
			if (age>=15 && age<=25){
				if(vo2>56){
					vo2Class="excellent";
				}else if (vo2>=47 && vo2<=56){
					vo2Class="good";
				}else if (vo2>=42 && vo2<=46){
					vo2Class="above average";
				}else if (vo2>=38 && vo2<=41){
					vo2Class="average";
				}else if (vo2>=33 && vo2<=37){
					vo2Class="below average";
				}else if (vo2>=28 && vo2<=32){
					vo2Class="poor";
				}else{ //vo2<28
					vo2Class="very poor";
				}
			}else if (age>=26 && age<=35){
				if(vo2>52){
					vo2Class="excellent";
				}else if (vo2>=45 && vo2<=52){
					vo2Class="good";
				}else if (vo2>=39 && vo2<=44){
					vo2Class="above average";
				}else if (vo2>=35 && vo2<=38){
					vo2Class="average";
				}else if (vo2>=31 && vo2<=34){
					vo2Class="below average";
				}else if (vo2>=26 && vo2<=30){
					vo2Class="poor";
				}else{ //vo2<26
					vo2Class="very poor";
				}
			}else if (age>=36 && age<=45){
				if(vo2>45){
					vo2Class="excellent";
				}else if (vo2>=38 && vo2<=45){
					vo2Class="good";
				}else if (vo2>=34 && vo2<=37){
					vo2Class="above average";
				}else if (vo2>=31 && vo2<=33){
					vo2Class="average";
				}else if (vo2>=27 && vo2<=30){
					vo2Class="below average";
				}else if (vo2>=22 && vo2<=26){
					vo2Class="poor";
				}else{ //vo2<22
					vo2Class="very poor";
				}
			}else if (age>=46 && age<=55){
				if(vo2>40){
					vo2Class="excellent";
				}else if (vo2>=34 && vo2<=40){
					vo2Class="good";
				}else if (vo2>=31 && vo2<=33){
					vo2Class="above average";
				}else if (vo2>=28 && vo2<=30){
					vo2Class="average";
				}else if (vo2>=25 && vo2<=27){
					vo2Class="below average";
				}else if (vo2>=20 && vo2<=24){
					vo2Class="poor";
				}else{ //vo2<20
					vo2Class="very poor";
				}
			}else if (age>=56 && age<=65){
				if(vo2>37){
					vo2Class="excellent";
				}else if (vo2>=32 && vo2<=37){
					vo2Class="good";
				}else if (vo2>=28 && vo2<=31){
					vo2Class="above average";
				}else if (vo2>=25 && vo2<=27){
					vo2Class="average";
				}else if (vo2>=22 && vo2<=24){
					vo2Class="below average";
				}else if (vo2>=18 && vo2<=21){
					vo2Class="poor";
				}else{ //vo2<18
					vo2Class="very poor";
				}
			}else { //age>65
				if(vo2>32){
					vo2Class="excellent";
				}else if (vo2>=28 && vo2<=32){
					vo2Class="good";
				}else if (vo2>=25 && vo2<=27){
					vo2Class="above average";
				}else if (vo2>=22 && vo2<=24){
					vo2Class="average";
				}else if (vo2>=19 && vo2<=21){
					vo2Class="below average";
				}else if (vo2>=17 && vo2<=18){
					vo2Class="poor";
				}else{ //vo2<17
					vo2Class="very poor";
				}
			}
		}
		Log.d("db","vo2: "+vo2Class);
		values.put(DatabaseHelper.bmiclass, bmiClass);
		values.put(DatabaseHelper.cvdriskclass, cvdRiskClass);
		values.put(DatabaseHelper.vo2class, vo2Class);		
		db.update(DatabaseHelper.USER_TABLE_NAME, values, "_id = "
				+ rowIndex, null);
	}
}
