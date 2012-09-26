package cscece.android.fitformula;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import cscece.android.fitformula.utils.ChartRenderer;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CardiovasRisk extends Activity {

	private double myCVDRisk;
	private double normalCVDRisk;
	private int gender;
	private int age;
	private int height;
	private int weight;
	private int hypertension;
	private int smoking;
	private int diabetes;
	private int bloodPressure;
	private double vo2;
	private View chartView;
	private TextView titleText;
	private RelativeLayout mRelative;
	
	@Override
	public void onCreate(Bundle icicle){
		super.onCreate(icicle);
		setContentView(R.layout.hc_fitness_layout);
		/* A virtually empty layout file that has to be filled with a
		 * title/heading, chartView, and a description TextView.
		 */
		
		mRelative = (RelativeLayout) findViewById(R.id.hc_relative);
		titleText = new TextView(this);
		getUserData();
		
	}
	
	public void getUserData(){
    	DatabaseHelper dbh;
		dbh = new DatabaseHelper(this);		
		int rowIndex = 1;
		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor c = db.query(DatabaseHelper.USER_TABLE_NAME, new String[] {
				DatabaseHelper.gender, DatabaseHelper.age,
				DatabaseHelper.height, DatabaseHelper.weight,
				DatabaseHelper.hypertension, DatabaseHelper.smoking,
				DatabaseHelper.diabetes, DatabaseHelper.bloodpressure, 
				DatabaseHelper.vo2, DatabaseHelper.cvdrisk,
				DatabaseHelper.normalcvdrisk},
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
			
			columnIndex = c.getColumnIndex(DatabaseHelper.cvdrisk);
			myCVDRisk = c.getDouble(columnIndex);
			Log.d("db", "myCVDRisk " + myCVDRisk);
			
			columnIndex = c.getColumnIndex(DatabaseHelper.normalcvdrisk);
			normalCVDRisk = c.getDouble(columnIndex);
			Log.d("db", "normalCVDRisk " + normalCVDRisk);									

			drawFitnessLayout();       	        	    

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
	    int[] colors = new int[] { Color.GREEN, Color.MAGENTA };
	    //TODO ChartRenderer should not be a singleton!!
	    XYMultipleSeriesRenderer renderer = ChartRenderer.buildBarRenderer(colors);
	    double yMax=100;
	    if(myCVDRisk>=normalCVDRisk){
	    	yMax=(myCVDRisk*100)+(myCVDRisk*20);
	    }else{
	    	yMax=(normalCVDRisk*100)+(myCVDRisk*20);
	    }
	    ChartRenderer.setChartSettings(renderer, "", "", "CVD Risk (%)", 0.5,
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
        chartView = ChartFactory.getBarChartView(this, ChartRenderer.buildBarDataset(titles, values), renderer, Type.DEFAULT);
        chartView.setId(2); 
        	        
        titleText.setText("Cardiovascular Risk");
        titleText.setId(1);
		RelativeLayout.LayoutParams relParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);	        
        relParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        relParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        titleText.setTextSize(30);
        titleText.setTextAppearance(this, android.R.attr.textAppearanceLarge);
        
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
        relParams.setMargins(10, 10, 10, 10);
        mRelative.addView(chartView,relParams);	   

        //TODO Can't figure out how to get rid of the black border around the graph!!!!!  It looks shitty :(
              
    }
}
