package com.tree.combatcalculator.nodes;

import java.util.ArrayList;
import java.util.List;

import com.tree.combatcalculator.AttackCalcHelper;
import com.tree.combatcalculator.PermanentTreeData;

public class FinalResNode extends ResultNode {


	public FinalResNode(Node parent) {

		super(parent);
		nodeType = Node.Type.FINAL_RES;

	}



	public static List<Node> createFinalResNodes(Node parent, PermanentTreeData permData) {

		List<Node> finalResNodes = new ArrayList<Node>();

		finalResNodes.add(new FinalResNode(parent));

		return finalResNodes;
	}





	@Override
	public float calculateValue(PermanentTreeData permData) {


		value = AttackCalcHelper.calculateValue(permData, this);

		return value;


	}




	@Override
	public List<Node> createChildren(PermanentTreeData permData) {
		return BuyNode.createBuyNodes(this, permData);
	}










}
