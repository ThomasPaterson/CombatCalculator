package com.tree.combatcalculator.AttackCalculation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.AtkVar.Conditional;
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

	public void checkConstraints(Map<Id, AtkVar> currentAtkVars, boolean useCrit) {

		if (!useCrit)
			currentAtkVars.remove(AtkVar.Id.CRIT);

		for (AtkVar atkVar : currentAtkVars.values()){
			if (atkVar.getConditions().size() > 0)
				checkConstraint(atkVar, currentAtkVars);


		}

	}

	//checks to see if all the variables conditions are in the variables in play
	//removes it if fails it's constraints, should be fine for now, because no
	//constraint variable is a constraint to another variable
	private void checkConstraint(AtkVar atkVar, Map<Id, AtkVar> currentAtkVars) {

		for (Conditional c : atkVar.getConditions()){

			if (!currentAtkVars.containsKey(AtkVar.Id.valueOf(c.toString()))){
				currentAtkVars.remove(atkVar);
				break;
			}

		}

	}

	public void processVariables(Attack attack, Map<Id, AtkVar> currentAtkVars) {

		for (AtkVar atkVar : currentAtkVars.values())
			processVariable(attack, atkVar, currentAtkVars);

	}

	//using either the Id of the atkVar, or triggered id if it is a conditional
	private void processVariable(Attack attack, AtkVar atkVar,
			Map<Id, AtkVar> currentAtkVars) {



		if (atkVar.getTriggered() != null)
			atkVar = currentAtkVars.get(AtkVar.Id.valueOf(atkVar.getTriggered().toString()));

		float atkVarValue = atkVar.getValue();


		switch (atkVar.getId()) {

		case ADD_DAM: attack.setAddDam(attack.getAddDam() + (int) atkVar.getValue());
		break;
		case ADD_HIT: attack.setAddAtk(attack.getAddAtk() + (int) atkVar.getValue());
		break;
		case ARM_PIERCE: attack.setModArm(attack.getModArm() - (int)Math.floor(attack.getBaseArm()/2));
		break;
		case AUTO_HIT: attack.setAutoHit(atkVarValue);
		break;
		case BOOSTED_DAM: attack.setBoostedDam(true);
		break;
		case BOOSTED_HIT: attack.setBoostedAtk(true);
		break;
		case CHARGE: attack.setBoostedDam(true);
		break;
		case CHARGING: attack.setBoostedDam(true);
		break;
		case SHRED: attack.setShred(true);
		break;
		case DISCARD_ATK: attack.setDisAtk(attack.getDisAtk() + (int) atkVar.getValue());
		break;
		case DISCARD_DAM: attack.setDisDam(attack.getDisDam() + (int) atkVar.getValue());
		break;
		case DOUBLE_DAMAGE: attack.setDoubleDamage(true);
		break;
		case KNOCKED_DOWN: attack.setKnockedDown(atkVarValue);
		break;
		case MOD_ARM: attack.setModArm(attack.getModArm() + (int) atkVar.getValue());
		break;
		case MOD_DAM: attack.setModPow(attack.getModPow() + (int) atkVar.getValue());
		break;
		case MOD_DEF: attack.setModDef(attack.getModDef() + (int) atkVar.getValue());
		break;
		case MOD_HIT: attack.setModAtk(attack.getModAtk() + (int) atkVar.getValue());
		break;
		case POWERFUL_ATK: attack.setBoostedDam(true); attack.setBoostedAtk(true);
		break;
		case REROLL_ATK: attack.setRerollAtk(true);
		break;
		case REROLL_DAM: attack.setRerollDam(true);
		break;
		case SUSTAINED: attack.setAutoHit(atkVarValue);
		break;
		case CRIT: attack.setCrit(true);
		break;
		default:
			break;

		}

	}

}
