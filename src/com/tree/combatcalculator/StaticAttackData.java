package com.tree.combatcalculator;

import java.util.List;
import java.util.Map;

import com.tree.combatcalculator.AtkVar.Id;

public class StaticAttackData {

	private Map<AtkVar.Id, AtkVar> variables;

	private DefendModel defender;
	private AttackModel attacker;
	private AttackCalculator atkCalc;
	private List<Map<AtkVar.Id, AtkVar>> weaponVariables;



	public boolean checkContains(Id id, int weaponIndex) {


		if (variables.get(id) != null)
			return true;

		if (weaponVariables.get(weaponIndex).get(id) != null)
			return true;

		return false;
	}



	//return first star attack variable found for that weapon
	public AtkVar findStarAttack(int weaponIndex) {


		for (AtkVar w : weaponVariables.get(weaponIndex).values()){
			if (w.getWeaponIndex() == weaponIndex){
				if (w.getModifiers().contains(AtkVar.Modifier.STAR_ATTACK))
					return w;
			}
		}

		return null;

	}

	public Map<AtkVar.Id, AtkVar> getVariables() {
		return variables;
	}

	public void setVariables(Map<AtkVar.Id, AtkVar> variables) {
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

	public List<Map<AtkVar.Id, AtkVar>> getWeaponVariables() {
		return weaponVariables;
	}

	public void setWeaponVariables(List<Map<AtkVar.Id, AtkVar>> weaponVariables) {
		this.weaponVariables = weaponVariables;
	}






}
