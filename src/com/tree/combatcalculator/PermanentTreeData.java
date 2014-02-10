package com.tree.combatcalculator;

import java.util.List;
import java.util.Map;

import com.tree.combatcalculator.AtkVarCopy.Group;
import com.tree.combatcalculator.AtkVarCopy.Id;

public class PermanentTreeData {
	
	public Map<AtkVarCopy.Group, List<AtkVarCopy> > variables;
	public DefendModel defender;
	public AttackModel attacker;
	public int optimization;
	public AttackCalculator atkCalc;
	public boolean useHeuristics;
	public List<Weapon> optimalWeapons;
	
	
	public List<Weapon> getOptimalWeapons() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public boolean contains(Id starAttack, PermanentTreeData permData) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean checkGroupsContains(Id id, int weaponIndex, Group ... groups) {
	
		for (Group g : groups){
			
			for (AtkVarCopy atkVar : variables.get(g)){
				
				if (atkVar.getId().equals(id)){
					if (g.equals(Group.WEAPON)){
						if (atkVar.getWeaponIndex() == weaponIndex)
							return true;
					}else{
						return true;
					}
				}
			}
			
		}
		
		return false;
	}



}
