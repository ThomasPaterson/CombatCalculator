package com.tree.combatcalculator.nodes;

import java.util.ArrayList;
import java.util.List;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.PermanentTreeData;

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
		return parent.getTempData().variables.containsKey(AtkVar.Id.CHARGE);
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
					AtkVar.Id.BOOSTED_DAM,
					AtkVar.createAtkVar(AtkVar.Id.BOOSTED_DAM));
		}



		attackDecNode.setBoughtBoost(boughtBoost);

		return attackDecNode;
	}


	private static boolean freeCharge(Node parent,
			PermanentTreeData permData) {

		if (permData == null)
			return false;
		else
			return (isCharging(parent) && AtkVar.contains(permData.variables,
					AtkVar.Group.ATTACKER, AtkVar.Id.FREE_CHARGE));

	}


	private static boolean notBoosted(Node parent, PermanentTreeData permData) {

		boolean permDataContains = permData.checkGroupsContains(AtkVar.Id.BOOSTED_DAM,
				parent.getWeaponIndex(),
				AtkVar.Group.ATTACKER,
				AtkVar.Group.WEAPON,
				AtkVar.Group.SITUATION
				);

		boolean tempDataContains = parent.getTempData().contains(AtkVar.Id.BOOSTED_DAM);

		return permDataContains || tempDataContains;
	}


	private static boolean canBoost(Node parent) {

		if (parent.tempData.focus > 0)
			return true;

		return false;
	}





	@Override
	public List<Node> createChildren(PermanentTreeData permData) {
		return DamageResNode.createDamageResNodes(this, permData);
	}


	@Override
	public float calculateValue(PermanentTreeData permData) {
		return 0;
	}






}
