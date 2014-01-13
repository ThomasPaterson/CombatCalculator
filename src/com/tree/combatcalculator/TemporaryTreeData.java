package com.tree.combatcalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tree.combatcalculator.AtkVarCopy.Id;

public class TemporaryTreeData {
	
	public int focus;
	public List<WeaponCountHolder> weaponHolders;
	public Map<Id, AtkVarCopy> variables;
	
	public TemporaryTreeData(TemporaryTreeData tempData) {
		
		this.focus = tempData.focus;
		this.weaponHolders = new ArrayList<WeaponCountHolder>(tempData.weaponHolders);
		this.variables = new HashMap<Id, AtkVarCopy>(tempData.variables);
	}

}
