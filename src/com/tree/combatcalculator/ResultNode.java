package com.tree.combatcalculator;

/**
 * @(#)ResultNode.java
 *
 *Stores the results of decisions
 *
 * @author
 * @version 1.00 2012/10/15
 */

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;

public class ResultNode extends Node implements Parcelable{


	private float value;

	//for tracking types of result_attack nodes results
	private int hitType;

	public static int HIT = 0;
	public static int MISS = 1;
	public static int CRIT = 2;
	public static int[] HIT_CAT = {HIT, MISS, CRIT};
	public static String[] STRING_CAT = {"HIT", "MISS", "CRIT"};

    public ResultNode(int newType, float newValue) {
    	type = newType;
    	value = newValue;
    }
    
    public ResultNode(Parcel in){
    	super(in);
    	readFromParcel(in);
    	
    }

    public ResultNode(int newType, float newValue, int newFocus, ArrayList<AtkVar> newSit, int[] newWeapons) {
    	type = newType;
    	value = newValue;
    	focus = newFocus;
    	sit = new ArrayList<AtkVar>(newSit);
		weaponCount = Arrays.copyOf(newWeapons, newWeapons.length);
    }

    public ResultNode(int newType, float newValue, int newHitType) {
    	type = newType;
    	value = newValue;
    	hitType = newHitType;
    }


	//returns the type of node
	public int getType(){
		return type;
	}

	//returns the type of result
	public int getHitType(){
		return hitType;
	}

	public float getValue(){
		return value;
	}


    //returns the value of the result and the value of any children it has afterwards
	public float findBestPath(ArrayList<Node> path, int optMethod){

		//if (type == Node.RESULT_DAMAGE)
		//S	System.out.println(numberParents());

		ArrayList<ArrayList<Node>> newPaths = new ArrayList<ArrayList<Node>>();
		ArrayList<Node> newPath;

		//if it has decision nodes as children, search for the best one
		//result nodes will always have decision nodes as children
		if (!children.isEmpty()){



			//search for the best path, send down the current path to be modified
			int bestIndex = 0;
			float bestValue = 0;


			for (int i = 0; i < children.size(); i++){

				newPath = new ArrayList<Node>();
				newPaths.add(newPath);

				float testValue = children.get(i).findBestPath(newPath, optMethod);

				//save the result of the best path
				if (testValue >= bestValue){
					bestIndex = i;
					bestValue = testValue;
				}

			}

			path.addAll(newPaths.get(bestIndex));

			//if it is a % chance to kill or an attack, multiply
			if (type == Node.RESULT_ATTACK)
				return value * bestValue;
			else
				return value + bestValue;

		}else
			return value;




	}//end findBestPath

	//finds the best score starting from the bottom
	public float getScore(Node prevNode, float prevValue){

		if (type == Node.RESULT_ATTACK)
			return parent.getScore(prevNode, value*prevValue);
		else if (type == Node.RESULT_DAMAGE)
			return parent.getScore(prevNode, value+prevValue);
		else
			return prevValue;

	}//end getScore

	
	/**
	 *Creates all the values from the parcel
	 */
  	protected void readFromParcel(Parcel in){
  		
  		value = in.readFloat();
		hitType = in.readInt();
  		
  	}
  		

	
	@Override
	public void writeToParcel(Parcel dest, int flags){
		
		dest.writeInt(Node.RESULT_NODE);
		dest.writeParcelable(parent, 0);
		dest.writeTypedList(children);
		dest.writeInt(type);
		dest.writeInt(focus);
		dest.writeTypedList(sit);
		dest.writeIntArray(weaponCount);
		dest.writeFloat(value);
		dest.writeInt(hitType);
	}

	//parcel stuff
	@Override
	public int describeContents() {
		return 0;
	}
	
  	
  	//end parcel stuff

/**
*
* This field is needed for Android to be able to
* create new objects, individually or as arrays.
*

*/
public static final Parcelable.Creator<ResultNode> CREATOR =
	new Parcelable.Creator<ResultNode>() {
       public ResultNode createFromParcel(Parcel in) {
           return new ResultNode(in);
       }
       
       public ResultNode[] newArray(int size) {
           return new ResultNode[size];
       }
   };







}