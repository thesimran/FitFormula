package cscece.android.fitformula;

import java.text.DateFormat;
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
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HcWeight extends Activity   {
	
	public Context context;
	private View chartView;
	private RelativeLayout mRelative;
	private TextView titleText;
	private EditText weightEntry;
	private TextView weightLabel;	
	private Button weightButton;
	private int weight;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        context = this;
        setContentView(R.layout.hc_weight_layout);
        mRelative = (RelativeLayout) findViewById(R.id.hc_weight_relative);
        titleText = (TextView) findViewById(R.id.weight_do_fitness_test_note);
                
    }//end of onCreate
    @Override
    protected void onStart(){
    	super.onStart();
    	
    	SharedPreferences settings = getSharedPreferences(MyWorkout.PREFS_NAME, 0);						
		boolean gottenWorkout = settings.getBoolean("gottenWorkout", false);
		
		if (gottenWorkout) { // if user has updated their weight, reload weight chart
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("weightUpdated", false); // make weightUpdated false so don't reload weight chart every time
			editor.commit();

			getUserWeightsAndDraw();
		}
    }//end of onStart
    
    
    @Override
    public void onResume(){
    	super.onResume();
    	
    	SharedPreferences settings = getSharedPreferences(MyWorkout.PREFS_NAME, 0);						
		boolean weightUpdated = settings.getBoolean("weightUpdated", false);
		boolean gottenWorkout = settings.getBoolean("gottenWorkout", false);

		if (gottenWorkout && weightUpdated) { // if user has updated their weight, reload weight chart
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("weightUpdated", false); // make weightUpdated false so don't reload weight chart every time
			editor.commit();
			
			getUserWeightsAndDraw();
		}
    }//end of onResume
    
    public void getUserWeightsAndDraw(){
    	DatabaseHelper dbh;
		dbh = new DatabaseHelper(this);
		SQLiteDatabase db = dbh.getReadableDatabase();

		Cursor c = db.query(DatabaseHelper.WEIGHT_TABLE_NAME, new String[] {
				DatabaseHelper.weight, DatabaseHelper.date }, null, null,
				null, null, null, null);

		ArrayList<Double> weights = new ArrayList<Double>();
		ArrayList<Long> dates = new ArrayList<Long>();

		while (c.moveToNext()) { // table has data (rows) in it

			int columnIndex = c.getColumnIndex(DatabaseHelper.weight);
			weights.add((double) (c.getInt(columnIndex)));

			columnIndex = c.getColumnIndex(DatabaseHelper.date);
			dates.add(c.getLong(columnIndex));
		}

		db.close();

		double[] weightsDouble = new double[weights.size()];
		Date[] theDates = new Date[dates.size()];
		for (int i = 0; i < weights.size(); i++) {
			weightsDouble[i] = weights.get(i);
			theDates[i] = new Date(dates.get(i));
			Log.d("weights","weight"+weights.get(i));
		}

		drawWeightLayout(weightsDouble, theDates);
    }
    
    public void drawWeightLayout(double[]myWeights, Date[]myDates){
    	 String[] titles = new String[] { "FitFormula User" };
         List<Date[]> dates = new ArrayList<Date[]>();
         List<double[]> values = new ArrayList<double[]>();
         for (int i = 0; i < titles.length; i++) {
           dates.add(myDates);
           values.add(myWeights);
         }                  
         
         int[] colors = new int[] {Color.YELLOW};
         PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE };
         XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
         int length = renderer.getSeriesRendererCount();
         for (int i = 0; i < length; i++) {
           ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
         }
         double weightMin=getMinValue(myWeights);
         double weightMax=getMaxValue(myWeights);
         setChartSettings(renderer, "Weight Loss", "Date", "Weight (kg)", dates.get(0)[0].getTime(),dates.get(0)[myDates.length-1].getTime(),
        		 weightMin-weightMin*0.10, weightMax+weightMax*0.10, Color.LTGRAY, Color.LTGRAY);
         renderer.setXLabels(myDates.length-1);
         renderer.setYLabels(8);
         renderer.setShowGrid(true);
         renderer.setXLabelsAlign(Align.RIGHT);
         renderer.setYLabelsAlign(Align.RIGHT);
         renderer.setZoomButtonsVisible(false);
         renderer.setZoomEnabled(false, false);
         renderer.setPanEnabled(true, false);
         renderer.setPanLimits(new double[] { 0, myDates.length-1, 0, weightMax}); //minX, maxX, minY, maxY
         //renderer.setZoomLimits(new double[] { 0, 20, 60, 120 });          
         
         mRelative.removeAllViews();	    	    
         
         titleText.setText(Html.fromHtml("<br><b><u><big>My Weight History</big></u></b><br>"));
         titleText.setId(1);
 		RelativeLayout.LayoutParams relParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);	        
         relParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
         relParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
         mRelative.addView(titleText,relParams);	  
         
         //Add the view!
         chartView = ChartFactory.getTimeChartView(context, buildDateDataset(titles, dates, values), renderer, "MM/dd/yyyy");         
         chartView.setId(2);
         DisplayMetrics displaymetrics = new DisplayMetrics();
         getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
         int height = displaymetrics.heightPixels;
         int width = displaymetrics.widthPixels;
         int chartSize = (int)(width*0.9);
         relParams=new RelativeLayout.LayoutParams(width,chartSize);
         relParams.addRule(RelativeLayout.BELOW,titleText.getId());
         relParams.addRule(RelativeLayout.CENTER_HORIZONTAL);        
         mRelative.addView(chartView,relParams);                	        
                 
         weightLabel = new TextView(this);
         weightLabel.setId(3);
         weightLabel.setText("New Weight (kg)");
         //weightLabel.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
         weightLabel.setGravity(Gravity.CENTER);
         relParams=new RelativeLayout.LayoutParams(width/2,RelativeLayout.LayoutParams.WRAP_CONTENT);	        
         relParams.addRule(RelativeLayout.BELOW,chartView.getId());            	                                                 	              
         mRelative.addView(weightLabel,relParams);weightEntry = new EditText(this);
        
         weightEntry.setId(4);
         weightEntry.setHint("Enter Weight");
         weightEntry.setInputType(InputType.TYPE_CLASS_NUMBER);
         InputFilter[] filterArray = new InputFilter[1];
         filterArray[0] = new InputFilter.LengthFilter(3);
         weightEntry.setFilters(filterArray);
         relParams=new RelativeLayout.LayoutParams(width/2,RelativeLayout.LayoutParams.WRAP_CONTENT);	        
         relParams.addRule(RelativeLayout.RIGHT_OF,weightLabel.getId());
         relParams.addRule(RelativeLayout.BELOW,chartView.getId());
         relParams.addRule(RelativeLayout.ALIGN_BASELINE,weightLabel.getId());
         mRelative.addView(weightEntry,relParams);	        
                 
         weightButton = new Button(this);
         weightButton.setId(5);
         weightButton.setText("Save New Weight");
         weightButton.setWidth(width/2);
         weightButton.setOnClickListener(new Button.OnClickListener() {  
         public void onClick(View v)
             {
         		enterWeightClicked();
             }
          });
         relParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);	        
         relParams.addRule(RelativeLayout.BELOW,weightEntry.getId());
         relParams.addRule(RelativeLayout.CENTER_HORIZONTAL);           
         mRelative.addView(weightButton,relParams);	        	               
    }
    
	public double getMaxValue(double[] numbers) {
		double maxValue = numbers[0];
		for (int i = 1; i < numbers.length; i++) {
			if (numbers[i] > maxValue) {
				maxValue = numbers[i];
			}
		}
		return maxValue;
	}

	public double getMinValue(double[] numbers) {
		double minValue = numbers[0];
		for (int i = 1; i < numbers.length; i++) {
			if (numbers[i] < minValue) {
				minValue = numbers[i];
			}
		}
		return minValue;
	}
    
    private void enterWeightClicked(){
        try{
    		weight = Integer.parseInt(weightEntry.getText().toString());
    	}catch(Exception e){    		
    		Toast.makeText(this, "You must enter a valid weight. Please try again.", Toast.LENGTH_LONG).show();
    		return;    		
    	}	
    	if(weight <= 0 ){    		    		
    		Toast.makeText(this, "You must enter a valid weight. Please try again.", Toast.LENGTH_LONG).show();
    		return;
    	}
    	confirmWeight();
    }
    
    private void confirmWeight(){
    	new AlertDialog.Builder(getParent())
		.setTitle("Save Weight")
		.setMessage(Html.fromHtml("Would you like to save this weight? <i>"+weight+" kg</i>"))
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface arg0, int arg1) {
		    	
		    	DatabaseHelper dbh= new DatabaseHelper(HcWeight.this);
				ContentValues values = new ContentValues();
				SQLiteDatabase db = dbh.getWritableDatabase();						    
				values.clear();				
				values.put(DatabaseHelper.weight, weight);
				values.put(DatabaseHelper.date, System.currentTimeMillis());				
				db.insert(DatabaseHelper.WEIGHT_TABLE_NAME, null, values);
				db.close();
				
				SharedPreferences settings = getSharedPreferences(MyWorkout.PREFS_NAME, 0);
		    	SharedPreferences.Editor editor = settings.edit();				
				editor.putBoolean("weightUpdated",false);
				editor.commit();					
		    	Toast.makeText(HcWeight.this, "Weight Saved", Toast.LENGTH_LONG).show();
		    	
				getUserWeightsAndDraw();
		    }
		})
		.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int whichButton) {
																
			}
		})
		.show();  
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