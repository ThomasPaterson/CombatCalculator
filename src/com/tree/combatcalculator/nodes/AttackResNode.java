package com.tree.combatcalculator.nodes;

import java.util.ArrayList;
import java.util.List;

import com.tree.combatcalculator.PermanentTreeData;

public class AttackResNode extends ResultNode {

	public AttackResNode(Node parent) {

		super(parent);
		nodeType = Node.Type.ATTACK_RES;

	}


	public static List<Node> createAttackResNodes(Node parent, PermanentTreeData permData) {

		List<Node> attackResNodes = new ArrayList<Node>();

		attackResNodes.add(makeAttackResult(parent, permData));


		return attackResNodes;
	}


	private static Node makeAttackResult(Node parent, PermanentTreeData permData) {

		Node attackResNode = new AttackResNode(parent);


		attackResNode.getTempData().putCrits(attackResNode, permData);


		return attackResNode;
	}




	public float calculateValue() {

		return value;

	}


	@Override
	public List<Node> createChildren(PermanentTreeData permData) {
		return DamageDecNode.createDamageDecNodes(this, permData);
	}



	@Override
	public float calculateValue(PermanentTreeData permData) {
		return 0;
	}










}
