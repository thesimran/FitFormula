package cscece.android.fitformula;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class FitnessTest extends Activity {
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.fitness_test_layout);
        //set title
        //FitFormula.title.setText("Fitness Test"); -- still a work in progress

    }//end of onCreate

    //Called when the "Start Fitness Test Now" button is pushed
    public void startTestPushed(View view){
    	
    	Intent i = new Intent(this, FitTest.class);
    	startActivity(i);
    }//end of startTestPushed
}
