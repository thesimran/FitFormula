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
	public static final String PROGRAM1_TABLE_NAME = "program1";
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
    
    //hrdata columns
    public static final String heartrate = "heartrate";
    public static final String date = "date";    
     
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
	    				+ "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
	    				+ gender + " INTEGER,"
	    				+ age + " INTEGER,"
	    				+ height + " INTEGER,"
	    				+ weight + " INTEGER," 
	    				+ hypertension + " INTEGER,"
	    				+ smoking + " INTEGER,"
	    				+ diabetes + " INTEGER,"
	    				+ bloodpressure + " INTEGER"
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
		db.execSQL("DROP TABLE IF EXISTS "+PROGRAM1_TABLE_NAME);
		onCreate(db);
	}

}
