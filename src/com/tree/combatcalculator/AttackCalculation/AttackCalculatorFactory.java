package com.tree.combatcalculator.AttackCalculation;

import com.tree.combatcalculator.DiceHolder;

public class AttackCalculatorFactory {

	private static AttackCalculator instance = null;

	public static AttackCalculator getInstance(){
		if (instance == null)
			throw new IllegalStateException();

		return instance;
	}

	public static void setInstance(DiceHolder holder){
		instance = new AttackCalculator(holder);
	}

}
