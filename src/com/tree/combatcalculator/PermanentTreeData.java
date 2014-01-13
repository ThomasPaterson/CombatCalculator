package com.tree.combatcalculator;

import java.util.List;
import java.util.Map;

public class PermanentTreeData {
	
	public List<Map<String, AtkVar>> permState;
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