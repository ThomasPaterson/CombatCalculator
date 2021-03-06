package com.tree.combatcalculator;

/**
 * @(#)AtkVar.java
 *
 *A class for managing variables and holding all the values/names for the various possibilities
 *
 * @author
 * @version 1.00 2012/9/12
 */
import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class AtkVar implements Parcelable {
	//name of the variable
	private String name;
	//type of variable
	private String type = "";
	//value of the variable
	private int value = 0;
	//target of the variable, so mat/rat, pow, def, arm
	private String target = BLANK;
	//trigger for the variable to take place
	private ArrayList<String> conditions = new ArrayList<String>();

	private boolean hasCond = false;

	//types
	public static String BOOST = "boost";
	public static String ADD = "add_die";
	public static String MOD = "mod_var";
	public static String REROLL = "reroll";
	public static String RANGED = "ranged";
	public static String KNOCKDOWN = "knockdown";
	public static String DISCARD = "DISCARD";
	public static String CRIT = "critical";
	public static String FOCUS_VALUE = "focus_value";
	public static String SPECIAL = "special";
	public static String BLANK = "blank";


	//targets
	public static String ATK = "attack_value";
	public static String ATK_ROLL = "attack_roll";
	public static String POW = "power";
	public static String DAM_ROLL = "damage";
	public static String DEF = "defense";
	public static String ARM = "arm";
	public static String FOCUS = "focus";

	//names
	public static String AIM = "AIM";
	public static String COVER = "COVER";
	public static String CONCEALMENT = "CONCEALMENT";
	public static String ELEVATION = "ELEVATION";
	public static String AUTO_HIT = "AUTO_HIT";
	public static String AUTO_CRIT = "AUTO_CRIT";
	public static String FREE_CHARGE = "FREE_CHARGE";
	public static String CHARGING = "CHARGING";
	public static String CHARGE_DAMAGE = "CHARGE_DAMAGE";
	public static String BOOSTED_HIT = "BOOSTED_HIT";
	public static String BOOSTED_DAM = "BOOSTED_DAM";
	public static String BOOSTED_HIT_FOCUS = "BOOSTED_HIT_FOCUS";
	public static String BOOSTED_DAM_FOCUS = "BOOSTED_DAM_FOCUS";
	public static String ADD_HIT = "ADD_HIT";
	public static String ADD_DAM = "ADD_DAM";
	public static String MOD_ATK = "MOD_ATK";
	public static String MOD_POW = "MOD_POW";
	public static String MOD_DEF = "MOD_DEF";
	public static String MOD_ARM = "MOD_ARM";
	public static String CRIT_KNOCKDOWN = "CRIT_KNOCKDOWN";
	public static String CRIT_DAM = "CRIT_DAM";
	public static String CRIT_ATK = "CRIT_ATK";
	public static String DISCARD_ATK = "DISCARD_ATK";
	public static String DISCARD_DAM = "DISCARD_DAM";
	public static String REROLL_ATK = "REROLL_ATK";
	public static String REROLL_DAM = "REROLL_DAM";
	public static String ARM_PIERCING = "ARM_PIERCING";
	public static String HAS_FOCUS = "HAS_FOCUS";
	public static String GUNFIGHTER = "GUNFIGHTER";
	public static String POINT_BLANK = "POINT_BLANK";
	public static String FIRST_ATTACK = "FIRST_ATTACK";
	public static String KNOCKED_DOWN = "KNOCKED_DOWN";
	public static String SHIELD = "SHIELD";
	public static String SHIELD_BONUS = "SHIELD_BONUS";
	public static String SHIELD_WALL = "SHIELD_WALL";
	public static String BUCKLER = "BUCKLER";
	public static String DOUBLE_DAMAGE = "DOUBLE_DAMAGE";
	public static String WEAPON_MASTER = "WEAPON_MASTER";
	public static String POWERFUL_ATK_AB = "POWERFUL_ATK_AB";
	public static String POWERFUL_ATK = "POWERFUL_ATK";
	public static String SUSTAINED_ATK = "SUSTAINED_ATK";
	public static String SUSTAINED_ATK_AB = "SUSTAINED_ATK_AB";
	public static String POWERFUL_CHARGE = "POWERFUL_CHARGE";
	public static String BRUTAL_CHARGE = "BRUTAL_CHARGE";
	public static String MULT_ATKERS = "MULT_ATKERS";
	public static String COMBINED = "COMBINED";
	public static String CRA = "CRA";
	public static String CMA = "CMA";
	public static String ASSAULT = "ASSAULT";
	public static String SET_DEFENSE = "SET_DEFENSE";
	public static String CAMOFLAUGE = "CAMOFLAUGE";


	//conditions
	public static String IS_CHARGING = "is_CHARGING";
	public static String IS_COVER_OR_CONCEALMENT = "IS_COVER_OR_CONCEALMENT";
	public static String IS_RANGED = "is_ranged";
	public static String GET_CRIT = "get_crit";
	public static String IS_FIRST_ATTACK = "is_first_attack";


    public AtkVar(String newName) {
    	name = newName;

    	setValues();
    }

    public AtkVar(String newName, int newValue) {
    	name = newName;
    	value = newValue;
    	setValues();
    }
    
    /**
	 
	 * Constructor to use when re-constructing object
	 * from a parcel
	 */
    public AtkVar(Parcel in) {
		readFromParcel(in);
	}

	//adds a new condition
    public void addCondition(String newCond){
    	conditions.add(newCond);
    }



	//converts the name into the required conditions, targets, and types
    private void setValues(){

    	if (name.equals(AIM)){
    		conditions.add(IS_RANGED);
    		target = ATK;
    		type = MOD;
    		value = 2;

    	}else if (name.equals(COVER)){

    		conditions.add(IS_RANGED);
    		target = ATK;
    		type = MOD;
    		value = -4;

    	}else if (name.equals(CAMOFLAUGE)){

    		conditions.add(IS_RANGED);
    		conditions.add(IS_COVER_OR_CONCEALMENT);
    		target = DEF;
    		type = MOD;
    		value = 2;

    	}else if (name.equals(CONCEALMENT)){

    		conditions.add(IS_RANGED);
    		target = ATK;
    		type = MOD;
    		value = -2;

    	}else if (name.equals(ELEVATION)){

    		conditions.add(IS_RANGED);
    		target = ATK;
    		type = MOD;
    		value = -2;

    	}else if (name.equals(AUTO_HIT)){

    		target = ATK_ROLL;
    		type = SPECIAL;

    	}else if (name.equals(FREE_CHARGE)){

    		conditions.add(IS_CHARGING);
    		conditions.add(FIRST_ATTACK);
    		target = FOCUS;
    		type = MOD;
    		value = 1;

    	}else if (name.equals(SET_DEFENSE)){

    		conditions.add(IS_CHARGING);
    		target = DEF;
    		type = MOD;
    		value = 2;

    	}else if (name.equals(CHARGING)){

    		target = FOCUS;
    		type = MOD;
    		value = -1;

    	}else if (name.equals(CHARGE_DAMAGE)){

    		target = DAM_ROLL;
    		type = BOOST;

    	}else if (name.equals(BOOSTED_HIT)){

    		target = ATK_ROLL;
    		type = BOOST;
    		value = 1;

    	}else if (name.equals(BOOSTED_HIT_FOCUS)){

			name = BOOSTED_HIT;
    		target = ATK_ROLL;
    		type = BOOST;
    		value = -1;

    	}else if (name.equals(BOOSTED_DAM)){

    		target = DAM_ROLL;
    		type = BOOST;
    		value = 1;

    	}else if (name.equals(BOOSTED_DAM_FOCUS)){

			name = BOOSTED_DAM;
    		target = DAM_ROLL;
    		type = BOOST;
    		value = -1;

    	}else if (name.equals(ADD_HIT)){

    		target = ATK_ROLL;
    		type = ADD;

    	}else if (name.equals(ADD_DAM)){

    		target = DAM_ROLL;
    		type = ADD;

    	}else if (name.equals(WEAPON_MASTER)){

    		target = DAM_ROLL;
    		type = ADD;
    		value = 1;

    	}else if (name.equals(MOD_ATK)){

    		target = ATK;
    		type = MOD;

    	}else if (name.equals(MOD_POW)){

    		target = POW;
    		type = MOD;

    	}else if (name.equals(MOD_DEF)){

    		target = DEF;
    		type = MOD;

    	}else if (name.equals(MOD_ARM)){

    		target = ARM;
    		type = MOD;

    	}else if (name.equals(SHIELD)){

    		target = ARM;
    		type = MOD;
    		value = 2;

    	}else if (name.equals(SHIELD_BONUS)){

    		target = ARM;
    		type = MOD;

    	}else if (name.equals(BUCKLER)){

    		target = ARM;
    		type = MOD;
    		value = 1;

    	}else if (name.equals(SHIELD_WALL)){

    		target = ARM;
    		type = MOD;
    		value = 4;

    	}else if (name.equals(CRIT_KNOCKDOWN)){

    		target = SPECIAL;
    		type = KNOCKDOWN;
    		conditions.add(GET_CRIT);

    	}else if (name.equals(CRIT_DAM)){

    		target = DAM_ROLL;
    		type = ADD;
    		value = 1;
    		conditions.add(GET_CRIT);
    		System.out.println(conditions.get(0));

    	}else if (name.equals(CRIT_ATK)){

    		target = SPECIAL;
    		type = SPECIAL;
    		conditions.add(GET_CRIT);

    	}else if (name.equals(DISCARD_ATK)){

    		target = ATK_ROLL;
    		type = DISCARD;

    	}else if (name.equals(DISCARD_DAM)){

    		target = DAM_ROLL;
    		type = DISCARD;

    	}else if (name.equals(REROLL_ATK)){

    		target = ATK_ROLL;
    		type = REROLL;

    	}else if (name.equals(REROLL_DAM)){

    		target = DAM_ROLL;
    		type = REROLL;

    	}else if (name.equals(ARM_PIERCING)){

    		target = DAM_ROLL;
    		type = SPECIAL;

    	}else if (name.equals(RANGED)){

    		target = BLANK;
    		type = BLANK;

    	}else if (name.equals(GUNFIGHTER)){

    		target = BLANK;
    		type = SPECIAL;

    	}else if (name.equals(POINT_BLANK)){

    		target = BLANK;
    		type = SPECIAL;

    	}else if (name.equals(DOUBLE_DAMAGE)){

    		target = BLANK;
    		type = SPECIAL;

    	}else if (name.equals(AUTO_HIT)){

    		target = BLANK;
    		type = SPECIAL;

    	}else if (name.equals(AUTO_CRIT)){

    		target = BLANK;
    		type = SPECIAL;

    	}else if (name.equals(KNOCKDOWN)){

    		target = BLANK;
    		type = SPECIAL;

    	}else if (name.equals(POWERFUL_ATK_AB)){

    		target = BLANK;
    		type = SPECIAL;

    	}else if (name.equals(POWERFUL_ATK)){

    		target = BLANK;
    		type = SPECIAL;

    	}else if (name.equals(SUSTAINED_ATK)){

    		target = BLANK;
    		type = SPECIAL;

    	}else if (name.equals(SUSTAINED_ATK_AB)){

    		target = BLANK;
    		type = SPECIAL;

    	}else if (name.equals(MULT_ATKERS)){

    		target = BLANK;
    		type = SPECIAL;

    	}else if (name.equals(COMBINED)){

    		target = BLANK;
    		type = SPECIAL;

    	}else if (name.equals(CRA)){

    		target = BLANK;
    		type = SPECIAL;

    	}else if (name.equals(CMA)){

    		target = BLANK;
    		type = SPECIAL;

    	}else if (name.equals(ASSAULT)){

    		target = BLANK;
    		type = SPECIAL;

    	}







    }//end setValues


	//get set functions

    public String getName(){
    	return name;
    }

    public String getType(){
    	return type;
    }

    public String getTarget(){
    	return target;
    }

    public int getValue(){
    	return value;
    }

    //end get set functions


	//removes a variable from an arraylist, using the variable to remove
    public static void removeVariable(ArrayList<AtkVar> variables, AtkVar remove){

    	for (int i = 0; i < variables.size(); i++){

    		if (variables.get(i).getName().equals(remove.getName())){
    			variables.remove(i);
    			break;
    		}

    	}


    }//end removeVariable

    //removes a variable from an arraylist, using the variable to remove
    public static void removeVariable(List<AtkVar> variables, String reName){

    	for (int i = 0; i < variables.size(); i++){

    		if (variables.get(i).getName().equals(reName)){
    			System.out.println("removing: " + reName);
    			variables.remove(i);
    			break;
    		}

    	}

    }//end removeVariable
    
  //returns either the atkvar requested, or null if nothing is there
    public static AtkVar getAtkVarByName(ArrayList<AtkVar> variables, String nameToCheck){
    	
    	for (int i = 0; i < variables.size(); i++)
    		if (variables.get(i).getName().equals(nameToCheck))
    			return variables.get(i);


    	return null;
    	
    	
    }


	//checks to see if an arraylist of atkvars contains one of a specific variable
    public static boolean checkContainsName(List<AtkVar> variables, String nameToCheck){

    	for (int i = 0; i < variables.size(); i++)
    		if (variables.get(i).getName().equals(nameToCheck))
    			return true;


    	return false;

    }//end removeVariable
    


    //checks to see if an arraylist of atkvars contains one of a specific variable
    public static boolean checkContainsName(List<AtkVar> variables1, List<AtkVar> variables2,
    		List<AtkVar> variables3, String nameToCheck){


    	for (int i = 0; i < variables1.size(); i++)
    		if (variables1.get(i).getName().equals(nameToCheck))
    			return true;

    	for (int i = 0; i < variables2.size(); i++)
    		if (variables2.get(i).getName().equals(nameToCheck))
    			return true;

    	for (int i = 0; i < variables3.size(); i++)
    		if (variables3.get(i).getName().equals(nameToCheck))
    			return true;

    	return false;


    }//end checkContainsName



    //checks all the conditions to see if there is one that is invalid
    public boolean meetsConditions(List<AtkVar> variables){

		ArrayList<String> targetString = new ArrayList<String>();

		//loop through all conditions
		for (String condition : conditions){

			//determine the targetString by the type of condition
			if (condition.equals(IS_CHARGING))
				targetString.add(CHARGING);
			else if (condition.equals(IS_RANGED))
				targetString.add(RANGED);
			else if (condition.equals(GET_CRIT))
				targetString.add(CRIT);
			else if (condition.equals(IS_FIRST_ATTACK))
				targetString.add(FIRST_ATTACK);
			else if (condition.equals(IS_COVER_OR_CONCEALMENT)){
				targetString.add(COVER);
				targetString.add(CONCEALMENT);
				
			}



			boolean found = false;

			//loop through all the variables provided, if it is in there, continue onto the next one
			//otherwise break the loop
    		for (AtkVar variable : variables){
    			
    			for (String s : targetString){
    				
    				if (variable.getName().equals(s)){
        				found = true;
        				break;
        			}
    				
    			}

    			if (found)
    				break;

    		}//end variables loop

			//if a condition is unsastified, return false
    		if (!found)
				return false;


		}//end conditions loop

    	return true;


    }//end meetsConditions

    //checks each atkvar in one list, to make sure there is the same one in the other
    static public boolean equalAtkVarList(ArrayList<AtkVar> list1, ArrayList<AtkVar> list2){


		for (AtkVar l1 : list1)
			if (!checkContainsName(list2, l1.getName()))
				return false;

		for (AtkVar l2 : list2)
			if (!checkContainsName(list1, l2.getName()))
				return false;


		return true;

    }//end equalAtkVarList


    //removes boosts, but only ones that are created by spending focus
    static public ArrayList<AtkVar> removeFocusBoost(ArrayList<AtkVar> sit){

    	ArrayList<AtkVar> newSit = new ArrayList<AtkVar>();

    	for (AtkVar a : sit){

    		if (a.getName().equals(BOOSTED_HIT) || a.getName().equals(BOOSTED_DAM))
    			if (a.getValue() != -1)
    				newSit.add(a);
    		else
    			newSit.add(a);

    	}


		return newSit;

    }//end removeFocusBoost


  	public ArrayList<String> getConditions(){
   		return conditions;
   	}
  	
  	public boolean checkCrit(){
  		return conditions.contains(GET_CRIT);
  	}
  	
  	//parcel stuff
  	
  	public int describeContents() {
		return 0;
	}
  	
  	//*
  	
  	/**
	 *Creates all the values from the parcel
	 */
  	private void readFromParcel(Parcel in){
  		
  		name = in.readString();
  		type = in.readString();
  		value = in.readInt();
  		target = in.readString();
  		conditions = new ArrayList<String>();
  		in.readStringList(conditions);
  		
  	}

	
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(name);
		dest.writeString(type);
		dest.writeInt(value);
		dest.writeString(target);
		dest.writeStringList(conditions);
	}
	
  	
  	
  	//end parcel stuff
	
	/**
    *
    * This field is needed for Android to be able to
    * create new objects, individually or as arrays.
    *

    */
   public static final Parcelable.Creator<AtkVar> CREATOR =
   	new Parcelable.Creator<AtkVar>() {
           public AtkVar createFromParcel(Parcel in) {
               return new AtkVar(in);
           }
           
           public AtkVar[] newArray(int size) {
               return new AtkVar[size];
           }
       };






}