package cscece.android.fitformula;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class HcFitness extends Activity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        TextView textview = new TextView(this);
        textview.setText("This is the HcFitness tab");
        setContentView(textview);
   
    }//end of onCreate
}
