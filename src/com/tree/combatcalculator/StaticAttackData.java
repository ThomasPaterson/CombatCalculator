package com.tree.combatcalculator;

import java.util.List;

import com.tree.combatcalculator.AtkVar.Id;

public class StaticAttackData {

	private List<AtkVar> variables;

	private DefendModel defender;
	private AttackModel attacker;
	private AttackCalculator atkCalc;
	private List<List<AtkVar>> weaponVariables;


	public boolean checkContains(Id id, int weaponIndex) {


		for (AtkVar atkVar : variables)
			if (atkVar.getId().equals(id))
				return true;

		for (AtkVar weaponVar : weaponVariables.get(weaponIndex))
			if (weaponVar.getId().equals(id))
				return true;

		return false;
	}

	//return first star attack variable found for that weapon
	public AtkVar findStarAttack(int weaponIndex) {


		for (AtkVar w : weaponVariables.get(weaponIndex)){
			if (w.getWeaponIndex() == weaponIndex){
				if (w.getModifiers().contains(AtkVar.Modifier.STAR_ATTACK))
					return w;
			}
		}

		return null;

	}

	public List<AtkVar> getVariables() {
		return variables;
	}

	public void setVariables(List<AtkVar> variables) {
		this.variables = variables;
	}

	public DefendModel getDefender() {
		return defender;
	}

	public void setDefender(DefendModel defender) {
		this.defender = defender;
	}

	public AttackModel getAttacker() {
		return attacker;
	}

	public void setAttacker(AttackModel attacker) {
		this.attacker = attacker;
	}

	public AttackCalculator getAtkCalc() {
		return atkCalc;
	}

	public void setAtkCalc(AttackCalculator atkCalc) {
		this.atkCalc = atkCalc;
	}

	public List<List<AtkVar>> getWeaponVariables() {
		return weaponVariables;
	}

	public void setWeaponVariables(List<List<AtkVar>> weaponVariables) {
		this.weaponVariables = weaponVariables;
	}






}
