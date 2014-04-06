package com.tree.combatcalculator.nodes;

import java.util.ArrayList;
import java.util.List;

import com.tree.combatcalculator.PermanentTreeData;

public class DamageResNode extends ResultNode {


	public DamageResNode(Node parent) {

		super(parent);
		nodeType = Node.Type.DAMAGE_RES;

	}



	public static List<Node> createDamageResNodes(Node parent, PermanentTreeData permData) {

		List<Node> damageResNodes = new ArrayList<Node>();

		damageResNodes.add(makeDamageResult(parent, permData));

		return damageResNodes;
	}


	private static Node makeDamageResult(Node parent, PermanentTreeData permData) {

		Node damageResNode = new DamageResNode(parent);


		return damageResNode;
	}






	@Override
	public List<Node> createChildren(PermanentTreeData permData) {
		return FinalResNode.createFinalResNodes(this, permData);
	}


	@Override
	public float calculateValue(PermanentTreeData permData) {
		return 0;
	}








}
