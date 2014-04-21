package com.tree.combatcalculator.AttackCalculation;

import java.util.Map;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.AtkVar.Id;
import com.tree.combatcalculator.AttackModel;
import com.tree.combatcalculator.DefendModel;
import com.tree.combatcalculator.DynamicAttackData;
import com.tree.combatcalculator.StaticAttackData;
import com.tree.combatcalculator.Weapon;

public class Attack {

	private int baseAtk;
	private int basePow;
	private int baseDef;
	private int baseArm;
	private int baseHealth;

	private int modAtk;
	private int modPow;
	private int modDef;
	private int modArm;

	private boolean boostedAtk;
	private boolean boostedDam;
	private int addAtk;
	private int addDam;

	private int disAtk;
	private int disDam;
	private boolean rerollAtk;
	private boolean rerollDam;
	private boolean autoHit;
	private boolean autoCrit;
	private boolean critAtk;
	private boolean doubleDamage;
	private Map<AtkVar.Id, AtkVar> currentAtkVars;

	private boolean isMelee;



	public Attack(StaticAttackData permData, DynamicAttackData tempData, int weaponIndex){

		AttackModel attacker = permData.getAttacker();
		DefendModel defender = permData.getDefender();
		Weapon weapon = attacker.getWeapons().get(weaponIndex);

		setBaseVariables(attacker, defender, weapon);

		currentAtkVars = AtkVarParser.getInstance().parseAtkVars(
				permData.getWeaponVariables(),
				permData.getVariables(),
				tempData.getVariables(),
				weaponIndex);

		checkValidAttack(currentAtkVars, weapon);

		AtkVarParser.getInstance().checkConstraints(currentAtkVars);
	}



	private boolean checkValidAttack(Map<Id, AtkVar> atkVars, Weapon weapon) {



		if (weapon.getRanged())
			return checkRanged(atkVars, weapon);
		else
			return checkMelee(atkVars, weapon);


	}



	private boolean checkRanged(Map<Id, AtkVar> atkVars, Weapon weapon){

		//if at ranged
		if (atkVars.containsKey(AtkVar.Id.RANGED)){
			return true;
			//if in melee and weapon has gunfighter
		}else if (atkVars.containsKey(AtkVar.Id.GUNFIGHTER)){
			return true;
			//if charging and weapon has assault
		}else if (atkVars.containsKey(AtkVar.Id.ASSAULT) && atkVars.containsKey(AtkVar.Id.CHARGE)){
			return true;
		}

		return false;

	}



	private boolean checkMelee(Map<Id, AtkVar> atkVars, Weapon weapon){

		if (!atkVars.containsKey(AtkVar.Id.RANGED))
			return true;


		return false;
	}



	private void setBaseVariables(AttackModel attacker, DefendModel defender, Weapon weapon){

		baseAtk = attacker.getMat();
		basePow = weapon.getPow();
		baseDef = defender.getDef();
		baseArm = defender.getArm();

		modAtk = baseAtk;
		modPow = basePow;
		modDef = baseDef;
		modArm = baseArm;

	}






}
