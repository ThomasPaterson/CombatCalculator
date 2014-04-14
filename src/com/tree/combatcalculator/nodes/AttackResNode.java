package com.tree.combatcalculator.nodes;

import java.util.ArrayList;
import java.util.List;

import com.tree.combatcalculator.StaticAttackData;
import com.tree.combatcalculator.AttackCalculation.AttackCalculator;
import com.tree.combatcalculator.AttackCalculation.AttackCalculatorFactory;
import com.tree.combatcalculator.AttackCalculation.AttackResult;

public class AttackResNode extends ResultNode {

	public AttackResNode(Node parent) {

		super(parent);
		nodeType = Node.Type.ATTACK_RES;

	}


	public static List<Node> createAttackResNodes(Node parent, StaticAttackData permData) {

		List<Node> attackResNodes = new ArrayList<Node>();

		attackResNodes.add(makeAttackResult(parent, permData));


		return attackResNodes;
	}


	private static Node makeAttackResult(Node parent, StaticAttackData permData) {

		Node attackResNode = new AttackResNode(parent);

		AttackCalculator atkCalculator = AttackCalculatorFactory.getInstance();
		AttackResult attackResult = atkCalculator.getHitResults(attackResNode.getTempData(), permData);
		attackResNode.getTempData().putCrits(attackResNode, permData);


		return attackResNode;
	}




	public float calculateValue() {

		return value;

	}


	@Override
	public List<Node> createChildren(StaticAttackData permData) {
		return DamageDecNode.createDamageDecNodes(this, permData);
	}



	@Override
	public float calculateValue(StaticAttackData permData) {
		return 0;
	}










}
