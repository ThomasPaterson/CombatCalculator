package com.tree.combatcalculator.nodes;

import java.util.ArrayList;
import java.util.List;

import com.tree.combatcalculator.StaticAttackData;
import com.tree.combatcalculator.AttackCalculation.AttackCalculator;
import com.tree.combatcalculator.AttackCalculation.AttackCalculatorFactory;
import com.tree.combatcalculator.AttackCalculation.AttackResult;
import com.tree.combatcalculator.AttackCalculation.InvalidAttackException;
import com.tree.combatcalculator.Dice.HitRoll;

public class AttackResNode extends ResultNode {

	HitRoll hitRoll;

	public AttackResNode(Node parent) {

		super(parent);
		nodeType = Node.Type.ATTACK_RES;

	}


	public static List<Node> createAttackResNodes(Node parent, StaticAttackData permData) {

		List<Node> attackResNodes = new ArrayList<Node>();

		attackResNodes.add(makeAttackResult(parent, permData));


		return attackResNodes;
	}


	//save the hit chances of the attack
	private static Node makeAttackResult(Node parent, StaticAttackData permData){

		AttackResNode attackResNode = new AttackResNode(parent);

		AttackCalculator atkCalculator = AttackCalculatorFactory.getInstance();

		try {
			AttackResult attackResult = atkCalculator.calculateAttackResult(permData,
					attackResNode.getTempData(), attackResNode.getWeaponIndex(), false);

			HitRoll hitRoll = new HitRoll(attackResult.getHitChance(), attackResult.getCritChance());
			attackResNode.setHitRoll(hitRoll);
		} catch (InvalidAttackException e) {
			attackResNode.setHitRoll(new HitRoll(0,0));
		}

		attackResNode.getTempData().updateContinuous(attackResNode, permData);


		return attackResNode;
	}




	public float calculateValue() {

		return value;

	}


	@Override
	public List<Node> createChildren(StaticAttackData permData) {
		return DamageDecNode.createDamageDecNodes(this, permData);
	}



	@Override
	public float calculateValue(StaticAttackData permData) {
		return 0;
	}


	public HitRoll getHitRoll() {
		return hitRoll;
	}


	public void setHitRoll(HitRoll hitRoll) {
		this.hitRoll = hitRoll;
	}










}
