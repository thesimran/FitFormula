package cscece.android.fitformula.objects;

public class HcListItem {
	//constant Strings
	private static final String PHYS_ACT_LEVEL = "Physical Activity Level: ";
	public static final String CARDIO_RISK = "Cardiovascular Risk: ";
	private static final String BMI = "Body Mass Index: ";
	private static final String ACTUAL_AGE = "Actual Age: ";
	private static final String HEART_AGE = "Heart Age: ";
	
	private static final String EXTREMELY_LOW = "Extremely Low";
	private static final String LOW = "Low";
	private static final String BELOW_AVG = "Below Average";
	private static final String AVG = "Average";
	private static final String ABOVE_AVG = "Above Average";
	
	private static final int EXTREMELY_LOW_INT = 0;
	private static final int LOW_INT = 1;
	private static final int BELOW_AVG_INT = 2;
	private static final int AVG_INT = 3;
	private static final int ABOVE_AVG_INT = 4;
	
	private static final int BMI_UNDERWEIGHT_INT = 0;
	private static final int BMI_NORMAL_INT = 1;
	private static final int BMI_OVERWEIGHT_INT = 2;

		
	//List Item type
	public static final int TYPE_PAL = 0;
	public static final int TYPE_CARDIO = 1;
	public static final int TYPE_BMI = 2;
	
	private int type;
	private String title;
	private String cvdClass;
	private String actualAge;
	private String heartAge;
	
	
	/**
	 * Constructor for type 0 items.
	 * 
	 * @param type
	 * @param value
	 */
	public HcListItem(int type, int level) throws IllegalArgumentException{
		
		if(type == TYPE_CARDIO){
			//incorrect
			throw new IllegalArgumentException("You must supply more arguments for a TYPE_CARDIO item!");
		}else{
			this.type = type;
			if(type == TYPE_PAL){
				title = PHYS_ACT_LEVEL + palLevelToString(level);
			}else{
				throw new IllegalArgumentException("You must supply a type -- int and a level -- int param!");
			}	
		}	
	}
	
	/**
	 * Constructor for type 2 items.
	 * 
	 * @param type
	 * @param bmiCLass
	 */
	public HcListItem(int type, String bmiClass) throws IllegalArgumentException{
		
		if(type == TYPE_CARDIO){
			//incorrect
			throw new IllegalArgumentException("You must supply more arguments for a TYPE_CARDIO item!");
		}else{
			this.type = type;
			title = BMI + bmiClass;
		}
		
	}
	
	/**
	 * Constructor for type 1 items.
	 * 
	 * @param type
	 * @param value1
	 * @param value2
	 */
	public HcListItem(int type, String cvdClass, int actualAge, int heartAge) throws IllegalArgumentException{
		if(type == TYPE_CARDIO){
			this.type = type; 
			this.title = CARDIO_RISK + cvdClass;
			this.actualAge = ACTUAL_AGE +  actualAge;
			this.heartAge = HEART_AGE + heartAge;	
		}else{
			//incorrect
			throw new IllegalArgumentException("You must only supply two arguments for TYPE_PAL and TYPE_BMI items!");
		}
	}
	
	
	private String palLevelToString(int level){
		
		if(level == EXTREMELY_LOW_INT){
			return EXTREMELY_LOW;
		}else if(level == LOW_INT){
			return LOW;
		}else if(level == BELOW_AVG_INT){
			return BELOW_AVG;
		}else if(level == AVG_INT){
			return AVG;
		}else if(level == ABOVE_AVG_INT){
			return ABOVE_AVG;
		}else{
			return "None";
		}
	}
	
	public int getType(){
		return type;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getHeartAge(){
		return heartAge;
	}
	
	public String getActualAge(){
		return actualAge;
	}
	
}
