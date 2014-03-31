package com.tree.combatcalculator;

/**
 * @(#)AtkVar.java
 *
 *A class for managing variables and holding all the values/names for the various possibilities
 *
 * @author
 * @version 1.00 2012/9/12
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.xmlpull.v1.XmlPullParserException;

import com.example.combatcalculator.AttackProperty;
import com.example.combatcalculator.AttackPropertyXmlParser;
import com.example.combatcalculator.ConfigManager;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class AtkVar implements Parcelable{
	
	public enum VariableType {
		 Id, Group, State, ValueType, Conditional, 
		 Modifier, Continuous, Triggered, Value;
	}


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
		DOUBLE_DAMAGE,
		MOD_DEF,
		MOD_DAM;
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
	
	public AtkVar(){
		conditions = new ArrayList<Conditional>();
		modifiers = new ArrayList<Modifier>();
		triggered = new ArrayList<Triggered>();
	}
	
	public AtkVar(Parcel in){
		readFromParcel(in);
	}
	

	public AtkVar(AtkVar atkVarCopy) {
		value = atkVarCopy.getValue();
		id = atkVarCopy.getId();
		group = atkVarCopy.getGroup();
		state = atkVarCopy.getState();
		valueType = atkVarCopy.getValueType();
		conditions = new ArrayList<Conditional>(atkVarCopy.getConditions());
		modifiers = new ArrayList<Modifier>(atkVarCopy.getModifiers());
		
	}
	
	public static AtkVar createAtkVar(Id id, Group group){

		AtkVar atkVar = createAtkVar(id);
		atkVar.setGroup(group);
		return atkVar;
	}
	

	public static AtkVar createAtkVar(Id id){
		

		AtkVar atkVar = AtkVarCache.getInstance().get(id);
		
		try {
		Validate.isTrue(atkVar.getState().equals(State.TEMP));
		
		}catch (Exception e){
			Log.e(AtkVar.class.toString(), "non flag initialized as flag", e);
		}
		
		return atkVar;
	}
	
	
	public AtkVar createAtkVar(Id id, float newValue, Group group){
		
		AtkVar atkVar = createAtkVar(id, newValue);
		atkVar.setGroup(group);
		
		return atkVar;
	}
	
	public AtkVar createAtkVar(Id id, float newValue){
		

		
		AtkVar atkVar = new AtkVar(AtkVarCache.getInstance().get(id));
		atkVar.setValue(newValue);
		
		try {
		Validate.isTrue(!atkVar.getState().equals(State.TEMP));
		
		}catch (Exception e){
			Log.e(AtkVar.class.toString(), "non flag initialized as flag", e);
		}
		
		return atkVar;
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
	
	static public int getHash(AtkVar a){
		
		String identifier = a.getGroup().toString() + 
				a.getId().toString() + Integer.toString(a.getWeaponIndex());
		
		return identifier.hashCode();
	}
	
	public int getHash(){
		
		String identifier = getGroup().toString() + 
				getId().toString() + Integer.toString(getWeaponIndex());
		
		return identifier.hashCode();
	}

	public static Boolean contains(Map<Group, List<AtkVar>> variables,
			Group group, Id id) {
		
		List<AtkVar> targetGroup = variables.get(group);
		
		for (AtkVar t : targetGroup)
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
	
	public void addVariable(VariableType type, String value){

		switch(type) {
			
			case Id: 
					this.id = getEnumFromString(Id.class, value); 
					break;
			case Group: 
					this.group = getEnumFromString(Group.class, value); 
					break;
			case State: 
					this.state = getEnumFromString(State.class, value); 
					break;
			case ValueType: 
					this.valueType = getEnumFromString(ValueType.class, value); 
					break;
			case Conditional: 
					this.conditions.add(getEnumFromString(Conditional.class, value)); 
					break;
			case Modifier: 
					this.modifiers.add(getEnumFromString(Modifier.class, value)); 
					break;
			case Continuous: 
					this.continuous = getEnumFromString(Continuous.class, value); 
					break;
			case Triggered: 
					this.triggered.add(getEnumFromString(Triggered.class, value)); 
			case Value:
					this.value = Integer.parseInt(value);
					break;
			
			default: Log.e("invalid variable", "can't add variable type:" + type.toString());
		}
			
	}
	
	public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string)
	{
	    if( c != null && string != null )
	    {
	        try
	        {
	            return Enum.valueOf(c, string.trim().toUpperCase());
	        }
	        catch(IllegalArgumentException ex)
	        {
	        	Log.e("invalid variable type", "can't add enum value:" + string);
	        }
	    }
	    return null;
	}



	public boolean checkCrit() {
			return getConditions().contains(Conditional.CRIT);
	}



  	//parcel stuff
  	
  	public int describeContents() {
		return 0;
	}
  	
  	//*
  	
  	/**
	 *Creates all the values from the parcel
	 */
  	private void readFromParcel(Parcel in){
  		
  		value = in.readFloat();
  		id = (Id)in.readSerializable();
  		group = (Group)in.readSerializable();
  		state = (State)in.readSerializable();
  		valueType = (ValueType)in.readSerializable();
  		
  		conditions = new ArrayList<Conditional>();
  		in.readList(conditions, null);
  		modifiers = new ArrayList<Modifier>();
  		in.readList(modifiers, null);
  		triggered = new ArrayList<Triggered>();
  		in.readList(triggered, null);
  		
  		continuous = (Continuous)in.readSerializable();
  		weaponIndex = in.readInt();

  		
  	}

	
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeFloat(value);
		dest.writeSerializable(id);
		dest.writeSerializable(group);
		dest.writeSerializable(state);
		dest.writeSerializable(valueType);
		dest.writeList(conditions);
		dest.writeList(modifiers);
		dest.writeList(triggered);
		dest.writeSerializable(continuous);
		dest.writeInt(weaponIndex);

	}
	
  	
  	
  	//end parcel stuff
	
	/**
    *
    * This field is needed for Android to be able to
    * create new objects, individually or as arrays.
    *

    */
   public static final Parcelable.Creator<AtkVar> CREATOR =
   	new Parcelable.Creator<AtkVar>() {
           public AtkVar createFromParcel(Parcel in) {
               return new AtkVar(in);
           }
           
           public AtkVar[] newArray(int size) {
               return new AtkVar[size];
           }
       };



	







}