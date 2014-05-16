package com.tree.combatcalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tree.combatcalculator.AtkVar.Id;
import com.tree.combatcalculator.Dice.HitRoll;
import com.tree.combatcalculator.nodes.AttackResNode;

public class DynamicAttackData {

	private int focus;
	private List<WeaponCountHolder> weaponHolders;
	private Map<Id, AtkVar> variables;
	private Map<Integer, AtkVar> sustainedVariables;


	public DynamicAttackData(DynamicAttackData tempData) {

		this.focus = tempData.focus;
		this.weaponHolders = new ArrayList<WeaponCountHolder>(tempData.weaponHolders);
		this.variables = new HashMap<Id, AtkVar>(tempData.variables);
		this.sustainedVariables = new HashMap<Integer, AtkVar>(tempData.sustainedVariables);
	}

	public DynamicAttackData(int numFocus,
			List<WeaponCountHolder> weaponHolders,
			Map<Id, AtkVar> variables) {

		this.focus = numFocus;
		this.weaponHolders = weaponHolders;
		this.variables = variables;
		this.sustainedVariables = new HashMap<Integer, AtkVar>();


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

	public void decrementFocus() {
		focus--;

	}

	//updates knocked down and sustained attack possibilities
	public void updateContinuous(AttackResNode attackResNode,
			StaticAttackData permData) {

		Map<AtkVar.Id, AtkVar> weaponVariables = permData.getWeaponVariables().get(attackResNode.getWeaponIndex());

		AtkVar knockedDown = attackResNode.getTempData().getVariables().get(AtkVar.Id.KNOCKED_DOWN);
		AtkVar sustainedAtk = attackResNode.getTempData().getSustainedVariables().get(attackResNode.getWeaponIndex());

		HitRoll hitRoll = attackResNode.getHitRoll();

		//if knockdown is possible, create a knocked_down value if needed, and update it
		if (weaponVariables.containsKey(AtkVar.Id.CRIT_KNOCKDOWN) ||
				weaponVariables.containsKey(AtkVar.Id.KNOCKDOWN)){

			if (knockedDown == null){
				knockedDown = AtkVar.createAtkVar(AtkVar.Id.KNOCKED_DOWN);
				attackResNode.getTempData().getVariables().put(AtkVar.Id.KNOCKED_DOWN, knockedDown);
			}

			updateVariable(attackResNode, weaponVariables, hitRoll,
					Id.CRIT_KNOCKDOWN, Id.KNOCKDOWN, knockedDown);

		}

		//if sustained attack is possible, create a sustained value if needed, and update it
		if (weaponVariables.containsKey(AtkVar.Id.CRIT_SUSTAINED_ATK) ||
				weaponVariables.containsKey(AtkVar.Id.SUSTAINED_ATK)){

			if (sustainedAtk == null){
				sustainedAtk = AtkVar.createAtkVar(AtkVar.Id.SUSTAINED);
				attackResNode.getTempData().getSustainedVariables().put(attackResNode.getWeaponIndex(), sustainedAtk);
			}

			updateVariable(attackResNode, weaponVariables, hitRoll,
					Id.CRIT_SUSTAINED_ATK, Id.SUSTAINED_ATK, sustainedAtk);

		}



	}

	//updates the variable
	private void updateVariable(AttackResNode attackResNode, Map<Id, AtkVar> weaponVariables,
			HitRoll hitRoll, Id crit, Id hit, AtkVar variable) {



		//get old value, or default to zero
		float value = variable.getValue();

		//if it crits, add the chance that it is critting to the chance it went off normally
		if (weaponVariables.containsKey(AtkVar.Id.CRIT_KNOCKDOWN)){
			value += (1 - value) * hitRoll.critChance;
		}

		//if it is on hit, then it happens regardless
		if (weaponVariables.containsKey(AtkVar.Id.KNOCKDOWN)){
			value = hitRoll.hitChance;
		}

		variable.setValue(value);


	}

	public Map<Integer, AtkVar> getSustainedVariables() {
		return sustainedVariables;
	}

	public void setSustainedVariables(Map<Integer, AtkVar> sustainedVariables) {
		this.sustainedVariables = sustainedVariables;
	}





}
