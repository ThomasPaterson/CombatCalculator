package com.tree.combatcalculator.nodes;

import java.util.ArrayList;
import java.util.List;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.StaticAttackData;

public class DamageDecNode extends DecisionNode {

	public static boolean BOUGHT_BOOSTED_DAMAGE = true;
	public static boolean BOUGHT_UNBOOSTED_DAMAGE = false;

	public DamageDecNode(Node parent) {

		super(parent);
		nodeType = Node.Type.ATTACK_DEC;

	}



	public static List<Node> createDamageDecNodes(Node parent, StaticAttackData permData) {

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
		return parent.getTempData().getVariables().containsKey(AtkVar.Id.CHARGE);
	}


	private static Node makeDamage(Node parent, boolean boughtBoost) {
		return makeDamage(parent, boughtBoost, null);
	}

	private static Node makeDamage(Node parent, boolean boughtBoost, StaticAttackData permData) {

		DecisionNode attackDecNode = new DamageDecNode(parent);

		if (boughtBoost){

			if (!freeCharge(parent, permData))
				attackDecNode.getTempData().decrementFocus();

			attackDecNode.getTempData().getVariables().put(
					AtkVar.Id.BOOSTED_DAM,
					AtkVar.createAtkVar(AtkVar.Id.BOOSTED_DAM));
		}



		attackDecNode.setBoughtBoost(boughtBoost);

		return attackDecNode;
	}


	private static boolean freeCharge(Node parent,
			StaticAttackData permData) {

		if (permData == null)
			return false;
		else
			return (isCharging(parent) && permData.checkContains(AtkVar.Id.FREE_CHARGE));

	}


	private static boolean notBoosted(Node parent, StaticAttackData permData) {

		boolean permDataContains = permData.checkContains(AtkVar.Id.BOOSTED_DAM,
				parent.getWeaponIndex());

		boolean tempDataContains = parent.getTempData().contains(AtkVar.Id.BOOSTED_DAM);

		return permDataContains || tempDataContains;
	}


	private static boolean canBoost(Node parent) {

		if (parent.tempData.getFocus() > 0)
			return true;

		return false;
	}





	@Override
	public List<Node> createChildren(StaticAttackData permData) {
		return DamageResNode.createDamageResNodes(this, permData);
	}


	@Override
	public float calculateValue(StaticAttackData permData) {
		return 0;
	}






}
