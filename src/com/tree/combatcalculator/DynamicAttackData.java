package com.tree.combatcalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tree.combatcalculator.AtkVar.Id;
import com.tree.combatcalculator.AttackCalculation.AttackCalculator;
import com.tree.combatcalculator.AttackCalculation.AttackCalculatorFactory;
import com.tree.combatcalculator.nodes.Node;

public class DynamicAttackData {

	private int focus;
	private List<WeaponCountHolder> weaponHolders;
	private Map<Id, AtkVar> variables;


	public DynamicAttackData(DynamicAttackData tempData) {

		this.focus = tempData.focus;
		this.weaponHolders = new ArrayList<WeaponCountHolder>(tempData.weaponHolders);
		this.variables = new HashMap<Id, AtkVar>(tempData.variables);
	}

	public DynamicAttackData(int numFocus,
			List<WeaponCountHolder> weaponHolders,
			Map<Id, AtkVar> variables) {

		this.focus = numFocus;
		this.weaponHolders = weaponHolders;
		this.variables = variables;

	}



	public void clearTempValues() {

		Iterator<Entry<Id, AtkVar>> entries = variables.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry entry = entries.next();
			AtkVar value = (AtkVar)entry.getValue();

			if (value.getState().equals(AtkVar.State.TEMP))
				entries.remove();
		}
	}

	public boolean contains(Id id) {
		return variables.containsKey(id);
	}

	public void putCrits(Node attackNode, StaticAttackData permData){

		Weapon weapon = permData.getAttacker().getWeapons().get(attackNode.getWeaponIndex());

		AttackCalculator atkCalculator = AttackCalculatorFactory.getInstance();
		float critChance = atkCalculator.calcCritWithoutCritProbability(attackNode, permData);

		for (AtkVar.Id id : weapon.getAtkVarIdsWithPermState()){

			if (variables.containsKey(id))
				addCritChance(id, critChance);
			else
				modifyCritChance(id, critChance);


		}


	}

	private void modifyCritChance(Id id, float critChance) {

		AtkVar newCrit = AtkVar.createAtkVar(id);

		newCrit.setValue(critChance);

		variables.put(id, newCrit);

	}

	private void addCritChance(Id id, float critChance) {

		AtkVar oldCrit = variables.get(id);

		float newCritChance = oldCrit.getValue() + (1-oldCrit.getValue())*critChance;

		oldCrit.setValue(newCritChance);

	}

	public int getFocus() {
		return focus;
	}

	public void setFocus(int focus) {
		this.focus = focus;
	}

	public List<WeaponCountHolder> getWeaponHolders() {
		return weaponHolders;
	}

	public void setWeaponHolders(List<WeaponCountHolder> weaponHolders) {
		this.weaponHolders = weaponHolders;
	}

	public Map<Id, AtkVar> getVariables() {
		return variables;
	}

	public void setVariables(Map<Id, AtkVar> variables) {
		this.variables = variables;
	}




}
