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



}
