package com.tree.combatcalculator.nodes;

/**
 * @(#)ResultNode.java
 *
 *Stores the results of decisions
 *
 * @author
 * @version 1.00 2012/10/15
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.AtkVarCopy;
import com.tree.combatcalculator.AtkVarCopy.Id;
import com.tree.combatcalculator.PermanentTreeData;
import com.tree.combatcalculator.WeaponCountHolder;

public abstract class ResultNode extends Node {


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
    
    public ResultNode(Node parent) {
    	super(parent);
    }
    
    
    public ResultNode(Parcel in){
    	super(in);
    	
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

	



  	

@Override
public List<Node> createChildren(PermanentTreeData permData) {
	
	return children;
	
}








}