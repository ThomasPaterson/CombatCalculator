package com.tree.combatcalculator.AttackCalculation;

import com.tree.combatcalculator.DynamicAttackData;
import com.tree.combatcalculator.StaticAttackData;
import com.tree.combatcalculator.Dice.DiceHolder;
import com.tree.combatcalculator.Dice.HitRoll;




/**
 * @(#)AttackCalculator.java
 *
 *
 *returns either expected damage on hit, expected damage on crit, or the various chances to hit in an array
 *
 * @author
 * @version 1.00 2012/10/1
 */


public class AttackCalculator {

	DiceHolder holder;
	private static final int BASE_DICE = 2;

	public AttackCalculator(DiceHolder holder) {

		this.holder = holder;
	}




	public AttackResult calculateAttackResult(StaticAttackData permData,
			DynamicAttackData tempData, int weaponIndex, boolean crit) throws InvalidAttackException{

		Attack attack = new Attack(permData, tempData, weaponIndex, crit);

		HitRoll hitRoll = calculateAttackRoll(attack);
		float damage = calculateDamageRoll(attack);
		AttackResult result= processExceptions(attack, hitRoll, damage, crit);


		return result;

	}


	private AttackResult processExceptions(Attack attack, HitRoll hitRoll, float damage, boolean crit) {

		AttackResult result = new AttackResult();

		if (attack.isDoubleDamage())
			damage *= 2;


		//problem, we aren't tracking source of knockdown or sustained, so we don't know if they
		//should be independent of each other or not.  However they should also never occur together,
		//so assuming one way or another is a good enough solution
		float autoHit =  attack.getKnockedDown() + attack.getSustained() + attack.getAutoHit();

		//if there is a chance of knockdown, autohit, or sustained, change hitRoll
		if (autoHit >= 1){
			hitRoll = new HitRoll(1, 0);
		}else if (autoHit < 1){
			hitRoll.hitChance = hitRoll.hitChance * (1-autoHit) + 1 * autoHit;
			hitRoll.critChance *= (1-autoHit);
		}

		result.setHitChance(hitRoll.hitChance);
		result.setCritChance(hitRoll.critChance);

		if (crit)
			result.setCritDamage(damage);
		else
			result.setHitDamage(damage);

		return result;
	}


	private float calculateDamageRoll(Attack attack) {

		//2 + additional dice + 1 if boosted
		int damageDice = BASE_DICE + attack.getAddDam() + (attack.isBoostedDam() ? 1 : 0);
		int disDamageDice = attack.getDisDam();
		int power = attack.getModPow();
		int armor = attack.getModArm();
		boolean reroll = attack.isRerollDam();


		return holder.rollToDamage(power, armor, damageDice - disDamageDice, disDamageDice, reroll);
	}


	private HitRoll calculateAttackRoll(Attack attack) {

		int attackDice = BASE_DICE + attack.getAddAtk() + (attack.isBoostedAtk() ? 1 : 0);
		int disAttackDice = attack.getDisAtk();
		int attackVal = attack.getModAtk();
		int defenseVal = attack.getModDef();
		boolean reroll = attack.isRerollAtk();

		return holder.rollToHit(attackVal, defenseVal, attackDice - disAttackDice, disAttackDice, reroll);
	}




}


