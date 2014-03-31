package com.tree.combatcalculator.nodes;

/**
 * @(#)DecisionNode.java
 *
 *Nodes for decisions such as choosing to boost, type of action
 *
 * @author
 * @version 1.00 2012/10/6
 */


public  abstract class DecisionNode extends Node{

	protected boolean boughtBoost = false;

	public DecisionNode(){
		super();
	}

	public DecisionNode(Node parent){

		super(parent);

	}

	//returns the type of decision
	public int getType(){
		return type;
	}

	//returns the type of decision
	public boolean getBoughtBoost(){
		return boughtBoost;
	}

	public void setBoughtBoost(boolean boughtBoost){
		this.boughtBoost = boughtBoost;
	}






}