package com.tree.combatcalculator;

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

import android.os.Parcel;
import android.os.Parcelable;

public abstract class Node implements Parcelable{

	protected Node parent;
	protected ArrayList<Node> children;
	protected int type;
	protected int focus;
	protected ArrayList<AtkVar> sit;
	protected int[] weaponCount;

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
    public Node(Node newParent) {
    	parent = newParent;
    	children = new ArrayList<Node>();
    }

    //constructor for nodes when we already know the children
    public Node(Node newParent, ArrayList<Node> newChildren){

    	parent = newParent;
    	children = newChildren;

    }

	//does a search for the best path on the tree
	abstract public float findBestPath(ArrayList<Node> path, int optMethod);

	//finds the best score from the bottom up, following the chain of decisions to reach that bottom
	abstract public float getScore(Node prevNode, float prevValue);

	//returns type of node
	abstract public int getType();

	//returns value of the node, as defined by children
	abstract public float getValue();


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

    public Node getChild(int index){
    	return children.get(index);
    }

    public void addChild(Node child){

    	children.add(child);
    	child.setParent(this);
    }

    public int getNumChildren(){

    	return children.size();
    }

    public ArrayList<AtkVar> getSit(){
		return sit;
	}

	//returns weaponCount, a which tracks how many attacks each weapon has made
	public int[] getWeaponCount(){
		return weaponCount;
	}

	public int getFocus(){
		return focus;
	}

	//creates a new copy of the situation the node is in
	public void setSit(ArrayList<AtkVar> newSit){
		sit = new ArrayList<AtkVar>(newSit);
	}

	public void setFocus(int newFocus){
		focus = newFocus;
	}

	public void setWeaponCount(int[] curWeapons){
		weaponCount = curWeapons;
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
  		

  		parent = in.readParcelable(null);
		children = new ArrayList<Node>();
		in.readTypedList(children, Node.CREATOR);
		type = in.readInt();
		focus = in.readInt();
		sit = new ArrayList<AtkVar>();
		in.readTypedList(sit, AtkVar.CREATOR);
		weaponCount = new int[10];
		in.readIntArray(weaponCount);
  		
  	}
  		

	
	abstract public void writeToParcel(Parcel dest, int flags);
	
	
	 public static final Parcelable.Creator<Node> CREATOR =
			 new Parcelable.Creator<Node>() {
			           public Node createFromParcel(Parcel in) {
			        	   
			        	   int curType = in.readInt();
			        	   
			        	   if (curType == DECISION_NODE){
			        		   DecisionNode newD = new DecisionNode(in);
			        		   return (Node) newD;
			        	   }else{
			        		   
			        		   ResultNode newR = new ResultNode(in);
			        		   return (Node) newR;
			        		   
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
	
  	
  	//end parcel stuff
	






}