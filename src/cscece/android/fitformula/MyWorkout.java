package cscece.android.fitformula;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MyWorkout extends Activity {
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.my_workout_layout);
       
        
        
    }//end of onCreate
    
    //Method called when the "Get Workout" button is Pushed
    public void getWorkoutPushed(View view){
    	
    	//intent to start FitnessTest Activity
    	startActivity(new Intent(this,FitnessTest.class));
    	
    }//end of getWorkoutPushed

}
