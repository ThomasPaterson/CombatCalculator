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
		 STAR_ATTACK, BOOSTED_ATTACK, CRIT, CRIT_PATH, BOOSTED_DAMAGE, CHARGE, FREE_CHARGE; 
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
		 ALL_INITIALS; 
	}
	
	public enum Modifier {
		STAR_ATTACK,
		CRIT;
	}
	
	public enum Duration {
		CONTINUOUS_STATE,
		TEMPORARY_STATE;
	}
	
	private float value;
	private Id id;
	private Group group;
	private State state;
	private ValueType valueType;
	private List<Conditional> conditions;
	private List<Modifier> modifiers;
	private Duration duration;
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
	
	public static Map<Integer, AtkVarCopy> setupPermState(
			Map<Group, List<AtkVarCopy>> permState) {
		
		//TODO: Determine how the interface is going to pass in variables

		return null;
		
	}

	public static boolean contains(Id starAttack, PermanentTreeData permData) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean checkGroup(Group attacker, Id boostedAttack) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean checkWeaponGroup(Group weapon, int weaponIndex,
			Id boostedAttack) {
		// TODO Auto-generated method stub
		return false;
	}

	public static void putCrits(Map<Id, AtkVarCopy> variables) {
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
	
	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
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







}