package cscece.android.fitformula.utils;

import java.util.Calendar;

import cscece.android.fitformula.FitFormulaApp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	
	private static final String DATABASE_NAME = "fitformula.db";
	private static final int DATABASE_VERSION = 1;	
	
	//Constant variables to represent days of the week
	private static final int MONDAY = 0;
	private static final int TUESDAY = 1;
	private static final int WEDNESDAY = 2;
	private static final int THURSDAY = 3;
	private static final int FRIDAY = 4;
	private static final int SATURDAY = 5;
	private static final int SUNDAY = 6;
	
	private static int TOTAL_STANDARD_PROGRAM_DAYS = 37; 
	private static final int NUMBER_OF_WEEKS_STANDARD_PROGRAM = 8;
	
	//User table
    private static final String DATABASE_TABLE_USER = "user";
    private static final String DATABASE_CREATE_TABLE_USER =
    		"create table user (" +
    				"_id INTEGER primary key autoincrement, "+
    				"gender INTEGER, " +
    				"age INTEGER," +
    				"height INTEGER," +
    				"weight INTEGER," +
    				"hypertension INTEGER, " +
    				"smoking INTEGER," +
    				"diabetes INTEGER," +
    				"bloodpressure INTEGER, " +
    				"bmi DOUBLE," +
    				"bmi_class TEXT," +
    				"risk DOUBLE," +
    				"normal_risk DOUBLE," +
    				"cvd_risk DOUBLE," +
    				"normal_cvd_risk DOUBLE," +
    				"cvd_risk_class DOUBLE," +
    				"heart_age DOUBLE," +
    				"vo2 DOUBLE," +
    				"vo2_class TEXT, " +
    				"program_number INTEGER," + //new meaning from last version -- if they start a new program, this number gets incremented 
    									//to join with the 'program' table.  This allows us to keep a workout history
    									//for the user.
    				//"level INTEGER," +
    				//"program_name TEXT," +
    				"regualr_exercise INTEGER," +
    				"date BIGINT" +
    				");";
    /*
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
			+ level + " INTEGER,"
			+ programname + " TEXT,"
			+ regularExercise + " INTEGER,"
			+ date + " BIGINT"
			+ ");");*/
    
    private static final String DATABASE_TABLE_PROGRAM = "program";
    private static final String DATABASE_CREATE_TABLE_PROGRAM =
    		"create table program (" +
    				"_id INTEGER primary key autoincrement," +
    				"week INTEGER," + // week 3
    				"length INTEGER," + // 20 minutes
    				"day_of_week INTEGER," + //0=Monday, 1=Tuesday, 2=Wednesday, 3=Thursday etc...
    				"makeup_days_left INTEGER," +
    				"intensity INTEGER," +
    				"completed BOOLEAN default false," +
    				"day INTEGER," +//0-56
    				"scheduled BOOLEAN default false," +
    				"rating INTEGER," + //0-2, -1 = not yet rated
    				"success_completed BOOLEAN," +
    				"sched_date BIGINT, " +
    				"program_number INTEGER" + //to join with the user table, to keep user history
    				");";

    private static final String DATABASE_TABLE_PROGRAM_META = "program_meta";
    private static final String DATABASE_CREATE_TABLE_PROGRAM_META = 
    		"create table program_meta (" +
    				"id_ INTEGER primary key autoincrement, " +
    				"program_id INTEGER, "  + //FK
    				"started BOOLEAN default false, " +
    				"complete BOOLEAN default false" +
    				");";
    
    private static final String HR_TABLE_NAME = "heartrate";
    private static final String DATABASE_CREATE_TABLE_HEARTRATE =
    		"create table heartrate (" +
    				"_id INTEGER primary key autoincrement, " +
    				"hr INTEGER," +
    				"date BIGINT," +
    				"resting BOOLEAN" + //That's it for now...
    				");";
    
    /*
    dbExist = checkDataBase(HR_TABLE_NAME);		  
    if(dbExist){
            //do nothing - database already exists
    	Log.d("db","hrdata table exists");
    }else{
    	Log.d("db","hrdata table does not exist");
		db.execSQL("CREATE TABLE " + HR_TABLE_NAME + " ("
				+ "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ heartrate + " INTEGER,"
				+ date + " BIGINT,"
				+ resting + " BOOLEAN"
				+ ");");	              
    }*/
    
    private static final String WARMUP_TABLE_NAME = "warmup";
    private static final String DATABASE_CREATE_TABLE_WARMUP =
    		"create table warmup (" +
    				"_id INTEGER primary key autoincrement," +
    				"intensity_low TEXT," +
    				"intensity_high TEXT," +
    				"progress_precentage TEXT," +
    				"duration INTEGER" +
    				");";
    
    private static final String COOLDOWN_TABLE_NAME = "cooldown";
    private static final String DATABASE_CREATE_TABLE_COOLDOWN =
    		"create table cooldown (" +
    				"_id INTEGER primary key autoincrement," +
    				"intensity_low TEXT," +
    				"intensity_high TEXT," +
    				"progress_precentage TEXT," +
    				"duration INTEGER" +
    				");";
    
    /*
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
    */
    
    private final Context context; 
    
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context myContext) {
        this.context = myContext;
        DBHelper = new DatabaseHelper(context);
    }

	private static class DatabaseHelper extends SQLiteOpenHelper {

		
		 DatabaseHelper(Context context) {
	            super(context, DATABASE_NAME, null, DATABASE_VERSION);
	        }

		@Override
		public void onCreate(SQLiteDatabase db) {
			
			 db.execSQL(DATABASE_CREATE_TABLE_USER);
	            db.execSQL(DATABASE_CREATE_TABLE_PROGRAM);
	            db.execSQL(DATABASE_CREATE_TABLE_PROGRAM_META);
	            db.execSQL(DATABASE_CREATE_TABLE_COOLDOWN);
	            db.execSQL(DATABASE_CREATE_TABLE_WARMUP);
	            db.execSQL(DATABASE_CREATE_TABLE_HEARTRATE);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
			Log.w("FitFormula", "Upgrading database from version " + oldVersion 
                    + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS user");
            db.execSQL("DROP TABLE IF EXISTS program");
            db.execSQL("DROP TABLE IF EXISTS program_meta");
            db.execSQL("DROP TABLE IF EXISTS cooldown");
            db.execSQL("DROP TABLE IF EXISTS warmup");
            db.execSQL("DROP TABLE IF EXISTS heartrate");
            onCreate(db);
			
		}
		
	}//end of DatabaseHelper class
	
	public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }
  
    public void close() {
    	db.close();
    }
    
    /**
     * This method simply returns the count of the rows in the program table.
     * 
     * @return programNum
     */
    private int getLastProgramNumber(){
    	final String statement = "SELECT count(*) from program;"; //TODO We'll find out if this is correct...
    	int programNum = 0;
    	Cursor c = db.rawQuery(statement, new String[] {});//I this ok?
    	c.moveToFirst();
    	programNum = c.getInt(0);
    	return programNum;
    }
    
    /**
     * 
     * Inserts all the necessary rows into the 'program' (TODO and 'user'?) table for a new
     * standardized workout.
     * 
     * @return long
     */
    public long insertNewStandardProgram(long todaysDate){
    	Cursor cursor;
    	int started;
    	long nextMonday = whensNextMonday(todaysDate);
    	if(nextMonday == -1){
    		return -1; //TODO just for now;
    	}
    	
    	int programNumber = getLastProgramNumber();
    	int newProgramNumber;
    	long success = -1;
    	int weekIndex = 0;
    	int dayOfWeekIndex = MONDAY;
    	long date = nextMonday;
    	//Standard "research version" workout values
    	final int[] intensityIndex = {45, 55, 65, 65, 65, 65, 65, 65};
    	final int[] lengthIndex = {20, 25, 30, 30, 30, 30, 30, 30};

    	int makeUpDaysLeft = 2;
    	int day = 0;
    	
    	//Check if the last program in the DB has started
    	cursor = db.query(DATABASE_TABLE_PROGRAM_META, null, null, null, null, null, null);
    	cursor.moveToLast();
    	started = cursor.getInt(cursor.getColumnIndex("started"));// no getBoolean() method
    	if(started == 1){
    		newProgramNumber = programNumber + 1;
    	}else{
    		/*
    		 * If not started, then we will replace the old one
    		 * with this one.
    		 */
    		//TODO SQL DELETE
    		newProgramNumber = programNumber;
    	}
    	
    	try{
	        ContentValues current_program;
	        
	        while(day < TOTAL_STANDARD_PROGRAM_DAYS){
	        	
	        	current_program = new ContentValues();
	        	
		        current_program.put("week", weekIndex); 
		        current_program.put("length", lengthIndex[weekIndex]);
		        current_program.put("day_of_week", dayOfWeekIndex);
		        current_program.put("makeup_days_left", makeUpDaysLeft);
		        current_program.put("intensity", intensityIndex[weekIndex]);
		        current_program.put("completed", false);
		        current_program.put("day", day);
		        current_program.put("scheduled", false);
		        current_program.put("rating", -1);
		        current_program.put("success_completed", false);
		        current_program.put("sched_date", date);
		        current_program.put("program_number", newProgramNumber);
		        
	        	success = db.insert(DATABASE_TABLE_PROGRAM, null, current_program);
		        
	        	if(success == -1){
	        		//TODO may not want to do this...
	        		deleteAllTables(context);
	        		return -1;
	        	}
	        	
	        	//Now set all the variables
	        	day++;
	        	
	        	if(weekIndex == 0){
	        		if(day == 0){
	        			dayOfWeekIndex = WEDNESDAY;
	        		}else if (day == 1){
	        			dayOfWeekIndex = FRIDAY;
	        			makeUpDaysLeft = 3;
	        		} else { //end of the week
	        			weekIndex++;
	        			dayOfWeekIndex = MONDAY;
	        			makeUpDaysLeft = 2;
	        			
	        		}
	        	}else if(weekIndex == 1){
	        		if(dayOfWeekIndex == MONDAY){
	        			dayOfWeekIndex = WEDNESDAY;
	        			makeUpDaysLeft = 1;
	        		}else if (dayOfWeekIndex == WEDNESDAY){
	        			dayOfWeekIndex = THURSDAY;
	        		} else if(dayOfWeekIndex == THURSDAY){
	        			
	        			dayOfWeekIndex = FRIDAY;
	        			makeUpDaysLeft = 3;
	        		}else{ //end of the week
	        			weekIndex++;
	        			dayOfWeekIndex = MONDAY;
	        			makeUpDaysLeft = 2;
	        		}
	        	}else if(weekIndex >= 2 && weekIndex < NUMBER_OF_WEEKS_STANDARD_PROGRAM - 1){
	        		if(dayOfWeekIndex == MONDAY){
	        			dayOfWeekIndex = WEDNESDAY;
	        			makeUpDaysLeft = 1;
	        		}else if(dayOfWeekIndex == WEDNESDAY){
	        			dayOfWeekIndex = THURSDAY;
	        		}else if(dayOfWeekIndex == THURSDAY){
	        			dayOfWeekIndex = FRIDAY;
	        			makeUpDaysLeft = 2;
	        		}else if(dayOfWeekIndex == FRIDAY){
	        			dayOfWeekIndex = SUNDAY;
	        			makeUpDaysLeft = 1;
	        		}else{
	        			weekIndex++;
	        			dayOfWeekIndex = MONDAY;
	        			makeUpDaysLeft = 2;
	        		}
	        	}else { //Last week of program
	        		
	        		if(dayOfWeekIndex == MONDAY){
	        			dayOfWeekIndex = WEDNESDAY;
	        			makeUpDaysLeft = 1;
	        		}else if(dayOfWeekIndex == WEDNESDAY){
	        			dayOfWeekIndex = THURSDAY;
	        		}else if(dayOfWeekIndex == THURSDAY){
	        			dayOfWeekIndex = FRIDAY;
	        			makeUpDaysLeft = 2;
	        		}else if(dayOfWeekIndex == FRIDAY){
	        			dayOfWeekIndex = SUNDAY;
	        			makeUpDaysLeft = 1;
	        		}else{
	        			//done
	        			break;
	        		}
	        	}	
	        }
	        
	        
    	} catch(NullPointerException npe){    
    	
    		Log.e(FitFormulaApp.TAG, "NullPointer in DBAdapter");
	    	return -1;
    	}
    	return -1; //TODO just for now...
    }
    
    private long whensNextMonday(long today){
    	Calendar c =  Calendar.getInstance();
    	int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
    	Log.e("Calendar", "day of the week: " + dayOfWeek);
    	return -1; //TODO just for now...
    }
    
    public void deleteAllTables(Context context) {
    	
    	context.deleteDatabase(DATABASE_NAME);
    	
    }
}
