package cscece.android.fitformula;

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
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
	int level;
	String programName;
	
	//private LinearLayout mLinear;
	private RelativeLayout mLinear;
	private View chartView;
	private ViewGroup.LayoutParams params;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);        
        //TextView textview = new TextView(this);
        //textview.setText("This is the HcFitness tab");
        setContentView(R.layout.hc_fitness_layout);
        myTextView = (TextView) findViewById(R.id.do_fitness_test_note);
        //mLinear = (LinearLayout) findViewById(R.id.hc_linear);
        mLinear = (RelativeLayout) findViewById(R.id.hc_linear);
                
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
			bloodPressure = c.getInt(columnIndex);
			Log.d("db", "bloodpressure " + bloodPressure);									
			determineHealthFactors(dbh);								
			determineHealthClass(dbh);
			determineProgram(dbh);

			SharedPreferences settings = getSharedPreferences(MyWorkout.PREFS_NAME, 0);
	    	SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("gottenWorkout", true);
			editor.commit();
			
			
			myTextView.setText("The quick brown fox jumped over the lazy dog.");
			myTextView.setId(1);
			/*myTextView.setText("Blood Pressure: "+bloodPressure+" mmHg\nHeight: "+height+" cm\nWeight: "+weight+" kg\nBMI: "
					 +String.format("%.1f",myBMI)+"\nRisk/Normal Risk: "+String.format("%.2f",risk)+"/"+String.format("%.2f",normalRisk)
					 +"\nCVD Risk/Normal CVD Risk: "+String.format("%.2f",100*myCVDRisk)+"/"+String.format("%.2f",100*normalCVDRisk)
					 +"%\nHeart Age/Age: "+String.format("%.0f",heartAge)+"/"+age);*/
			
			String[] titles = new String[] { "My CVD Risk", "Normal CVD Risk" };
		    List<double[]> values = new ArrayList<double[]>();
		    
		    int temp=(int)(myCVDRisk*10000.0);
		    double temp2=temp/100.0;		    
		    values.add(new double[] { temp2 });
		    temp=(int)(normalCVDRisk*10000.0);
		    temp2=temp/100.0;
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
		    renderer.setZoomRate(1.1f);
		    renderer.setBarSpacing(-0.85f);
		    
		    //Add the view!
	        chartView = ChartFactory.getBarChartView(this, buildBarDataset(titles, values), renderer,Type.DEFAULT);
	        chartView.setId(2);
	        //params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
	        params = new ViewGroup.LayoutParams(200, 200);
	        //addContentView(chartView, params);
	        
	        //LinearLayout linLayout = new LinearLayout(this);	        	        	        
	        TextView asdf = new TextView(this);	      
	        asdf.setId(3);
	        asdf.setText("abcdefghijklmnopqrstuvwxyz");
	        //linLayout.addView(asdf,params);
	        Log.d("count",""+mLinear.getChildCount());
	        //mLinear.addView(chartView,mLinear.getChildCount()-1,params);
	        RelativeLayout.LayoutParams relParams1=new RelativeLayout.LayoutParams(200,200);
	        relParams1.addRule(RelativeLayout.BELOW,myTextView.getId());	       
	        
	        mLinear.addView(chartView,relParams1);
	        Log.d("count",""+mLinear.getChildCount());
	        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	        RelativeLayout.LayoutParams relParams2=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
	        relParams2.addRule(RelativeLayout.RIGHT_OF,chartView.getId());
	        relParams2.addRule(RelativeLayout.BELOW,myTextView.getId());
	        //mLinear.addView(asdf,mLinear.getChildCount()-1,params);
	        mLinear.addView(asdf,relParams2);
	        Log.d("count",""+mLinear.getChildCount());
	        
	        //TODO: clear chartView/contentView before it updates otherwise overlap old one		    
			
		}else{
	        myTextView.setText("You have not yet done a Fitness Test, therefore your Health Center contains no data.  Please do the Fitness Test and then select a workout to begin your journey towards ultimate fitness prowess.");
		}
		db.close();
    }
    
    public void determineProgram(DatabaseHelper dbh){
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
    	
    	//TODO: ************************************REMOVE LEVEL****************************************/
    	level=4;
    	
    	ContentValues values = new ContentValues();		
		int rowIndex = 1;
		SQLiteDatabase db = dbh.getWritableDatabase();		
		Log.d("db","program:"+program+" level:"+level+" bmiProblem:"+bmiProblem+" cvdRiskProblem:"+cvdRiskProblem+" vo2Problem:"+vo2Problem);
		values.put(DatabaseHelper.program, program);
		values.put(DatabaseHelper.level, level);		
		db.update(DatabaseHelper.USER_TABLE_NAME, values, "_id = "
				+ rowIndex, null);
		db.close();
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
