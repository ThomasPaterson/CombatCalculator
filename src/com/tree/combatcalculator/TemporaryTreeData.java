package com.tree.combatcalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tree.combatcalculator.AtkVarCopy.Id;
import com.tree.combatcalculator.nodes.Node;

public class TemporaryTreeData {
	
	public int focus;
	public List<WeaponCountHolder> weaponHolders;
	public Map<Id, AtkVarCopy> variables;
	
	public TemporaryTreeData(TemporaryTreeData tempData) {
		
		this.focus = tempData.focus;
		this.weaponHolders = new ArrayList<WeaponCountHolder>(tempData.weaponHolders);
		this.variables = new HashMap<Id, AtkVarCopy>(tempData.variables);
	}

	public TemporaryTreeData(int numFocus,
			List<WeaponCountHolder> weaponHolders,
			Map<Id, AtkVarCopy> variables) {
		
		this.focus = numFocus;
		this.weaponHolders = weaponHolders;
		this.variables = variables;
		
	}



	public void clearTempValues() {
		
		Iterator<Entry<Id, AtkVarCopy>> entries = variables.entrySet().iterator();
		
		while (entries.hasNext()) {
		    Map.Entry entry = (Map.Entry) entries.next();
		    AtkVarCopy value = (AtkVarCopy)entry.getValue();
		    
		    if (value.getDuration().equals(AtkVarCopy.Duration.TEMPORARY_STATE))
		    	entries.remove();
		}
	}

	public boolean contains(Id id) {
		return variables.containsKey(id);
	}
	
	public void putCrits(Node attackNode, PermanentTreeData permData){
		
		Weapon weapon = permData.attacker.getWeapons().get(attackNode.getWeaponIndex());
		
		for (AtkVarCopy.Id id : weapon.getAtkVarIdsWithPermState()){
			
			float critChance = AttackCalcHelper.calcCritWithoutCritProbability(attackNode, permData);
			
			if (variables.containsKey(id))
				addCritChance(id, critChance);
			else
				modifyCritChance(id, critChance);
			
			
		}
		
		
	}

	private void modifyCritChance(Id id, float critChance) {
		
		AtkVarCopy newCrit = AtkVarCopy.createAtkVar(id);
		
		newCrit.setValue(critChance);
		
		variables.put(id, newCrit);
		
	}

	private void addCritChance(Id id, float critChance) {
		
		AtkVarCopy oldCrit = variables.get(id);
		
		float newCritChance = oldCrit.getValue() + (1-oldCrit.getValue())*critChance;
		
		oldCrit.setValue(newCritChance);
		
	}


}
