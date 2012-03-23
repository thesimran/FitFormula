package cscece.android.fitformula;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "fitformula.db";
	private static final int DATABASE_VERSION = 2;
	public static final String USER_TABLE_NAME = "userinfo";
	public static final String HR_TABLE_NAME = "hrdata";
	public static final String MY_PROGRAM_TABLE_NAME = "myprogram";
	public static final String FREQUENCY_TABLE_NAME = "frequency";
	public static final String WARMUP_TABLE_NAME = "warmup";
	public static final String COOLDOWN_TABLE_NAME = "cooldown";
	public static final String PROGRAM1_TABLE_NAME = "program1";
	public static final String PROGRAM2_TABLE_NAME = "program2";
	public static final String PROGRAM3_TABLE_NAME = "program3";
    private static String DB_PATH = "/data/data/cscece.android.fitformula/databases/";    

    //userinfo columns
    public static final String gender = "gender";
    public static final String age = "age";
    public static final String height = "height";
    public static final String weight = "weight";
    public static final String hypertension = "hypertension";
    public static final String smoking = "smoking";
    public static final String diabetes = "diabetes";
    public static final String bloodpressure = "bloodpressure";
    public static final String bmi = "bmi";
    public static final String bmiclass = "bmiclass";
    public static final String risk = "risk";
    public static final String normalrisk = "normalrisk";
    public static final String cvdrisk = "cvdrisk";
    public static final String normalcvdrisk = "normalcvdrisk";
    public static final String cvdriskclass = "cvdriskclass";
    public static final String heartage = "heartage";
    public static final String vo2 = "vo2";
    public static final String vo2class = "vo2class";    
    
    //hrdata columns
    public static final String heartrate = "heartrate";
    public static final String date = "date";    
     
    //myprogram columns
    public static final String program = "program";    
    public static final String calID = "calID";
    
    //frequency columns
    public static final String numinterval="numinterval";
    public static final String numaerobic = "numaerobic";    
    public static final String numcycles="numcycles";
    public static final String rest="rest";
    public static final String restalternate="restalternate";
    
    //program columns
    public static final String level="level";
    public static final String intensitylow="intensitylow";
    public static final String intensityhigh="intensityhigh";
    public static final String progresspercentage="progresspercentage";
    public static final String duration="duration";
    public static final String aerobic="aerobic";
    
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
				
		  boolean dbExist = checkDataBase(USER_TABLE_NAME);		  
	        if(dbExist){
	                //do nothing - database already exists
	        	Log.d("db","userinfo table exists");
	        }else{
	        	Log.d("db","userinfo table does not exist");
	    		db.execSQL("CREATE TABLE " + USER_TABLE_NAME + " ("
	    				+ "_id" + " INTEGER PRIMARY KEY,"
	    				+ gender + " INTEGER,"
	    				+ age + " INTEGER,"
	    				+ height + " INTEGER,"
	    				+ weight + " INTEGER," 
	    				+ hypertension + " INTEGER,"
	    				+ smoking + " INTEGER,"
	    				+ diabetes + " INTEGER,"
	    				+ bloodpressure + " INTEGER,"
	    				+ bmi + " DOUBLE,"
	    				+ bmiclass + " TEXT,"
	    				+ risk + " DOUBLE,"
	    				+ normalrisk + " DOUBLE,"
	    				+ cvdrisk + " DOUBLE,"
	    				+ normalcvdrisk + " DOUBLE,"
	    				+ cvdriskclass + " TEXT,"
	    				+ heartage + " DOUBLE,"
	    				+ vo2 + " DOUBLE,"
	    				+ vo2class + " TEXT,"
	    				+ program + " INTEGER,"
	    				+ level + " INTEGER"
	    				+ ");");	              
	        }
	        
	        dbExist = checkDataBase(HR_TABLE_NAME);		  
	        if(dbExist){
	                //do nothing - database already exists
	        	Log.d("db","hrdata table exists");
	        }else{
	        	Log.d("db","hrdata table does not exist");
	    		db.execSQL("CREATE TABLE " + HR_TABLE_NAME + " ("
	    				+ "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
	    				+ heartrate + " INTEGER,"
	    				+ date + " BIGINT"	    				
	    				+ ");");	              
	        }

	        dbExist = checkDataBase(MY_PROGRAM_TABLE_NAME);		  
	        if(dbExist){
	                //do nothing - database already exists
	        	Log.d("db","myprogram table exists");
	        }else{
	        	Log.d("db","myprogram table does not exist");
	    		db.execSQL("CREATE TABLE " + MY_PROGRAM_TABLE_NAME + " ("
	    				+ "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
	    				+ program + " INTEGER,"	    				
	    				+ level + " INTEGER,"	    				
	    				+ aerobic + " BOOLEAN,"
	    				+ date + " BIGINT,"
	    				+ calID + " INTEGER"
	    				+ ");");	               
	        }
	        dbExist = checkDataBase(FREQUENCY_TABLE_NAME);		  
	        if(dbExist){
	                //do nothing - database already exists
	        	Log.d("db","frequency table exists");
	        }else{
	        	Log.d("db","frequency table does not exist");
	    		db.execSQL("CREATE TABLE " + FREQUENCY_TABLE_NAME + " ("
	    				+ "_id" + " INTEGER PRIMARY KEY,"
	    				+ program + " INTEGER,"	    				
	    				+ level + " INTEGER,"
	    				+ numinterval + " INTEGER,"
	    				+ numaerobic + " INTEGER,"	    				
	    				+ numcycles + " INTEGER,"
	    				+ rest + " INTEGER,"
	    				+ restalternate + " BOOLEAN"	    				
	    				+ ");");	               
	        }
	        
	        dbExist = checkDataBase(WARMUP_TABLE_NAME);		  
	        if(dbExist){
	                //do nothing - database already exists
	        	Log.d("db","warmup table exists");
	        }else{
	        	Log.d("db","warmup table does not exist");
	    		db.execSQL("CREATE TABLE " + WARMUP_TABLE_NAME + " ("
	    				+ "_id" + " INTEGER PRIMARY KEY,"
	    				+ intensitylow + " TEXT,"
	    				+ intensityhigh + " TEXT,"
	    				+ progresspercentage + " TEXT,"
	    				+ duration + " INTEGER"
	    				+ ");");	               
	        }
	        
	        dbExist = checkDataBase(COOLDOWN_TABLE_NAME);		  
	        if(dbExist){
	                //do nothing - database already exists
	        	Log.d("db","cooldown table exists");
	        }else{
	        	Log.d("db","cooldown table does not exist");
	    		db.execSQL("CREATE TABLE " + COOLDOWN_TABLE_NAME + " ("
	    				+ "_id" + " INTEGER PRIMARY KEY,"
	    				+ intensitylow + " TEXT,"
	    				+ intensityhigh + " TEXT,"
	    				+ progresspercentage + " TEXT,"
	    				+ duration + " INTEGER"
	    				+ ");");	               
	        }
	        
	        dbExist = checkDataBase(PROGRAM1_TABLE_NAME);		  
	        if(dbExist){
	                //do nothing - database already exists
	        	Log.d("db","program1 table exists");
	        }else{
	        	Log.d("db","program1 table does not exist");
	    		db.execSQL("CREATE TABLE " + PROGRAM1_TABLE_NAME + " ("
	    				+ "_id" + " INTEGER PRIMARY KEY,"
	    				+ level + " INTEGER,"
	    				+ intensitylow + " TEXT,"
	    				+ intensityhigh + " TEXT,"
	    				+ progresspercentage + " TEXT,"
	    				+ duration + " INTEGER,"
	    				+ aerobic + " BOOLEAN"
	    				+ ");");	               
	        }
	        dbExist = checkDataBase(PROGRAM2_TABLE_NAME);		  
	        if(dbExist){
	                //do nothing - database already exists
	        	Log.d("db","program2 table exists");
	        }else{
	        	Log.d("db","program2 table does not exist");
	    		db.execSQL("CREATE TABLE " + PROGRAM2_TABLE_NAME + " ("
	    				+ "_id" + " INTEGER PRIMARY KEY,"
	    				+ level + " INTEGER,"
	    				+ intensitylow + " TEXT,"
	    				+ intensityhigh + " TEXT,"
	    				+ progresspercentage + " TEXT,"
	    				+ duration + " INTEGER,"
	    				+ aerobic + " BOOLEAN"
	    				+ ");");	               
	        }
	        dbExist = checkDataBase(PROGRAM3_TABLE_NAME);		  
	        if(dbExist){
	                //do nothing - database already exists
	        	Log.d("db","program3 table exists");
	        }else{
	        	Log.d("db","program3 table does not exist");
	    		db.execSQL("CREATE TABLE " + PROGRAM3_TABLE_NAME + " ("
	    				+ "_id" + " INTEGER PRIMARY KEY,"
	    				+ level + " INTEGER,"
	    				+ intensitylow + " TEXT,"
	    				+ intensityhigh + " TEXT,"
	    				+ progresspercentage + " TEXT,"
	    				+ duration + " INTEGER,"
	    				+ aerobic + " BOOLEAN"
	    				+ ");");	               
	        }
  
	}
	
	private boolean checkDataBase(String name) {

		SQLiteDatabase checkDB = null;
		try {
			String myPath = DB_PATH + name;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {
			// database does't exist yet.
		}

		if (checkDB != null) {
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("UPGRADE", "Upgrading database from version " + oldVersion
				+ " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS "+USER_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+HR_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+MY_PROGRAM_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+FREQUENCY_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+WARMUP_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+COOLDOWN_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+PROGRAM1_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+PROGRAM2_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+PROGRAM3_TABLE_NAME);
		onCreate(db);
	}

}
