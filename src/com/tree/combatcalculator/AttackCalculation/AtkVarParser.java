package com.tree.combatcalculator.AttackCalculation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.AtkVar.Id;

public class AtkVarParser {

	private static AtkVarParser atkVarParser;

	public static AtkVarParser getInstance() {

		if (atkVarParser == null)
			setInstance();
		return atkVarParser;
	}

	public static void setInstance(){
		atkVarParser = new AtkVarParser();
	}


	public List<AtkVar> parseAtkVars(List<List<AtkVar>> weaponVariables,
			List<AtkVar> permVariables, Map<Id, AtkVar> dynamicVariables, int weaponIndex) {


		List<AtkVar> atkVars = new ArrayList<AtkVar>();

		atkVars.addAll(weaponVariables.get(weaponIndex));
		atkVars.addAll(permVariables);
		atkVars.addAll(dynamicVariables.values());

		return atkVars;
	}

	public void checkConstraints(List<AtkVar> currentAtkVars) {
		// TODO Auto-generated method stub

	}

}
