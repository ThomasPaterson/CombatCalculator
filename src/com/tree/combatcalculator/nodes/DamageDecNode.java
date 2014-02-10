package com.tree.combatcalculator.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.AtkVarCopy;
import com.tree.combatcalculator.PermanentTreeData;
import com.tree.combatcalculator.WeaponCountHolder;
import com.tree.combatcalculator.AtkVarCopy.Group;
import com.tree.combatcalculator.AtkVarCopy.Id;

public class DamageDecNode extends DecisionNode {
	
	public static boolean BOUGHT_BOOSTED_DAMAGE = true;
	public static boolean BOUGHT_UNBOOSTED_DAMAGE = false;

	public DamageDecNode(Node parent) {
		
		super(parent);
		nodeType = Node.Type.ATTACK_DEC;

	}

	
	public static List<Node> createDamageDecNodes(Node parent, PermanentTreeData permData) {
		
		List<Node> damageNodes = new ArrayList<Node>();
		
		if (isCharging(parent)){
			damageNodes.add(makeDamage(parent, BOUGHT_BOOSTED_DAMAGE));
		}else{
			if (canBoost(parent) && notBoosted(parent, permData)){
				damageNodes.add(makeDamage(parent, BOUGHT_BOOSTED_DAMAGE));
			}
			damageNodes.add(makeDamage(parent, BOUGHT_UNBOOSTED_DAMAGE));	
		}

		return damageNodes;	
	}

	private static boolean isCharging(Node parent) {
		return parent.getTempData().variables.containsKey(AtkVarCopy.Id.CHARGE);
	}


	private static Node makeDamage(Node parent, boolean boughtBoost) {
		return makeDamage(parent, boughtBoost, null);
	}
	
	private static Node makeDamage(Node parent, boolean boughtBoost, PermanentTreeData permData) {
		
		DecisionNode attackDecNode = new DamageDecNode(parent);
		
		if (boughtBoost){
			
			if (!freeCharge(parent, permData))
				attackDecNode.getTempData().focus--;
			
			attackDecNode.getTempData().variables.put(
					AtkVarCopy.Id.BOOSTED_DAMAGE, 
					AtkVarCopy.createAtkVar(AtkVarCopy.Id.BOOSTED_DAMAGE));
		}
		
		
		
		attackDecNode.setBoughtBoost(boughtBoost);
		
		return attackDecNode;
	}


	private static boolean freeCharge(Node parent,
			PermanentTreeData permData) {
		
		if (permData == null)
			return false;
		else
			return (isCharging(parent) && AtkVarCopy.contains(permData.variables,
					AtkVarCopy.Group.ATTACKER, AtkVarCopy.Id.FREE_CHARGE));
		
	}


	private static boolean notBoosted(Node parent, PermanentTreeData permData) {
		
		boolean permDataContains = permData.checkGroupsContains(AtkVarCopy.Id.BOOSTED_DAMAGE, 
				parent.getWeaponIndex(), 
				AtkVarCopy.Group.ATTACKER, 
				AtkVarCopy.Group.WEAPON,
				AtkVarCopy.Group.SITUATION
				);
		
		boolean tempDataContains = parent.getTempData().contains(AtkVarCopy.Id.BOOSTED_DAMAGE);

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
		return DamageResNode.createDamageResNodes(this, permData);
	}


	@Override
	public float calculateValue(PermanentTreeData permData) {
		// TODO Auto-generated method stub
		return 0;
	}

	




}
