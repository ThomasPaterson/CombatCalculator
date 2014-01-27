package com.tree.combatcalculator.nodes;

import java.util.ArrayList;
import java.util.List;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.AtkVarCopy;
import com.tree.combatcalculator.AttackCalcHelper;
import com.tree.combatcalculator.PermanentTreeData;
import com.tree.combatcalculator.Weapon;
import com.tree.combatcalculator.WeaponCountHolder;

public class DamageResNode extends ResultNode {
	

	public DamageResNode(Node parent) {
		
		super(parent);
		nodeType = Node.Type.ATTACK_DEC;

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
		// TODO Auto-generated method stub
		return FinalResNode.createFinalResNodes(this, permData);
	}


	@Override
	public float calculateValue(PermanentTreeData permData) {
		// TODO Auto-generated method stub
		return 0;
	}



	




}
