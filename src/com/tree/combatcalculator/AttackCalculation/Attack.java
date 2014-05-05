package com.tree.combatcalculator.AttackCalculation;

import java.util.Map;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.AtkVar.Id;
import com.tree.combatcalculator.AttackModel;
import com.tree.combatcalculator.DefendModel;
import com.tree.combatcalculator.DynamicAttackData;
import com.tree.combatcalculator.StaticAttackData;
import com.tree.combatcalculator.Weapon;

public class Attack {

	private int baseAtk;
	private int basePow;
	private int baseDef;
	private int baseArm;
	//private int baseHealth;

	private int modAtk = 0;
	private int modPow = 0;
	private int modDef = 0;
	private int modArm = 0;

	private boolean boostedAtk = false;
	private boolean boostedDam = false;
	private int addAtk = 0;
	private int addDam = 0;

	private int disAtk = 0;
	private int disDam = 0;
	private boolean rerollAtk = false;
	private boolean rerollDam = false;
	private float autoHit = 0.0f;
	private float sustained = 0.0f;
	private float knockedDown = 0.0f;
	private boolean shred = false;
	private boolean doubleDamage = false;
	private boolean crit = false;
	private Map<AtkVar.Id, AtkVar> currentAtkVars;

	private boolean isMelee;




	public Attack(StaticAttackData permData, DynamicAttackData tempData, int weaponIndex) throws InvalidAttackException{

		AttackModel attacker = permData.getAttacker();
		DefendModel defender = permData.getDefender();
		Weapon weapon = attacker.getWeapons().get(weaponIndex);

		setBaseVariables(attacker, defender, weapon);

		currentAtkVars = AtkVarParser.getInstance().parseAtkVars(
				permData.getWeaponVariables(),
				permData.getVariables(),
				tempData.getVariables(),
				weaponIndex);

		checkValidAttack(currentAtkVars, weapon);

		AtkVarParser.getInstance().checkConstraints(currentAtkVars);
		AtkVarParser.getInstance().processVariables(this, currentAtkVars);
		calculateAttackResult();
	}





	private void calculateAttackResult() {



	}



	private void checkValidAttack(Map<Id, AtkVar> atkVars, Weapon weapon) throws InvalidAttackException{


		checkRanged(atkVars, weapon);
		checkMelee(atkVars, weapon);


	}




	private void checkRanged(Map<Id, AtkVar> atkVars, Weapon weapon) throws InvalidAttackException {

		boolean isRanged = atkVars.containsKey(AtkVar.Id.RANGED);
		boolean isGunfighter = atkVars.containsKey(AtkVar.Id.GUNFIGHTER);
		boolean isAssault = atkVars.containsKey(AtkVar.Id.ASSAULT) && atkVars.containsKey(AtkVar.Id.CHARGE);

		if (!(isRanged || isGunfighter || isAssault))
			throw new InvalidAttackException("cannot make an attack with this ranged weapon");


	}



	private void checkMelee(Map<Id, AtkVar> atkVars, Weapon weapon) throws InvalidAttackException{

		if (atkVars.containsKey(AtkVar.Id.RANGED))
			throw new InvalidAttackException("Melee weapons cannot be used to make ranged attacks");

	}



	private void setBaseVariables(AttackModel attacker, DefendModel defender, Weapon weapon){

		baseAtk = attacker.getMat();
		basePow = weapon.getPow();
		baseDef = defender.getDef();
		baseArm = defender.getArm();

		modAtk = baseAtk;
		modPow = basePow;
		modDef = baseDef;
		modArm = baseArm;

	}




	public int getBaseAtk() {
		return baseAtk;
	}



	public void setBaseAtk(int baseAtk) {
		this.baseAtk = baseAtk;
	}



	public int getBasePow() {
		return basePow;
	}



	public void setBasePow(int basePow) {
		this.basePow = basePow;
	}



	public int getBaseDef() {
		return baseDef;
	}



	public void setBaseDef(int baseDef) {
		this.baseDef = baseDef;
	}



	public int getBaseArm() {
		return baseArm;
	}



	public void setBaseArm(int baseArm) {
		this.baseArm = baseArm;
	}



	public int getModAtk() {
		return modAtk;
	}



	public void setModAtk(int modAtk) {
		this.modAtk = modAtk;
	}



	public int getModPow() {
		return modPow;
	}



	public void setModPow(int modPow) {
		this.modPow = modPow;
	}



	public int getModDef() {
		return modDef;
	}



	public void setModDef(int modDef) {
		this.modDef = modDef;
	}



	public int getModArm() {
		return modArm;
	}



	public void setModArm(int modArm) {
		this.modArm = modArm;
	}



	public boolean isBoostedAtk() {
		return boostedAtk;
	}



	public void setBoostedAtk(boolean boostedAtk) {
		this.boostedAtk = boostedAtk;
	}



	public boolean isBoostedDam() {
		return boostedDam;
	}



	public void setBoostedDam(boolean boostedDam) {
		this.boostedDam = boostedDam;
	}



	public int getAddAtk() {
		return addAtk;
	}



	public void setAddAtk(int addAtk) {
		this.addAtk = addAtk;
	}



	public int getAddDam() {
		return addDam;
	}



	public void setAddDam(int addDam) {
		this.addDam = addDam;
	}



	public int getDisAtk() {
		return disAtk;
	}



	public void setDisAtk(int disAtk) {
		this.disAtk = disAtk;
	}



	public int getDisDam() {
		return disDam;
	}



	public void setDisDam(int disDam) {
		this.disDam = disDam;
	}



	public boolean isRerollAtk() {
		return rerollAtk;
	}



	public void setRerollAtk(boolean rerollAtk) {
		this.rerollAtk = rerollAtk;
	}



	public boolean isRerollDam() {
		return rerollDam;
	}



	public void setRerollDam(boolean rerollDam) {
		this.rerollDam = rerollDam;
	}



	public boolean isShred() {
		return shred;
	}



	public void setShred(boolean shred) {
		this.shred = shred;
	}



	public boolean isDoubleDamage() {
		return doubleDamage;
	}



	public void setDoubleDamage(boolean doubleDamage) {
		this.doubleDamage = doubleDamage;
	}



	public boolean isMelee() {
		return isMelee;
	}



	public void setMelee(boolean isMelee) {
		this.isMelee = isMelee;
	}



	public float getAutoHit() {
		return autoHit;
	}



	public void setAutoHit(float autoHit) {
		this.autoHit = autoHit;
	}



	public float getSustained() {
		return sustained;
	}



	public void setSustained(float sustained) {
		this.sustained = sustained;
	}



	public float getKnockedDown() {
		return knockedDown;
	}



	public void setKnockedDown(float knockedDown) {
		this.knockedDown = knockedDown;
	}



	public boolean getCrit() {
		return crit;
	}



	public void setCrit(boolean crit) {
		this.crit = crit;
	}









}
