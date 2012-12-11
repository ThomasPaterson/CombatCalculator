package com.tree.combatcalculator;

/**
* @(#)DefendModel.java
 *
 *Contains the information for the defending model
 *
 * @author
 * @version 1.00 2012/9/13
 */

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class DefendModel extends AtkVarUser implements Parcelable {

	//defense stat, need to surpass to hit
	private int def;
	//arm stat, need to surpass to damage
	private int arm;
	//amount of damage needed to kill
	private int health;


    public DefendModel(int d, int a) {

    	def = d;
    	arm = a;
    	health = 1;
    }

    public DefendModel(int d, int a, int newHealth) {

    	def = d;
    	arm = a;
    	health = newHealth;
    }
    
    /**
	 * Constructor to use when re-constructing object
	 * from a parcel
	 */
	public DefendModel(Parcel in) {
		readFromParcel(in);
	}


     public DefendModel(int d, int a, ArrayList<AtkVar> newVariables) {

    	def = d;
    	arm = a;
    	health = 1;
    	variables = newVariables;
    }


    public int getDef(){
    	return def;
    }

    public int getArm(){
    	return arm;
    }

    public int getHealth(){
    	return health;
    }
    
    public void setDef(int newVar){
    	def = newVar;
    }

    public void setArm(int newVar){
    	arm = newVar;
    }



    public void setHealth(int newHealth){
    	health = newHealth;
    }
    
	//parcel stuff
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeInt(def);
		dest.writeInt(arm);
		dest.writeInt(health);
		dest.writeTypedList(variables);
	}
	
	private void readFromParcel(Parcel in){
		
		def = in.readInt();
		arm = in.readInt();
		health = in.readInt();
		variables = new ArrayList<AtkVar>();
		in.readTypedList(variables, AtkVar.CREATOR);
	}
	
	
	//end parcel stuff


    /**
    *
    * This field is needed for Android to be able to
    * create new objects, individually or as arrays.
    *
    */
   public static final Parcelable.Creator<DefendModel> CREATOR =
   	new Parcelable.Creator<DefendModel>() {
           public DefendModel createFromParcel(Parcel in) {
               return new DefendModel(in);
           }
           
           public DefendModel[] newArray(int size) {
               return new DefendModel[size];
           }
       };





}