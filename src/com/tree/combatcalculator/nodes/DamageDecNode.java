package com.tree.combatcalculator.nodes;

import java.util.ArrayList;
import java.util.List;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.AtkVarCopy;
import com.tree.combatcalculator.PermanentTreeData;
import com.tree.combatcalculator.WeaponCountHolder;

public class DamageDecNode extends DecisionNode {
	
	public static boolean BOUGHT_BOOSTED_DAMAGE = true;
	public static boolean BOUGHT_UNBOOSTED_DAMAGE = false;

	public DamageDecNode(Node parent) {
		
		super(parent);
		nodeType = Node.Type.ATTACK_DEC;

	}

	
	public static List<Node> createDamageDecNodes(Node parent, PermanentTreeData permData) {
		
		List<Node> damageNodes = new ArrayList<Node>();
		
		if (canBoost(parent) && notBoosted(parent, permData)){
			damageNodes.add(makeDamage(parent, BOUGHT_BOOSTED_DAMAGE));
		}
		
		damageNodes.add(makeDamage(parent, BOUGHT_UNBOOSTED_DAMAGE));
		
		
		return damageNodes;	
	}

	private static Node makeDamage(Node parent, boolean boughtBoost) {
		
		DecisionNode attackDecNode = new DamageDecNode(parent);
		
		if (boughtBoost){
			attackDecNode.getTempData().focus--;
			attackDecNode.getTempData().variables.put(
					AtkVarCopy.Id.BOOSTED_DAMAGE, new AtkVarCopy(AtkVarCopy.Id.BOOSTED_DAMAGE));
		}
		
		attackDecNode.setBoughtBoost(boughtBoost);
		
		return attackDecNode;
	}


	private static boolean notBoosted(Node parent, PermanentTreeData permData) {
		
		if (AtkVarCopy.checkGroup(AtkVarCopy.Group.ATTACKER, AtkVarCopy.Id.BOOSTED_DAMAGE))
			return true;
		else if (AtkVarCopy.checkWeaponGroup(AtkVarCopy.Group.WEAPON, parent.getWeaponIndex(), AtkVarCopy.Id.BOOSTED_DAMAGE))
			return true;
		else if (AtkVarCopy.checkWeaponGroup(AtkVarCopy.Group.SITUATION, parent.getWeaponIndex(), AtkVarCopy.Id.BOOSTED_DAMAGE))
			return true;

		return false;
	}


	private static boolean canBoost(Node parent) {
		
		if (parent.tempData.focus > 0)
			return true;
		
		return false;
	}
	

	@Override
	public void calculateValue() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public List<Node> createChildren(PermanentTreeData permData) {
		// TODO Auto-generated method stub
		return DamageResNode.createDamageResNodes(this, permData);
	}

	




}
