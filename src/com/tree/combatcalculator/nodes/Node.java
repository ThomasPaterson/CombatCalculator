package com.tree.combatcalculator.nodes;

/**
 * @(#)Node.java
 *
 *
 *Basic node class for the tree search
 *
 * @author
 * @version 1.00 2012/10/5
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

public abstract class Node implements Parcelable, Comparable<Node>{

	protected Node parent;
	protected ArrayList<Node> children;
	protected int type;
	TemporaryTreeData tempData;
	protected float value;
	protected Type nodeType;
	protected int weaponIndex;

	//types of nodes
	static public int ATTACK = 1;
	static public int DAMAGE = 2;
	static public int BUY = 3;
	static public int END = 4;
	static public int RESULT_ATTACK = 5;
	static public int RESULT_DAMAGE = 6;
	static public String[] TYPE_ARR= {"Attack", "Damage", "Buy", "End", "Result_Attack", "Result_Damage"};
	static public int DECISION_NODE = 7;
	static public int RESULT_NODE = 8;
	
	public enum Type {
		 END("END", "BUY"), 
		 BUY("BUY", "ATTACK_DEC"), 
		 ATTACK_DEC("ATTACK_DEC", "ATTACK_RES"), 
		 ATTACK_RES("ATTACK_RES", "DAMAGE_DEC"), 
		 DAMAGE_DEC("DAMAGE_DEC", "DAMAGE_RES"), 
		 DAMAGE_RES("DAMAGE_RES", "FINAL_RES"),
		 FINAL_RES("FINAL_RES", null);
		 
		 private String name;
		 private String next;
		 
		 private Type(String name, String next) {
		   this.name = name;
		   this.next = next;
		 }
		 
		 public String getName() {
		   return name;
		 }
		 
		 public String getNext() {
			   return next;
		 }
	}

	//parent node constructor
    public Node() {
    	parent = null;
    	children = new ArrayList<Node>();
    }
    
  //parent node constructor
    public Node(Parcel in) {
    	readFromParcel(in);
    }

	//child node constructor
    public Node(Node parent) {
    	this.parent = parent;
    	this.tempData = new TemporaryTreeData(parent.getTempData());
    	this.weaponIndex = parent.getWeaponIndex();
    	parent.addChild(this);
    }

    protected int getWeaponIndex() {
		// TODO Auto-generated method stub
		return weaponIndex;
	}

	//constructor for nodes when we already know the children
    public Node(Node newParent, ArrayList<Node> newChildren){

    	parent = newParent;
    	children = newChildren;

    }
    
    public Node(Node parent, TemporaryTreeData tempData){

    	this.parent = parent;
    	this.tempData = tempData;

    }

	//does a search for the best path on the tree
	abstract public float findBestPath(ArrayList<Node> path, int optMethod);

	//finds the best score from the bottom up, following the chain of decisions to reach that bottom
	abstract public float getScore(Node prevNode, float prevValue);

	//returns type of node
	abstract public int getType();

	//returns value of the node, as defined by children
	abstract public float getValue();
	
	//returns value of the node, as defined by children
	public void setValue(float value){
		this.value = value;
	}


    //get/set functions
    public Node getParent(){
    	return parent;
    }

    public void setParent(Node newParent){
    	parent = newParent;
    }

	//return list of children
    public ArrayList<Node> getChildren(){
    	return children;
    }
    
    public TemporaryTreeData getTempData(){
    	return tempData;
    }
    
    public void setTempData(TemporaryTreeData tempData){
    	this.tempData = new TemporaryTreeData(tempData);
    }

    public Node getChild(int index){
    	return children.get(index);
    }

    public void addChild(Node child){

    	if (children == null)
    		children = new ArrayList<Node>();
    	
    	children.add(child);
    }

    public int getNumChildren(){

    	return children.size();
    }
    
    public Node findParent(Type type){
    	
    	if (!nodeType.equals(type))
    		return parent.findParent(type);
    	else
    		return this;
    	
    }




	//returns if there are any children
    public boolean hasChildren(){

    	if (children.size() > 0)
    		return true;

    	return false;
    }

    //end get/set functions



    //checks up the chain of nodes, sees if it hits a non hit resultattack node
    //used for pruning if there are too many initial attacks
    public boolean isHitChain(){

    	if (parent.getType() == END)
    		return true;
    	else if (parent.getType() == RESULT_ATTACK){

    		if (((ResultNode)parent).getHitType() != ResultNode.HIT)
    			return false;
    		else
    			return parent.isHitChain();
    	}else
    		return parent.isHitChain();


    }//end isHitChain

	//bug testing, prints out the chain from the current node to the top
    public void printChain(){

    	System.out.println("Type: " + type);

    	if (type != END)
    		parent.printChain();
    }

	//checks the number of parents
    public int numberParents(){

    	if (type != END)
    		return parent.numberParents() + 1;
    	else
    		return 0;
    }
    
    /**
	 *Creates all the values from the parcel
	 */
  	private void readFromParcel(Parcel in){
  		

//  		parent = in.readParcelable(null);
//		children = new ArrayList<Node>();
//		in.readTypedList(children, Node.CREATOR);
//		type = in.readInt();
//		focus = in.readInt();
//		sit = new Map<Id, AtkVarCopy>();
//		in.readTypedList(sit, AtkVar.CREATOR);
//		//TODO: weaponCount = new int[10];
//		//TODO: in.readTypedList(weaponCount);
  		
  	}
  		

	
	abstract public void writeToParcel(Parcel dest, int flags);
	
	
	 public static final Parcelable.Creator<Node> CREATOR =
			 new Parcelable.Creator<Node>() {
			           public Node createFromParcel(Parcel in) {
			        	   
			        	   int curType = in.readInt();
			        	   
			        	   if (curType == DECISION_NODE){
			        		   //DecisionNode newD = new DecisionNode(in);
			        		   //return (Node) newD;
			        		   //TODO
			        		   return null;
			        	   }else{
			        		   
			        		   //ResultNode newR = new ResultNode(in);
			        		   //return (Node) newR;
			        		   //TODO
			        		   return null;
			        		   
			        	   }
			        	   		
			           }
			           
			           public Node[] newArray(int size) {
			               return new Node[size];
			           }
			       };


	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int compareTo(Node compareNode) {
		return Float.compare(this.value, compareNode.getValue());
 
	}

	public Type getNodeType() {
		// TODO Auto-generated method stub
		return nodeType;
	}

	public abstract List<Node> createChildren(PermanentTreeData permData);


	public abstract float calculateValue(PermanentTreeData permData);




	
  	
  	//end parcel stuff
	






}