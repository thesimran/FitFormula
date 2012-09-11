package cscece.android.fitformula;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HcFitness extends Activity {

	private int gender;
	private int age;
	private int height;
	private int weight;
	private int hypertension;
	private int smoking;
	private int diabetes;
	private int bloodPressure;
	
	private double vo2;
	private double myBMI;
	private double risk;
	private double myCVDRisk;
	private double normalRisk;
	private double normalCVDRisk;
	private double heartAge;	
	
	private String bmiClass;
	private String cvdRiskClass;
	private String vo2Class;
	
	private int program;
	private int level;
	private String programName;
	private long gotProgramDate;
	
	private RelativeLayout mRelative;
	private View chartView;
	private TextView titleText;
	private TextView cvdText;
	private TextView bmiText;
	private TextView vo2Text;	
	private TextView programText;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);        
        //TextView textview = new TextView(this);
        //textview.setText("This is the HcFitness tab");
        setContentView(R.layout.hc_fitness_layout);
        titleText = (TextView) findViewById(R.id.do_fitness_test_note);
        //mLinear = (LinearLayout) findViewById(R.id.hc_linear);
        mRelative = (RelativeLayout) findViewById(R.id.hc_relative);
                
    }//end of onCreate
        
    
    @Override
    protected void onStart(){
    	super.onStart();
    	
    	SharedPreferences settings = getSharedPreferences(MyWorkout.PREFS_NAME, 0);						
		boolean gottenWorkout = settings.getBoolean("gottenWorkout", false);
		
		if(gottenWorkout){
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("biometricsUpdated", false); //make biometricsUpdated false so don't reload HC info every time
			editor.commit();	
			
			getUserData();
		}
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        SharedPreferences settings = getSharedPreferences(MyWorkout.PREFS_NAME, 0);						
		boolean gottenWorkout = settings.getBoolean("gottenWorkout", false);
		boolean biometricsUpdated = settings.getBoolean("biometricsUpdated", false);				    	
		
		if(gottenWorkout && biometricsUpdated){ //if user has got their workout and biometrics have been updated, reload HC info
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("biometricsUpdated", false); //make biometricsUpdated false so don't reload HC info every time
			editor.commit();	
			
			getUserData();
		}
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
				DatabaseHelper.diabetes, DatabaseHelper.bloodpressure, DatabaseHelper.vo2 },
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
			bloodPressure = c.getInt(columnIndex);
			Log.d("db", "bloodpressure " + bloodPressure);
			
			columnIndex = c.getColumnIndex(DatabaseHelper.vo2);
			vo2 = c.getDouble(columnIndex);
			Log.d("db", "vo2 " + vo2);	
			
			determineHealthFactors(dbh);								
			determineHealthClass(dbh);
			determineProgram(dbh);										
			
			drawFitnessLayout();       	        	    
			
		}else{
			titleText.setText(R.string.do_test_str);
		}
		db.close();
    }
    
    public void drawFitnessLayout(){
    	String[] titles = new String[] { "My CVD Risk", "Normal CVD Risk" };
	    List<double[]> values = new ArrayList<double[]>();
	    
	    String temp=String.format("%.2f",100*myCVDRisk);
	    double temp2=Double.parseDouble(temp);		    		   
	    values.add(new double[] { temp2 });
	    temp=String.format("%.2f",100*normalCVDRisk);
	    temp2=Double.parseDouble(temp);		    		    
	    values.add(new double[] { temp2 });
	    int[] colors = new int[] { Color.BLUE, Color.CYAN };
	    XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
	    double yMax=100;
	    if(myCVDRisk>=normalCVDRisk){
	    	yMax=(myCVDRisk*100)+(myCVDRisk*20);
	    }else{
	    	yMax=(normalCVDRisk*100)+(myCVDRisk*20);
	    }
	    setChartSettings(renderer, "My Cardiovascular Risk", "", "CVD Risk (%)", 0.5,
	        1.5, 0, yMax, Color.GRAY, Color.LTGRAY);
	    renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
	    renderer.getSeriesRendererAt(1).setDisplayChartValues(true);
	    renderer.setXLabels(0);
	    //renderer.setYLabels(5);
	    renderer.setXLabelsAlign(Align.LEFT);
	    renderer.setYLabelsAlign(Align.LEFT);
	    renderer.setChartValuesTextSize(18.0f);
	    //renderer.setChartValuesTextAlign(Align.LEFT);
	    //SimpleSeriesRenderer.setChartValuesTextSize(12.0f);
	    renderer.setPanEnabled(false, false);
	    // renderer.setZoomEnabled(false);
	    renderer.setZoomEnabled(false, false);
	    //renderer.setZoomRate(1.1f);
	    renderer.setBarSpacing(-0.85f);
	    
		
	    mRelative.removeAllViews();
	    
	    //Add the view!
        chartView = ChartFactory.getBarChartView(this, buildBarDataset(titles, values), renderer,Type.DEFAULT);
        chartView.setId(2); 
        	        
        //myTextView.setText(Html.fromHtml("<br><b><u><big>My Health Report - "+ DateFormat.getDateInstance().format(new Date())+"</big></u></b><br>"));
        titleText.setText(Html.fromHtml("<br><b><u><big>My Health Report - "+ DateFormat.getDateInstance(DateFormat.LONG).format(gotProgramDate)+"</big></u></b><br>"));
        titleText.setId(1);
		RelativeLayout.LayoutParams relParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);	        
        relParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        relParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mRelative.addView(titleText,relParams);	  
        
		/*myTextView.setText("Blood Pressure: "+bloodPressure+" mmHg\nHeight: "+height+" cm\nWeight: "+weight+" kg\nBMI: "
				 +String.format("%.1f",myBMI)+"\nRisk/Normal Risk: "+String.format("%.2f",risk)+"/"+String.format("%.2f",normalRisk)
				 +"\nCVD Risk/Normal CVD Risk: "+String.format("%.2f",100*myCVDRisk)+"/"+String.format("%.2f",100*normalCVDRisk)
				 +"%\nHeart Age/Age: "+String.format("%.0f",heartAge)+"/"+age);*/
			        
        //Log.d("count",""+mRelative.getChildCount());
		DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        int chartSize = (int)(width*0.8);
        Log.d("count","height"+height+"width"+width);	        
        relParams=new RelativeLayout.LayoutParams(chartSize,chartSize);	        
        relParams.addRule(RelativeLayout.BELOW,titleText.getId());
        relParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mRelative.addView(chartView,relParams);	        
        	   
        cvdText = new TextView(this);
        cvdText.setId(3);
        cvdText.setText(Html.fromHtml("<b><big>Cardiovascular Disease (CVD) Risk Score:</big></b><br>This score indicates the risk that you have for developing " +
        		"cardiovascular disease (eg. coronary death, " +
        		"myocardial infarction, stroke and heart failure) over the next 10 years. Your risk is <i><u><b>"+String.format("%.2f",100*myCVDRisk)+
        		"%</b></u></i>, which is considered <i><u><b>"+cvdRiskClass+"</b></u></i>. Your heart age is <i><u><b>"+String.format("%.0f",heartAge)+"</b></u></i>" +
        				", which reflects the age of your vascular system."+
        		"<br>Improving your dietary and exercise behaviour and quitting smoking will help lower your CVD risk score and heart age.<br>"));	        
        relParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);	        
        relParams.addRule(RelativeLayout.BELOW,chartView.getId());
        mRelative.addView(cvdText,relParams);	        
        
        bmiText=new TextView(this);
        bmiText.setId(4);
        String lessThan = "&#60;";
        String greaterThan ="&#62;";
        bmiText.setText(Html.fromHtml("<b><big>Body Mass Index (BMI):</big></b><br>BMI assesses your health risk based on your " +
        		"height and weight. Your current BMI is <i><u><b>"+String.format("%.1f",myBMI)+
        		"</b></u></i>, which is considered <i><u><b>"+bmiClass+"</b></u></i>. The risk of developing weight-related health problems is minimal " +
        				"when your BMI is between 18.5 to 24.9. A high BMI ("+greaterThan+"25 overweight or obese) is associated with increased risk " +
        				"of health problems such as diabetes, heart disease, high blood pressure, gallbladder disease and some forms of cancer. " +
        				"A low BMI ("+lessThan+"18.5, underweight) is associated with health problems such as osteoporosis, under nutrition and eating disorders. " +
        				"Note: BMI applies to adults 18 years and over with the exception of pregnant and lactating women. BMI measurement may " +
        				"not apply if you are an athlete who has a large amount of muscle mass.<br>"));	        
        relParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);	        
        relParams.addRule(RelativeLayout.BELOW,cvdText.getId());
        mRelative.addView(bmiText,relParams);
        
        vo2Text=new TextView(this);
        vo2Text.setId(5);
        vo2Text.setText(Html.fromHtml("<b><big>Aerobic Fitness:</big></b><br>Your predicted VO2 max is " +
        		"<i><u><b>"+String.format("%.1f",vo2)+
        		" ml/kg/min</b></u></i>, which is considered <i><u><b>"+vo2Class+"</b></u></i> relative to your age group. VO2 max refers " +
        				"to the maximum amount of oxygen" +
        				" that an individual can utilize during intense or maximal exercise. The more fit you are, the higher your VO2 max will be.<br>"));	        
        relParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);	        
        relParams.addRule(RelativeLayout.BELOW,bmiText.getId());
        mRelative.addView(vo2Text,relParams);
        
        programText=new TextView(this); 
        programText.setId(6);
        programText.setText(Html.fromHtml("<b><big>Recommended Workout:</big></b><br>According to your health and fitness assessment, we recommend the " +
        		"<i><u><b>"+programName+
        		"</b></u></i> Program <i><u><b>"+program+"</b></u></i> starting at level <i><u><b>"+level+"</b></u></i>.<br>"));	        
        relParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);	        
        relParams.addRule(RelativeLayout.BELOW,vo2Text.getId());
        mRelative.addView(programText,relParams);	 
        
        Button switchButton = new Button(this);
        switchButton.setId(7);
        switchButton.setText("Go to My Workout");
        switchButton.setOnClickListener(new Button.OnClickListener() {  
            public void onClick(View v)
                {
            	//	Switch Tabs
          			FitFormula ParentActivity;
          			ParentActivity = (FitFormula) HcFitness.this.getParent().getParent();
          			ParentActivity.switchTab(0);
          			//TODO: trigger schedule in My Workout?
                }
         });
        relParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);	        
        relParams.addRule(RelativeLayout.BELOW,programText.getId());
        relParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mRelative.addView(switchButton,relParams);	 
              
    }
    
    public void determineProgram(DatabaseHelper dbh){
    	boolean bmiProblem=false;
    	boolean cvdRiskProblem=false;
    	boolean vo2Problem=false;
    	//Determine Program    	
    	if (myBMI>28){
    		bmiProblem=true;
    	}
    	if (myCVDRisk>=0.10 || heartAge>=(age+5)){ 
    		cvdRiskProblem=true;
    	}
    	if (vo2Class.equals("below average") || vo2Class.equals("poor") || vo2Class.equals("very poor")){ //vo2Class.equals("average") || 
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
        //TODO: Program goals
    	
    	//Determine Level
    	if(vo2Class.equals("excellent")){
    		level=5;
    	}else if(vo2Class.equals("good")){
    		level=4;
    	}else if(vo2Class.equals("above average")){
    		level=3;
    	}else if(vo2Class.equals("average")){
    		level=2;
    	}else{ //(vo2Class.equals("below average") || vo2Class.equals("poor") || vo2Class.equals("very poor"))
    		level=1;
    	}
    		
    	
    	ContentValues values = new ContentValues();		
		int rowIndex = 1;
		SQLiteDatabase db = dbh.getWritableDatabase();		
		Log.d("db","programname: "+programName+" program:"+program+" level:"+level+" bmiProblem:"+bmiProblem+" cvdRiskProblem:"+cvdRiskProblem+" vo2Problem:"+vo2Problem);
		values.put(DatabaseHelper.program, program);
		values.put(DatabaseHelper.level, level);	
		values.put(DatabaseHelper.programname, programName);
		gotProgramDate = System.currentTimeMillis();
		values.put(DatabaseHelper.date, gotProgramDate);
		db.update(DatabaseHelper.USER_TABLE_NAME, values, "_id = "
				+ rowIndex, null);
		db.close();
    }
    
    public void determineHealthFactors(DatabaseHelper dbh){    					
		ContentValues values = new ContentValues();		
		int rowIndex = 1;
		SQLiteDatabase db = dbh.getWritableDatabase();		
   	
    	myBMI=weight/(height*height/10000.0); //kg/m^2					
		Log.d("db","gender"+gender+"hyp"+hypertension+"myBMI"+myBMI+"weight"+weight+"height"+height); 
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
		Log.d("db","mycvdrisk"+myCVDRisk+"myBMI"+myBMI);
		values.put(DatabaseHelper.bmi, myBMI);
		values.put(DatabaseHelper.risk, risk);
		values.put(DatabaseHelper.normalrisk, normalRisk);
		values.put(DatabaseHelper.cvdrisk, myCVDRisk);
		values.put(DatabaseHelper.normalcvdrisk, normalCVDRisk);
		values.put(DatabaseHelper.heartage, heartAge);
		db.update(DatabaseHelper.USER_TABLE_NAME, values, "_id = "
				+ rowIndex, null);
		db.close();
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
			cvdRiskClass="moderate";
		}else { //myCVDRisk>=0.20
			cvdRiskClass="high";
		}
		
		//VO2
		if (gender==0){ //male==0
			if (age>=15 && age<=25){
				if(vo2>60){
					vo2Class="excellent";
				}else if (vo2>51 && vo2<=60){
					vo2Class="good";
				}else if (vo2>46 && vo2<=51){
					vo2Class="above average";
				}else if (vo2>41 && vo2<=46){
					vo2Class="average";
				}else if (vo2>36 && vo2<=41){
					vo2Class="below average";
				}else if (vo2>=30 && vo2<=36){
					vo2Class="poor";
				}else{ //vo2<30
					vo2Class="very poor";
				}
			}else if (age>=26 && age<=35){
				if(vo2>56){
					vo2Class="excellent";
				}else if (vo2>48 && vo2<=56){
					vo2Class="good";
				}else if (vo2>42 && vo2<=48){
					vo2Class="above average";
				}else if (vo2>39 && vo2<=42){
					vo2Class="average";
				}else if (vo2>34 && vo2<=39){
					vo2Class="below average";
				}else if (vo2>=30 && vo2<=34){
					vo2Class="poor";
				}else{ //vo2<30
					vo2Class="very poor";
				}
			}else if (age>=36 && age<=45){
				if(vo2>51){
					vo2Class="excellent";
				}else if (vo2>42 && vo2<=51){
					vo2Class="good";
				}else if (vo2>38 && vo2<=42){
					vo2Class="above average";
				}else if (vo2>34 && vo2<=38){
					vo2Class="average";
				}else if (vo2>30 && vo2<=34){
					vo2Class="below average";
				}else if (vo2>=26 && vo2<=30){
					vo2Class="poor";
				}else{ //vo2<26
					vo2Class="very poor";
				}
			}else if (age>=46 && age<=55){
				if(vo2>45){
					vo2Class="excellent";
				}else if (vo2>38 && vo2<=45){
					vo2Class="good";
				}else if (vo2>35 && vo2<=38){
					vo2Class="above average";
				}else if (vo2>31 && vo2<=35){
					vo2Class="average";
				}else if (vo2>28 && vo2<=31){
					vo2Class="below average";
				}else if (vo2>=25 && vo2<=28){
					vo2Class="poor";
				}else{ //vo2<25
					vo2Class="very poor";
				}
			}else if (age>=56 && age<=65){
				if(vo2>41){
					vo2Class="excellent";
				}else if (vo2>35 && vo2<=41){
					vo2Class="good";
				}else if (vo2>31 && vo2<=35){
					vo2Class="above average";
				}else if (vo2>29 && vo2<=31){
					vo2Class="average";
				}else if (vo2>25 && vo2<=29){
					vo2Class="below average";
				}else if (vo2>=22 && vo2<=25){
					vo2Class="poor";
				}else{ //vo2<22
					vo2Class="very poor";
				}
			}else { //age>65
				if(vo2>37){
					vo2Class="excellent";
				}else if (vo2>32 && vo2<=37){
					vo2Class="good";
				}else if (vo2>28 && vo2<=32){
					vo2Class="above average";
				}else if (vo2>25 && vo2<=28){
					vo2Class="average";
				}else if (vo2>21 && vo2<=25){
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
				}else if (vo2>46 && vo2<=56){
					vo2Class="good";
				}else if (vo2>41 && vo2<=46){
					vo2Class="above average";
				}else if (vo2>37 && vo2<=41){
					vo2Class="average";
				}else if (vo2>32 && vo2<=37){
					vo2Class="below average";
				}else if (vo2>=28 && vo2<=32){
					vo2Class="poor";
				}else{ //vo2<28
					vo2Class="very poor";
				}
			}else if (age>=26 && age<=35){
				if(vo2>52){
					vo2Class="excellent";
				}else if (vo2>44 && vo2<=52){
					vo2Class="good";
				}else if (vo2>38 && vo2<=44){
					vo2Class="above average";
				}else if (vo2>34 && vo2<=38){
					vo2Class="average";
				}else if (vo2>30 && vo2<=34){
					vo2Class="below average";
				}else if (vo2>=26 && vo2<=30){
					vo2Class="poor";
				}else{ //vo2<26
					vo2Class="very poor";
				}
			}else if (age>=36 && age<=45){
				if(vo2>45){
					vo2Class="excellent";
				}else if (vo2>37 && vo2<=45){
					vo2Class="good";
				}else if (vo2>33 && vo2<=37){
					vo2Class="above average";
				}else if (vo2>30 && vo2<=33){
					vo2Class="average";
				}else if (vo2>26 && vo2<=30){
					vo2Class="below average";
				}else if (vo2>=22 && vo2<=26){
					vo2Class="poor";
				}else{ //vo2<22
					vo2Class="very poor";
				}
			}else if (age>=46 && age<=55){
				if(vo2>40){
					vo2Class="excellent";
				}else if (vo2>33 && vo2<=40){
					vo2Class="good";
				}else if (vo2>30 && vo2<=33){
					vo2Class="above average";
				}else if (vo2>27 && vo2<=30){
					vo2Class="average";
				}else if (vo2>24 && vo2<=27){
					vo2Class="below average";
				}else if (vo2>=20 && vo2<=24){
					vo2Class="poor";
				}else{ //vo2<20
					vo2Class="very poor";
				}
			}else if (age>=56 && age<=65){
				if(vo2>37){
					vo2Class="excellent";
				}else if (vo2>31 && vo2<=37){
					vo2Class="good";
				}else if (vo2>27 && vo2<=31){
					vo2Class="above average";
				}else if (vo2>24 && vo2<=27){
					vo2Class="average";
				}else if (vo2>21 && vo2<=24){
					vo2Class="below average";
				}else if (vo2>=18 && vo2<=21){
					vo2Class="poor";
				}else{ //vo2<18
					vo2Class="very poor";
				}
			}else { //age>65
				if(vo2>32){
					vo2Class="excellent";
				}else if (vo2>27 && vo2<=32){
					vo2Class="good";
				}else if (vo2>24 && vo2<=27){
					vo2Class="above average";
				}else if (vo2>21 && vo2<=24){
					vo2Class="average";
				}else if (vo2>18 && vo2<=21){
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
		db.close();
	}
	
	/*TODO: All this code may have to be moved into its own class.  Right now, I dont want to deal
     * with the multiple inheritance problem im having.  I want to extend Activity (obviously), but I also want 
     * to grab these methods below, and I'm definitely gonna need them in multiple activities...
     */
    
    
    /**
     * Builds an XY multiple dataset using the provided values.
     * 
     * @param titles the series titles
     * @param xValues the values for the X axis
     * @param yValues the values for the Y axis
     * @return the XY multiple dataset
     */
    protected XYMultipleSeriesDataset buildDataset(String[] titles, List<double[]> xValues,
        List<double[]> yValues) {
      XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
      addXYSeries(dataset, titles, xValues, yValues, 0);
      return dataset;
    }

    public void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles, List<double[]> xValues,
        List<double[]> yValues, int scale) {
      int length = titles.length;
      for (int i = 0; i < length; i++) {
        XYSeries series = new XYSeries(titles[i], scale);
        double[] xV = xValues.get(i);
        double[] yV = yValues.get(i);
        int seriesLength = xV.length;
        for (int k = 0; k < seriesLength; k++) {
          series.add(xV[k], yV[k]);
        }
        dataset.addSeries(series);
      }
    }

    /**
     * Builds an XY multiple series renderer.
     * 
     * @param colors the series rendering colors
     * @param styles the series point styles
     * @return the XY multiple series renderers
     */
    protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
      XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
      setRenderer(renderer, colors, styles);
      return renderer;
    }

    protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors, PointStyle[] styles) {
      renderer.setAxisTitleTextSize(16);
      renderer.setChartTitleTextSize(20);
      renderer.setLabelsTextSize(15);
      renderer.setLegendTextSize(15);
      renderer.setPointSize(5f);
      renderer.setMargins(new int[] { 20, 30, 15, 20 });
      int length = colors.length;
      for (int i = 0; i < length; i++) {
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(colors[i]);
        r.setPointStyle(styles[i]);
        renderer.addSeriesRenderer(r);
      }
    }

    /**
     * Sets a few of the series renderer settings.
     * 
     * @param renderer the renderer to set the properties to
     * @param title the chart title
     * @param xTitle the title for the X axis
     * @param yTitle the title for the Y axis
     * @param xMin the minimum value on the X axis
     * @param xMax the maximum value on the X axis
     * @param yMin the minimum value on the Y axis
     * @param yMax the maximum value on the Y axis
     * @param axesColor the axes color
     * @param labelsColor the labels color
     */
    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
        String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
        int labelsColor) {
      renderer.setChartTitle(title);
      renderer.setXTitle(xTitle);
      renderer.setYTitle(yTitle);
      renderer.setXAxisMin(xMin);
      renderer.setXAxisMax(xMax);
      renderer.setYAxisMin(yMin);
      renderer.setYAxisMax(yMax);
      renderer.setAxesColor(axesColor);
      renderer.setLabelsColor(labelsColor);
    }

    /**
     * Builds an XY multiple time dataset using the provided values.
     * 
     * @param titles the series titles
     * @param xValues the values for the X axis
     * @param yValues the values for the Y axis
     * @return the XY multiple time dataset
     */
    protected XYMultipleSeriesDataset buildDateDataset(String[] titles, List<Date[]> xValues,
        List<double[]> yValues) {
      XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
      int length = titles.length;
      for (int i = 0; i < length; i++) {
        TimeSeries series = new TimeSeries(titles[i]);
        Date[] xV = xValues.get(i);
        double[] yV = yValues.get(i);
        int seriesLength = xV.length;
        for (int k = 0; k < seriesLength; k++) {
          series.add(xV[k], yV[k]);
        }
        dataset.addSeries(series);
      }
      return dataset;
    }

    /**
     * Builds a category series using the provided values.
     * 
     * @param titles the series titles
     * @param values the values
     * @return the category series
     */
    protected CategorySeries buildCategoryDataset(String title, double[] values) {
      CategorySeries series = new CategorySeries(title);
      int k = 0;
      for (double value : values) {
        series.add("Project " + ++k, value);
      }

      return series;
    }

    /**
     * Builds a multiple category series using the provided values.
     * 
     * @param titles the series titles
     * @param values the values
     * @return the category series
     */
    protected MultipleCategorySeries buildMultipleCategoryDataset(String title,
        List<String[]> titles, List<double[]> values) {
      MultipleCategorySeries series = new MultipleCategorySeries(title);
      int k = 0;
      for (double[] value : values) {
        series.add(2007 + k + "", titles.get(k), value);
        k++;
      }
      return series;
    }

    /**
     * Builds a category renderer to use the provided colors.
     * 
     * @param colors the colors
     * @return the category renderer
     */
    protected DefaultRenderer buildCategoryRenderer(int[] colors) {
      DefaultRenderer renderer = new DefaultRenderer();
      renderer.setLabelsTextSize(15);
      renderer.setLegendTextSize(15);
      renderer.setMargins(new int[] { 20, 30, 15, 0 });
      for (int color : colors) {
        SimpleSeriesRenderer r = new SimpleSeriesRenderer();
        r.setColor(color);
        renderer.addSeriesRenderer(r);
      }
      return renderer;
    }

    /**
     * Builds a bar multiple series dataset using the provided values.
     * 
     * @param titles the series titles
     * @param values the values
     * @return the XY multiple bar dataset
     */
    protected XYMultipleSeriesDataset buildBarDataset(String[] titles, List<double[]> values) {
      XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
      int length = titles.length;
      for (int i = 0; i < length; i++) {
        CategorySeries series = new CategorySeries(titles[i]);
        double[] v = values.get(i);
        int seriesLength = v.length;
        for (int k = 0; k < seriesLength; k++) {
          series.add(v[k]);
        }
        dataset.addSeries(series.toXYSeries());
      }
      return dataset;
    }

    /**
     * Builds a bar multiple series renderer to use the provided colors.
     * 
     * @param colors the series renderers colors
     * @return the bar multiple series renderer
     */
    protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
      XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
      renderer.setAxisTitleTextSize(16);
      renderer.setChartTitleTextSize(20);
      renderer.setLabelsTextSize(15);
      renderer.setLegendTextSize(15);
      int length = colors.length;
      for (int i = 0; i < length; i++) {
        SimpleSeriesRenderer r = new SimpleSeriesRenderer();
        r.setColor(colors[i]);
        renderer.addSeriesRenderer(r);
      }
      return renderer;
    }


}
