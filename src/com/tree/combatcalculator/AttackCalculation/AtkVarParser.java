package com.tree.combatcalculator.AttackCalculation;

import java.util.HashMap;
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


	public Map<AtkVar.Id, AtkVar> parseAtkVars(List<Map<AtkVar.Id, AtkVar>> weaponMaps,
			Map<AtkVar.Id, AtkVar> staticMap, Map<Id, AtkVar> dynamicMap, int weaponIndex) {


		Map<AtkVar.Id, AtkVar> atkVars = new HashMap<AtkVar.Id, AtkVar>();

		atkVars.putAll(weaponMaps.get(weaponIndex));
		atkVars.putAll(staticMap);
		atkVars.putAll(dynamicMap);

		return atkVars;
	}

	public void checkConstraints(Map<Id, AtkVar> currentAtkVars) {
		// TODO Auto-generated method stub

	}

}
