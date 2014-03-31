package com.tree.combatcalculator;

import java.util.List;
import java.util.Map;

import com.tree.combatcalculator.AtkVar.Group;
import com.tree.combatcalculator.AtkVar.Id;

public class PermanentTreeData {
	
	public Map<AtkVar.Group, List<AtkVar> > variables;
	public DefendModel defender;
	public AttackModel attacker;
	public AttackCalculator atkCalc;
	
	
	
	public boolean checkGroupsContains(Id id, int weaponIndex, Group ... groups) {
	
		for (Group g : groups){
			
			for (AtkVar atkVar : variables.get(g)){
				
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
	
	//return first star attack variable found for that weapon
	public AtkVar findStarAttack(int weaponIndex) {
		
		List<AtkVar> weaponVars = variables.get(AtkVar.Group.WEAPON);
		
		for (AtkVar w : weaponVars){
			if (w.getWeaponIndex() == weaponIndex){
				if (w.getModifiers().contains(AtkVar.Modifier.STAR_ATTACK))
					return w;
			}
		}
		
		return null;
		
	}




}
