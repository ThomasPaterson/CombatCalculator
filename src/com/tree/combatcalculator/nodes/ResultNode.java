package com.tree.combatcalculator.nodes;

import com.tree.combatcalculator.AttackCalculation.AttackResult;

/**
 * @(#)ResultNode.java
 *
 *Stores the results of decisions
 *
 * @author
 * @version 1.00 2012/10/15
 */


public abstract class ResultNode extends Node {

	AttackResult result;


	public ResultNode(Node parent) {
		super(parent);
	}


	public AttackResult getResult() {
		return result;
	}


	public void setResult(AttackResult result) {
		this.result = result;
	}



}