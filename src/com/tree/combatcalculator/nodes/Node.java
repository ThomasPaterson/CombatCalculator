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

import com.tree.combatcalculator.StaticAttackData;
import com.tree.combatcalculator.DynamicAttackData;

public abstract class Node implements Comparable<Node>{

	protected Node parent;
	protected ArrayList<Node> children;
	protected int type;
	DynamicAttackData tempData;
	protected float value;
	protected Type nodeType;
	protected int weaponIndex;
	static public Type TERMINUS_TYPE = Type.FINAL_RES;

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
		static final Map<String,Type> typeMap =
				new HashMap<String,Type>();
		static {
			for (Type t : Type.values())
				typeMap.put(t.toString(), t);
		}

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

		public static Type findType(String type){
			return typeMap.get(type);
		}
	}

	//parent node constructor
	public Node() {
		parent = null;
		children = new ArrayList<Node>();
	}



	//child node constructor
	public Node(Node parent) {
		this.parent = parent;
		this.tempData = new DynamicAttackData(parent.getTempData());
		this.weaponIndex = parent.getWeaponIndex();
		parent.addChild(this);
	}

	public int getWeaponIndex() {
		return weaponIndex;
	}

	//constructor for nodes when we already know the children
	public Node(Node newParent, ArrayList<Node> newChildren){

		parent = newParent;
		children = newChildren;

	}

	public Node(Node parent, DynamicAttackData tempData){

		this.parent = parent;
		this.tempData = tempData;

	}

	public float getValue(){
		return value;
	}

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

	public DynamicAttackData getTempData(){
		return tempData;
	}

	public void setTempData(DynamicAttackData tempData){
		this.tempData = new DynamicAttackData(tempData);
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




	@Override
	public int compareTo(Node compareNode) {
		return Float.compare(this.value, compareNode.getValue());

	}

	public Type getNodeType() {
		return nodeType;
	}

	public abstract List<Node> createChildren(StaticAttackData permData);


	public abstract float calculateValue(StaticAttackData permData);






	//end parcel stuff







}