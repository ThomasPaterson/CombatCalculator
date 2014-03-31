package com.tree.combatcalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tree.combatcalculator.AtkVar.Id;
import com.tree.combatcalculator.nodes.Node;

public class TemporaryTreeData {
	
	public int focus;
	public List<WeaponCountHolder> weaponHolders;
	public Map<Id, AtkVar> variables;
	
	public TemporaryTreeData(TemporaryTreeData tempData) {
		
		this.focus = tempData.focus;
		this.weaponHolders = new ArrayList<WeaponCountHolder>(tempData.weaponHolders);
		this.variables = new HashMap<Id, AtkVar>(tempData.variables);
	}

	public TemporaryTreeData(int numFocus,
			List<WeaponCountHolder> weaponHolders,
			Map<Id, AtkVar> variables) {
		
		this.focus = numFocus;
		this.weaponHolders = weaponHolders;
		this.variables = variables;
		
	}



	public void clearTempValues() {
		
		Iterator<Entry<Id, AtkVar>> entries = variables.entrySet().iterator();
		
		while (entries.hasNext()) {
		    Map.Entry entry = (Map.Entry) entries.next();
		    AtkVar value = (AtkVar)entry.getValue();
		    
		    if (value.getDuration().equals(AtkVarCopy.AtkVar.TEMPORARY_STATE))
		    	entries.remove();
		}
	}

	public boolean contains(Id id) {
		return variables.containsKey(id);
	}
	
	public void putCrits(Node attackNode, PermanentTreeData permData){
		
		Weapon weapon = permData.attacker.getWeapons().get(attackNode.getWeaponIndex());
		
		for (AtkVar.Id id : weapon.getAtkVarIdsWithPermState()){
			
			float critChance = AttackCalcHelper.calcCritWithoutCritProbability(attackNode, permData);
			
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


}
