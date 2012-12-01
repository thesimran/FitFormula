package cscece.android.fitformula;

import cscece.android.fitformula.utils.DatabaseHelper;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Bmi extends Activity {

	private ProgressBar bmiProgressBar;
	private TextView yourBmiView;
	
	//ProgressBar constant values
	private static final int BAR_UNDERWEIGHT = 25;
	private static final int BAR_NORMAL = 50;
	private static final int BAR_OVERWEIGHT = 70;
	private static final int BAR_OBESE = 90;
	
	public static final double BMI_UNDERWEIGHT = 18.5;
	public static final int BMI_NORMAL = 25;
	public static final int BMI_OVERWEIGHT = 30;
	
	
	@Override
	public void onCreate(Bundle icicle){
		super.onCreate(icicle);
		setContentView(R.layout.hc_bmi_layout);
		
		init();
	}
	
	private void init(){
		//Obtain ProgressBar
		bmiProgressBar = (ProgressBar) findViewById(R.id.bmi_progress_bar);
		//...and the your_bmi TextView
		yourBmiView = (TextView)findViewById(R.id.your_bmi);
		
		//Extract from DB
		DatabaseHelper dbh;
		dbh = new DatabaseHelper(this);		
		int rowIndex = 1;
		int bmi = 0;
		String bmiClass = "";
		
		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor c = db.query(DatabaseHelper.USER_TABLE_NAME, new String[] {
				DatabaseHelper.bmi, DatabaseHelper.bmiclass},
				"_id = " + rowIndex, null, null, null, null);
		
		
		if (c.moveToNext()) { // table has data (rows) in it
			
			Log.d("db", "userinfo table not empty");
			int columnIndex = c.getColumnIndex(DatabaseHelper.bmi);
			bmi = c.getInt(columnIndex);
			Log.d("db", "bmi " + bmi);
			
			columnIndex = c.getColumnIndex(DatabaseHelper.bmiclass);
			bmiClass = c.getString(columnIndex);
			Log.d("db", "bmiClass " + bmiClass);
			
		}
		c.close();
		dbh.close();
		//set TextView
		String newYourBmi = "Your BMI level is: " + bmi + ", " + bmiClass;
		yourBmiView.setText(newYourBmi);
		
		//set ProgressBar
		if (bmi < BMI_UNDERWEIGHT) {
			bmiProgressBar.setProgress(BAR_UNDERWEIGHT);
		}else if (bmi >= BMI_UNDERWEIGHT && bmi < BMI_NORMAL) {
			bmiProgressBar.setProgress(BAR_NORMAL);
		}else if (bmi >= BMI_NORMAL && bmi < BMI_OVERWEIGHT){
			bmiProgressBar.setProgress(BAR_OVERWEIGHT);
		}else{ //myBMI>=30
			bmiProgressBar.setProgress(BAR_OBESE);
		}
	}
	
	
}
