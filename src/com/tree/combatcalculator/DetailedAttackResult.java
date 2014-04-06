package com.tree.combatcalculator;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;



public class DetailedAttackResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4969741298965801915L;
	private float hitDamage;
	private float hitChance;
	private float critChance;
	private float critDamage;
	private String hitChancePer;
	private String hitAndCritChancePer;
	private String critChancePer;
	private boolean hasCrit;

	private Weapon weapon;
	private boolean isCMA = false;
	private int cmaGroup = 0;
	private int cmaTotal = 0;
	private static NumberFormat f = new DecimalFormat("###,###,###,###,##0.##");
	private boolean boughtAttackBoost = false;
	private boolean boughtDamageBoost = false;
	private boolean boughtAttack = false;
	private boolean isCharge = false;

	public DetailedAttackResult(){

	}


	public DetailedAttackResult(float hitDamage, float hitChance, boolean hasCrit, float critChance, float critDamage, Weapon weapon){
		this.hitDamage = hitDamage;
		this.hitChance = hitChance;
		this.hasCrit = hasCrit;
		this.critChance = critChance;
		this.critDamage = critDamage;
		this.weapon = weapon;
		this.hitChancePer = f.format(hitChance*100);
		this.hitAndCritChancePer = f.format((hitChance + critChance) * 100);
		this.critChancePer = f.format(critChance*100);
	}

	public DetailedAttackResult(float hitDamage, float hitChance, boolean hasCrit, float critChance, float critDamage, Weapon weapon,
			boolean isCMA, int cmaNum, int cmaTotal){
		this.hitDamage = hitDamage;
		this.hitChance = hitChance;
		this.hasCrit = hasCrit;
		this.critChance = critChance;
		this.critDamage = critDamage;
		this.weapon = weapon;
		this.hitChancePer = f.format(hitChance*100);
		this.hitAndCritChancePer = f.format((hitChance + critChance) * 100);
		this.critChancePer = f.format(critChance*100);
		this.isCMA = isCMA;
		this.cmaGroup = cmaNum;
		this.cmaTotal = cmaTotal;
	}

	public String getTruncExpectedHitDamage(){
		return f.format(hitDamage*hitChance);
	}

	public String getTruncExpectedCritDamage(){
		return f.format(critDamage*critChance);
	}

	public float getTotalExpectedDamageWithHitChance(boolean usingCrits){
		if (usingCrits)
			return hitChance*hitDamage + critChance*critDamage;
		else
			return (hitChance + critChance)*hitDamage;
	}

	public float getTotalExpectedDamageAssumeHit(){
		return hitDamage*hitChance/(hitChance+critChance)+critDamage*critChance/(hitChance+critChance);
	}

	static public String formatResult(float result){
		return f.format(result);
	}


	static public String totalHits(List<DetailedAttackResult> attackResults, boolean usingCrits){

		float total = 0;

		if (usingCrits)
			for (DetailedAttackResult a : attackResults)
				total += a.hitChance;
		else
			for (DetailedAttackResult a : attackResults)
				total += a.hitChance + a.critChance;

		return f.format(total);
	}

	static public String expDamage(List<DetailedAttackResult> attackResults, boolean usingCrits){

		float total = 0;

		if (usingCrits)
			for (DetailedAttackResult a : attackResults)
				total += a.getTotalExpectedDamageWithHitChance(usingCrits);
		else
			for (DetailedAttackResult a : attackResults)
				total += (a.hitChance + a.critChance) * a.hitDamage;

		return f.format(total);
	}

	static public String expDamageAllHit(List<DetailedAttackResult> attackResults, boolean usingCrits){

		float total = 0;

		if (usingCrits)
			for (DetailedAttackResult a : attackResults)
				total += a.getTotalExpectedDamageAssumeHit();
		else
			for (DetailedAttackResult a : attackResults)
				total += a.hitDamage;

		return f.format(total);
	}

	static public String totalCrits(List<DetailedAttackResult> attackResults, boolean usingCrits){

		float total = 0;

		if (usingCrits)
			for (DetailedAttackResult a : attackResults)
				total += a.critChance;


		return f.format(total);
	}

	public String constructAttackValues() {
		String values = "";

		if (isCharge)
			values += "C=> ";

		if (boughtAttackBoost)
			values += "+A ";

		if (boughtDamageBoost)
			values += "+D ";

		if (boughtAttack)
			values += "$B ";


		return values;
	}

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

	public String getHitChancePer() {
		return hitChancePer;
	}

	public void setHitChancePer(String hitChancePer) {
		this.hitChancePer = hitChancePer;
	}

	public String getHitAndCritChancePer() {
		return hitAndCritChancePer;
	}

	public void setHitAndCritChancePer(String hitAndCritChancePer) {
		this.hitAndCritChancePer = hitAndCritChancePer;
	}

	public String getCritChancePer() {
		return critChancePer;
	}

	public void setCritChancePer(String critChancePer) {
		this.critChancePer = critChancePer;
	}

	public boolean isHasCrit() {
		return hasCrit;
	}

	public void setHasCrit(boolean hasCrit) {
		this.hasCrit = hasCrit;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	public boolean isCMA() {
		return isCMA;
	}

	public void setCMA(boolean isCMA) {
		this.isCMA = isCMA;
	}

	public int getCmaGroup() {
		return cmaGroup;
	}

	public void setCmaGroup(int cmaGroup) {
		this.cmaGroup = cmaGroup;
	}

	public int getCmaTotal() {
		return cmaTotal;
	}

	public void setCmaTotal(int cmaTotal) {
		this.cmaTotal = cmaTotal;
	}

	public boolean isBoughtAttackBoost() {
		return boughtAttackBoost;
	}

	public void setBoughtAttackBoost(boolean boughtAttackBoost) {
		this.boughtAttackBoost = boughtAttackBoost;
	}

	public boolean isBoughtDamageBoost() {
		return boughtDamageBoost;
	}

	public void setBoughtDamageBoost(boolean boughtDamageBoost) {
		this.boughtDamageBoost = boughtDamageBoost;
	}

	public boolean isBoughtAttack() {
		return boughtAttack;
	}

	public void setBoughtAttack(boolean boughtAttack) {
		this.boughtAttack = boughtAttack;
	}

	public boolean isCharge() {
		return isCharge;
	}

	public void setCharge(boolean isCharge) {
		this.isCharge = isCharge;
	}







}
