package com.tree.combatcalculator.AttackCalculation;

import java.util.List;

import com.tree.combatcalculator.AtkVar;
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
	private List<AtkVar> currentAtkVars;

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



	private void checkValidAttack(List<AtkVar> currentAtkVars2, Weapon weapon) {
		// TODO Auto-generated method stub

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
