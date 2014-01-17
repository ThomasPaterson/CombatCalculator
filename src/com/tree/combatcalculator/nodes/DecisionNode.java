package com.tree.combatcalculator.nodes;

/**
 * @(#)DecisionNode.java
 *
 *Nodes for decisions such as choosing to boost, type of action
 *
 * @author
 * @version 1.00 2012/10/6
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.tree.combatcalculator.AtkVarCopy;
import com.tree.combatcalculator.AtkVarCopy.Id;
import com.tree.combatcalculator.PermanentTreeData;
import com.tree.combatcalculator.TemporaryTreeData;
import com.tree.combatcalculator.WeaponCountHolder;

public  abstract class DecisionNode extends Node implements Parcelable{

	private float total = 0;
	private ArrayList<Node> pathTo = new ArrayList<Node>();
	private int comboNum;
	private boolean boughtBoost = false;



	//basic creator, will be created by the TreeCreator class
    public DecisionNode() {

    }

    //basic creator, will be created by the TreeCreator class
    public DecisionNode(int newType) {

    	type = newType;
    	boughtBoost = false;
    	value = -1;
    }
    
    public DecisionNode(Parcel in){
    	super(in);
    	readFromParcel(in);
    }



	
	public DecisionNode(Node parent){
		
    	super(parent);

    }

	//finds the best path from this decision node by checking all children, then returning the result
	//of the best one, and adding itself to the array
	public float findBestPath(ArrayList<Node> path, int optMethod){

		float bestValue = 0;

		pathTo = new ArrayList<Node>();

		//storage container for all potential paths
		ArrayList<ArrayList<Node>> newPaths = new ArrayList<ArrayList<Node>>();
		ArrayList<Node> newPath;

		//default index is 0, because some decision nodes only have one child
		int bestIndex = 0;

		//if it has decision nodes as children, search for the best one
		if (children.get(0) instanceof DecisionNode){

			//search for the best path, send down the current path to be modified
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


		//if it is result nodes that are children, return the sum of them
		}else{


			for (int j = 0; j < children.size(); j++){

				newPath = new ArrayList<Node>();
				newPaths.add(newPath);

				//for hit nodes, only take the path with hits for remembering the trail, but
				//still use the values of the other paths
				if ((children.get(j).getType() == Node.RESULT_ATTACK)){

					if (((ResultNode)children.get(j)).getHitType() == ResultNode.HIT)
						bestIndex = j;

					bestValue += children.get(j).findBestPath(newPath, optMethod);



				//for damage nodes, since there will only be one, just use that path
				}else{

					bestValue += children.get(j).findBestPath(newPath, optMethod);

				}

			}

		}


		//merge the path with the best results, including the current node
		path.add(this);
		path.addAll(newPaths.get(bestIndex));

		//save value up to this point and the optimal route to this point
		total = bestValue;
		pathTo = new ArrayList<Node>(newPaths.get(bestIndex));

		return bestValue;

	}//end findBestPath


	//finds the best score from the bottom up, following the chain of decisions to reach that bottom
	public float getScore(Node prevNode, float prevValue){

		//just go up the chain, unless it is an attack, in which case take the optimal damage from the other
		//chains created from missing/critting
		if (type == Node.END)
			return prevValue;
		else if (type == Node.BUY)
			return parent.getScore(this, prevValue);
		else if (type == Node.DAMAGE)
			return parent.getScore(this, prevValue);
		else if (type == Node.ATTACK){

			float addDamage = 0;

			//check the other chains, add their damage, assuming optimal (taking totals)
			for (int i = 0; i < children.size(); i++){

				//check the other potential result_attack nodes
				if (!getChild(i).equals(prevNode)){

					//total them up for correct determination of the score for this attack
					for (int j = 0; j < getChild(i).getChildren().size(); j++)
						addDamage += getChild(i).getValue() * ((DecisionNode)getChild(i).getChild(j)).getTotal();

				}

			}

			return parent.getScore(this, prevValue + addDamage);

		}else
			return -1;

	}

	//returns the type of decision
	public int getType(){
		return type;
	}

	//returns the type of decision
	public boolean getBoughtBoost(){
		return boughtBoost;
	}

	public float getValue(){
		return value;
	}

	public float getTotal(){
		return total;
	}

	public int getComboNum(){
		return comboNum;
	}

	public void setComboNum(int newComboNum){
		comboNum = newComboNum;
	}
	
	public void setBoughtBoost(boolean boughtBoost){
		this.boughtBoost = boughtBoost;
	}

	public ArrayList<Node> getPathTo(){
		return pathTo;
	}
	
	  /**
		 *Creates all the values from the parcel
		 */
	  	protected void readFromParcel(Parcel in){
	  		
			
			boolean[] boostArr = new boolean[1];
			in.readBooleanArray(boostArr);
			boughtBoost = boostArr[0];
			value = in.readFloat();
			total = in.readFloat();
			pathTo = new ArrayList<Node>();
			in.readTypedList(pathTo, Node.CREATOR);
			comboNum = in.readInt();
	  		
	  	}
	  		

		
		@Override
		public void writeToParcel(Parcel dest, int flags){
			
//			dest.writeInt(Node.DECISION_NODE);
//			dest.writeParcelable(parent, 0);
//			dest.writeTypedList(children);
//			dest.writeInt(type);
//			dest.writeInt(focus);
//			dest.writeTypedList(sit);
//			//TODO: dest.writeIntArray(weaponCount);
//			boolean[] boostArr = new boolean[1];
//			boostArr[0] = boost;
//			dest.writeBooleanArray(boostArr);
//			dest.writeFloat(value);
//			dest.writeFloat(total);
//			dest.writeTypedList(pathTo);
//			dest.writeInt(comboNum);
//			
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
   public static final Parcelable.Creator<DecisionNode> CREATOR =
   	new Parcelable.Creator<DecisionNode>() {
           public DecisionNode createFromParcel(Parcel in) {
               //TODO: return new DecisionNode(in);
               return null;
           }
           
           public DecisionNode[] newArray(int size) {
               return new DecisionNode[size];
           }
       };



	@Override
	public abstract List<Node> createChildren(PermanentTreeData permData);

	@Override
	public abstract void calculateValue();
	


}