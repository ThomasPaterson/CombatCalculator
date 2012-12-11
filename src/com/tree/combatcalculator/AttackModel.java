package com.tree.combatcalculator;

/**
 * @(#)AttackModel.java
 *
 *contains the stats and variables for an attacking model
 *
 * @author
 * @version 1.00 2012/9/12
 */


import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class AttackModel extends AtkVarUser implements Parcelable {

	private int mat;
	private int rat;
	private boolean usesFocus = false;
	private ArrayList<Weapon> weapons;
	private int numAttackers = 1;


    public AttackModel(int newMat, int newRat){

    	mat = newMat;
    	rat = newRat;
    	weapons = new ArrayList<Weapon>();
    }
    
    /**
	 * Constructor to use when re-constructing object
	 * from a parcel
	 */
	public AttackModel(Parcel in) {
		readFromParcel(in);
	}

    public AttackModel(int newMat, int newRat, ArrayList<AtkVar> newVariables){

    	mat = newMat;
    	rat = newRat;
    	variables = newVariables;
    	weapons = new ArrayList<Weapon>();
    }

	public AttackModel(int newMat, int newRat, ArrayList<AtkVar> newVariables, int newAttackers){

    	mat = newMat;
    	rat = newRat;
    	variables = newVariables;
    	weapons = new ArrayList<Weapon>();
    	numAttackers = newAttackers;
    }
	
	//parcel stuff
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeInt(mat);
		dest.writeInt(rat);
		dest.writeInt(numAttackers);
		boolean[] temp = {usesFocus};
		dest.writeBooleanArray(temp);
		dest.writeTypedList(variables);
		dest.writeTypedList(weapons);
	}
	
	private void readFromParcel(Parcel in){
		
		mat = in.readInt();
		rat = in.readInt();
		numAttackers = in.readInt();
		boolean[] temp = new boolean[1];
		in.readBooleanArray(temp);
		usesFocus = temp[0];
		variables = new ArrayList<AtkVar>();
		in.readTypedList(variables, AtkVar.CREATOR);
		weapons = new ArrayList<Weapon>();
		in.readTypedList(weapons, Weapon.CREATOR);
	}
	
	
	//end parcel stuff


	//getting and setting functions

    public int getMat(){
    	return mat;
    }

    public int getRat(){
    	return rat;
    }

    public int getNumAttackers(){
    	return numAttackers;
    }

     public void setNumAttackers(int newAttackers){
    	numAttackers = newAttackers;
    }


    public ArrayList<Weapon> getWeapons(){
    	return weapons;
    }

    public void setWeapons(ArrayList<Weapon> newWeapons){
    	weapons = newWeapons;
    }

    public void addWeapon(Weapon newWeapon){
    	weapons.add(newWeapon);
    }



    public Weapon getWeapon(int index){
    	return weapons.get(index);
    }

     //set the focus using of the model
    public void setFocus(boolean newState){

    	usesFocus = newState;
    }

    public void setMat(int newMat){
    	mat = newMat;
    }

    public void setRat(int newRat){
    	rat = newRat;
    }

    //end getting and setting
    
    /**
    *
    * This field is needed for Android to be able to
    * create new objects, individually or as arrays.
    *
    * This also means that you can use use the default
    * constructor to create the object and use another
    * method to hyrdate it as necessary.
    *
    * I just find it easier to use the constructor.
    * It makes sense for the way my brain thinks ;-)
    *
    */
   public static final Parcelable.Creator<AttackModel> CREATOR =
   	new Parcelable.Creator<AttackModel>() {
           public AttackModel createFromParcel(Parcel in) {
               return new AttackModel(in);
           }
           
           public AttackModel[] newArray(int size) {
               return new AttackModel[size];
           }
       };





}