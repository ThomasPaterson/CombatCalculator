package com.tree.combatcalculator.Dice;

public final class HitRoll {

	public float hitChance = 0.0f;
	public float critChance = 0.0f;


	public HitRoll (){

	}

	public HitRoll (float hitChance, float critChance){
		this.hitChance = hitChance;
		this.critChance = critChance;
	}
}
