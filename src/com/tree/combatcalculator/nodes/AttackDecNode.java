package com.tree.combatcalculator.nodes;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.AtkVarCopy;
import com.tree.combatcalculator.PermanentTreeData;
import com.tree.combatcalculator.WeaponCountHolder;

public class AttackDecNode extends DecisionNode {
	
	public static boolean BOUGHT_BOOSTED_ATTACK = true;
	public static boolean BOUGHT_UNBOOSTED_ATTACK = false;

	public AttackDecNode(Node parent) {
		
		super(parent);
		nodeType = Node.Type.ATTACK_DEC;

	}

	
	public AttackDecNode(Parcel in) {
		super(in);
		nodeType = Node.Type.ATTACK_DEC;
	}


	public static List<Node> createAttackDecNodes(Node parent, PermanentTreeData permData) {
		
		List<Node> attackNodes = new ArrayList<Node>();
		
		if (canBoost(parent) && notBoosted(parent, permData)){
			attackNodes.add(makeAttack(parent, BOUGHT_BOOSTED_ATTACK));
		}
		
		attackNodes.add(makeAttack(parent, BOUGHT_BOOSTED_ATTACK));
		
		
		return attackNodes;	
	}

	private static Node makeAttack(Node parent, boolean boughtBoost) {
		
		DecisionNode attackDecNode = new AttackDecNode(parent);
		
		if (boughtBoost){
			attackDecNode.getTempData().focus--;
			attackDecNode.getTempData().variables.put(
					AtkVarCopy.Id.BOOSTED_HIT, AtkVarCopy.createAtkVar(AtkVarCopy.Id.BOOSTED_HIT));
		}
		
		attackDecNode.setBoughtBoost(boughtBoost);
		
		return attackDecNode;
	}


	private static boolean notBoosted(Node parent, PermanentTreeData permData) {

		boolean permDataContains = permData.checkGroupsContains(AtkVarCopy.Id.BOOSTED_HIT, 
				parent.getWeaponIndex(), 
				AtkVarCopy.Group.ATTACKER, 
				AtkVarCopy.Group.WEAPON
				);
		
		boolean tempDataContains = parent.getTempData().contains(AtkVarCopy.Id.BOOSTED_HIT);
		
		return permDataContains || tempDataContains;
	}


	private static boolean canBoost(Node parent) {
		
		if (parent.tempData.focus > 0)
			return true;
		
		return false;
	}
	



	@Override
	public List<Node> createChildren(PermanentTreeData permData) {
		// TODO Auto-generated method stub
		return AttackResNode.createAttackResNodes(this, permData);
	}




	@Override
	public float calculateValue(PermanentTreeData permData) {
		// TODO Auto-generated method stub
		return 0;
	}

	




}
