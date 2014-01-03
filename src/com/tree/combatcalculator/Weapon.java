package com.tree.combatcalculator;

/**
 * @(#)Weapon.java
 *
 *
 *holds the stats for a weapon
 *
 * @author
 * @version 1.00 2012/9/12
 */

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Weapon extends AtkVarUser implements Parcelable{

	private String name = "";
	private int pow;
	private int ROF = -1;
	private int numAtks = 1;
	private boolean is_ranged= false;
	private boolean hasCrit = false;
	private boolean hasCA = false;
	
	//holds the variables for this weapon



	//manually set all variables
    public Weapon(int newPow, boolean isRanged, List<AtkVar> newVariables) {

    	pow = newPow;
    	is_ranged = isRanged;
    	variables = newVariables;

    }
    
  //manually set all variables
    public Weapon(int newPow, boolean isRanged, int rof, List<AtkVar> newVariables) {

    	pow = newPow;
    	is_ranged = isRanged;
    	variables = newVariables;
    	ROF = rof;
    	hasCrit = checkCrits(newVariables);

    }

    //manually set all variables
    public Weapon(int newPow, boolean isRanged, List<AtkVar> newVariables, boolean canCrit) {

    	pow = newPow;
    	is_ranged = isRanged;
    	variables = newVariables;
    	hasCrit = canCrit;


    }
    
    //read in from parcel
    public Weapon(Parcel in){
    	readFromParcel(in);
    }
    

    //manually set all variables
    public Weapon(int newPow, boolean isRanged, ArrayList<AtkVar> newVariables, boolean canCrit, int newROF, int numberAtks) {

    	pow = newPow;
    	is_ranged = isRanged;
    	variables = newVariables;
    	hasCrit = canCrit;
    	ROF = newROF;
    	numAtks = numberAtks;

    }

	//assume no attack variables
    public Weapon(int newPow, boolean isRanged) {

    	pow = newPow;
    	is_ranged = isRanged;
    	variables = new ArrayList<AtkVar>();

    }

	//assume no attack variables, not ranged
    public Weapon(int newPow) {

    	pow = newPow;
    	is_ranged = false;
    	variables = new ArrayList<AtkVar>();

    }

    //returns the pow of the weapon
    public int getPow(){
    	return pow;
    }

    public boolean getRanged(){
    	return is_ranged;
    }

    public boolean getCrit(){
    	return hasCrit;
    }

    public int getROF(){
    	return ROF;
    }

    public boolean getHasCA(){
    	return hasCA;
    }

    public void setHasCA(boolean newCA){
    	hasCA = newCA;
    }


    public int getNumAtks(){
    	return numAtks;
    }
    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    
    private boolean checkCrits(List<AtkVar> newVariables){
    	
    	for (AtkVar variable : newVariables)
    		if (variable.checkCrit())
    			return true;
    	
		return false;
    	
    }
    
  //parcel stuff
  	public int describeContents() {
  		return 0;
  	}

  	
  	public void writeToParcel(Parcel dest, int flags) {

  		dest.writeInt(pow);
  		dest.writeInt(ROF);
  		dest.writeInt(numAtks);
  		boolean[] temp = {is_ranged, hasCrit, hasCA};
  		dest.writeBooleanArray(temp);
  		dest.writeTypedList(variables);
  		dest.writeString(name);
  		
  	}
  	
  	private void readFromParcel(Parcel in){
  		
  		pow = in.readInt();
  		ROF = in.readInt();
  		numAtks = in.readInt();
  		boolean[] temp = new boolean[3];
  		in.readBooleanArray(temp);
  		is_ranged = temp[0];
  		hasCrit = temp[1];
  		hasCA = temp[2];
  		variables = new ArrayList<AtkVar>();
  		in.readTypedList(variables, AtkVar.CREATOR);
  		name = in.readString();
  	}
  	
  	
  	//end parcel stuff
    
    
    /**
    *
    * This field is needed for Android to be able to
    * create new objects, individually or as arrays.
    *
    */
   public static final Parcelable.Creator<Weapon> CREATOR =
   	new Parcelable.Creator<Weapon>() {
           public Weapon createFromParcel(Parcel in) {
               return new Weapon(in);
           }
           
           public Weapon[] newArray(int size) {
               return new Weapon[size];
           }
       };




}