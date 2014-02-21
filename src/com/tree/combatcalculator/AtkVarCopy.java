package com.tree.combatcalculator;

/**
 * @(#)AtkVar.java
 *
 *A class for managing variables and holding all the values/names for the various possibilities
 *
 * @author
 * @version 1.00 2012/9/12
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import android.util.Log;

import com.tree.combatcalculator.AtkVarCopy.Group;
import com.tree.combatcalculator.AtkVarCopy.Id;
import com.tree.combatcalculator.nodes.Node;

public class AtkVarCopy {


	public enum Id {
		 CRIT_PATH,CHARGE, FREE_CHARGE, SET_DEFENSE, ASSAULT,
		 COMBINED, CMA, CRA, BRUTAL_CHARGE, POWERFUL_CHARGE,
		 CRIT_SUSTAINED_ATK, SUSTAINED_ATK, POWERFUL_ATK, WEAPONMASTER,
		 CRIT_DOUBLE_DAMAGE, DOUBLE_DAMAGE, KNOCKED_DOWN,
		 POINT_BLANK, GUNFIGHTER, STAR_ARM_PIERCE, ARM_PIERCE,
		 REROLL_DAM, REROLL_ATK, DISCARD_DAM, DISCARD_ATK, CRIT_ATK,
		 CRIT_DAM, CRIT_KNOCKDOWN, MOD_ARM, MOD_DEF, MOD_DAM, MOD_HIT, 
		 ADD_DAM, ADD_HIT, BOOSTED_DAM, BOOSTED_HIT, CHARGE_DAMAGE, CHARGING,
		 AUTO_CRIT, AUTO_HIT; 
		}
	
	
	public enum Group {
		 ATTACKER, 
		 DEFENDER, 
		 WEAPON, 
		 SITUATION,
		 TEMPORARY; 
	}
	
	public enum State {
		 TEMP, 
		 PERM, 
		 BOTH; 
	}
	
	public enum ValueType {
		 FLAG, 
		 COUNTER, 
		 PROB; 
	}
	
	public enum Conditional {
		CRIT,
		CHARGING,
		MELEE, 
		RANGED, 
		ALL_INITIALS, 
		HIT, 
		BOOSTED_HIT; 
	}
	
	public enum Modifier {
		STAR_ATTACK,
		CRIT;
	}
	
	public enum Continuous {
		TRUE;
	}
	
	public enum Triggered {
		KNOCKED_DOWN, 
		ADD_DAM, 
		SHRED, 
		AUTO_HIT, 
		BOOSTED_DAM, 
		DOUBLE_DAMAGE;
	}
	
	private float value;
	private Id id;
	private Group group;
	private State state;
	private ValueType valueType;
	private List<Conditional> conditions;
	private List<Modifier> modifiers;
	private List<Triggered> triggered;
	private Continuous continuous;
	private int weaponIndex;
	static private Map<AtkVarCopy.Id, AtkVarCopy> atkVarCopyCache = null;
	
	
	
	public AtkVarCopy(AtkVarCopy atkVarCopy) {
		value = atkVarCopy.getValue();
		id = atkVarCopy.getId();
		group = atkVarCopy.getGroup();
		state = atkVarCopy.getState();
		valueType = atkVarCopy.getValueType();
		conditions = new ArrayList<Conditional>(atkVarCopy.getConditions());
		modifiers = new ArrayList<Modifier>(atkVarCopy.getModifiers());
		
	}
	
	public static AtkVarCopy createAtkVar(Id id, Group group){

		AtkVarCopy atkVar = createAtkVar(id);
		atkVar.setGroup(group);
		return atkVar;
	}
	

	public static AtkVarCopy createAtkVar(Id id){
		
		if (atkVarCopyCache == null)
			readCacheFromXml();
		
		AtkVarCopy atkVar = atkVarCopyCache.get(id);
		
		try {
		Validate.isTrue(atkVar.getState().equals(State.TEMP));
		
		}catch (Exception e){
			Log.e(AtkVarCopy.class.toString(), "non flag initialized as flag", e);
		}
		
		return atkVar;
	}
	
	
	public AtkVarCopy createAtkVar(Id id, float newValue, Group group){
		
		AtkVarCopy atkVar = createAtkVar(id, newValue);
		atkVar.setGroup(group);
		
		return atkVar;
	}
	
	public AtkVarCopy createAtkVar(Id id, float newValue){
		
		if (atkVarCopyCache == null)
			readCacheFromXml();
		
		AtkVarCopy atkVar = new AtkVarCopy(atkVarCopyCache.get(id));
		atkVar.setValue(newValue);
		
		try {
		Validate.isTrue(!atkVar.getState().equals(State.TEMP));
		
		}catch (Exception e){
			Log.e(AtkVarCopy.class.toString(), "non flag initialized as flag", e);
		}
		
		return atkVar;
	}


	private static void readCacheFromXml() {
		// TODO Auto-generated method stub
		
	}
	



	public float getValue() {
		return value;
	}



	public void setValue(float value) {
		this.value = value;
	}



	public Id getId() {
		return id;
	}



	public void setId(Id id) {
		this.id = id;
	}



	public Group getGroup() {
		return group;
	}



	public void setGroup(Group group) {
		this.group = group;
	}



	public State getState() {
		return state;
	}



	public void setState(State state) {
		this.state = state;
	}



	public ValueType getValueType() {
		return valueType;
	}



	public void setValueType(ValueType valueType) {
		this.valueType = valueType;
	}



	public List<Conditional> getConditions() {
		return conditions;
	}



	public void setConditions(List<Conditional> conditions) {
		this.conditions = conditions;
	}



	public List<Modifier> getModifiers() {
		return modifiers;
	}



	public void setModifier(List<Modifier> groupFlags) {
		this.modifiers = modifiers;
	}
	
	public Continuous getContinuous() {
		return continuous;
	}

	public void setContinuous(Continuous continuous) {
		this.continuous = continuous;
	}

	public int getWeaponIndex() {
		return weaponIndex;
	}

	public void setWeaponIndex(int weaponIndex) {
		this.weaponIndex = weaponIndex;
	}
	
	static public int getHash(AtkVarCopy a){
		
		String identifier = a.getGroup().toString() + 
				a.getId().toString() + Integer.toString(a.getWeaponIndex());
		
		return identifier.hashCode();
	}
	
	public int getHash(){
		
		String identifier = getGroup().toString() + 
				getId().toString() + Integer.toString(getWeaponIndex());
		
		return identifier.hashCode();
	}

	public static Boolean contains(Map<Group, List<AtkVarCopy>> variables,
			Group group, Id id) {
		
		List<AtkVarCopy> targetGroup = variables.get(group);
		
		for (AtkVarCopy t : targetGroup)
			if (t.getId().equals(id))
				return true;
		
		return false;
	}

	public List<Triggered> getTriggered() {
		return triggered;
	}

	public void setTriggered(List<Triggered> triggered) {
		this.triggered = triggered;
	}







}