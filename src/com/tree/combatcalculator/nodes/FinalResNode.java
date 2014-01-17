package com.tree.combatcalculator.nodes;

import java.util.ArrayList;
import java.util.List;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.AtkVarCopy;
import com.tree.combatcalculator.AttackCalcHelper;
import com.tree.combatcalculator.PermanentTreeData;
import com.tree.combatcalculator.Weapon;
import com.tree.combatcalculator.WeaponCountHolder;

public class FinalResNode extends ResultNode {
	

	public FinalResNode(Node parent) {
		
		super(parent);
		nodeType = Node.Type.ATTACK_DEC;

	}

	
	public static List<Node> createFinalResNodes(Node parent, PermanentTreeData permData) {
		
		List<Node> finalResNodes = new ArrayList<Node>();

		finalResNodes.add(calcFullAttack(parent, permData));

		return finalResNodes;	
	}


	private static Node calcFullAttack(Node parent, PermanentTreeData permData) {

		Node finalResNode = new FinalResNode(parent);
		
		finalResNode.setValue(AttackCalcHelper.calcFullAttack(finalResNode, permData));

		return finalResNode;
	}




	@Override
	public void calculateValue() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public List<Node> createChildren(PermanentTreeData permData) {
		// TODO Auto-generated method stub
		return null;
	}

	




}
