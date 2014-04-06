package com.tree.combatcalculator.AttackCalculation;

public class AttackResult {

	private float hitDamage;
	private float hitChance;
	private float critChance;
	private float critDamage;

	public float getHitDamage() {
		return hitDamage;
	}
	public void setHitDamage(float hitDamage) {
		this.hitDamage = hitDamage;
	}
	public float getHitChance() {
		return hitChance;
	}
	public void setHitChance(float hitChance) {
		this.hitChance = hitChance;
	}
	public float getCritChance() {
		return critChance;
	}
	public void setCritChance(float critChance) {
		this.critChance = critChance;
	}
	public float getCritDamage() {
		return critDamage;
	}
	public void setCritDamage(float critDamage) {
		this.critDamage = critDamage;
	}

}
