package com.example.combatcalculator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.tree.combatcalculator.AtkVar;

public class AttackProperty implements Parcelable{
	
	private final static String BINARY = "binary";
	public final static String DEFAULT_VALUE = "0";
	
	private String name;
	private String display;
	private String value = DEFAULT_VALUE;
	private String type;
	
	public AttackProperty(String name, String display, String value, String type){
		this.name = name;
		this.display = display;
		this.value = value;
		this.type = type;
	}
	
	public AttackProperty(String name, String display, String type){
		this.name = name;
		this.display = display;
		this.type = type;
	}

	public AttackProperty(Parcel in) {
		readFromParcel(in);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}
	
	public boolean hasValues() {
		if (type.equals(BINARY))
			return false;
		return true;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public AtkVar convertToAtkVar(){
		
		if (type.equals(BINARY)){
			return new AtkVar(name);
		}else{
			return new AtkVar(name, Integer.parseInt(value));
		}		
	}

	public int describeContents() {
		return 0;
	}


  	private void readFromParcel(Parcel in){
  		
  		name = in.readString();
  		display = in.readString();
  		value = in.readString();
  		type = in.readString();
  		
  	}

	
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(name);
		dest.writeString(display);
		dest.writeString(value);
		dest.writeString(type);
	}
	
	@Override
	public String toString(){
		return display;
	}
	
	
	public static final List<AttackProperty> getOptions(String type, Context context){
		
		String file = null;
		List<AttackProperty> options = new ArrayList<AttackProperty>();
		System.out.println("getting type: " + type);
		
		if (type.equals(ConfigManager.ATTACKER))
			file = ConfigManager.ATTACKER_XML;
		else if (type.equals(ConfigManager.DEFENDER))
			file = ConfigManager.DEFENDER_XML;
		else if (type.equals(ConfigManager.WEAPONS))
			file = ConfigManager.WEAPON_XML;
		else if (type.equals(ConfigManager.WEAPONS_STAR))
			file = ConfigManager.SITUATION_XML;
		
		if (file == null){
			Log.e("ATTACKPROPERTY", "No file selected");
		}else{
			
			try {
				
				InputStream istr = context.getAssets().open(file);
				options = AttackPropertyXmlParser.parse(istr);
				Log.d(AttackProperty.class.getName(), "grabbed xml files");
			}catch(Exception e){
				Log.e(AttackProperty.class.getName(), "attempted to grab xml files", e);
			}
		}
		
		return options;
	
	}
	
	public static final List<Map<String, AttackProperty>> makeMapForSpinner(List<AttackProperty> toBeMapped){
		
		List<Map<String, AttackProperty>> list = new ArrayList<Map<String, AttackProperty>>();
		
		for (AttackProperty property : toBeMapped){
			Map<String, AttackProperty> map = new HashMap<String, AttackProperty>(); 
			map.put("Name", property);
			list.add(map);
		}
		
		return list;
	}
	
	public static final ArrayList<AtkVar> convertAttackProperties(List<AttackProperty> AttackProperties){
		
		ArrayList<AtkVar> atkVars = new ArrayList<AtkVar>();
		
		for (AttackProperty property : AttackProperties) {
			atkVars.add(property.convertToAtkVar());
		}
		
		return atkVars;
		
	}
	
	
  	
   public static final Parcelable.Creator<AttackProperty> CREATOR =
   	new Parcelable.Creator<AttackProperty>() {
           public AttackProperty createFromParcel(Parcel in) {
               return new AttackProperty(in);
           }
           
           public AttackProperty[] newArray(int size) {
               return new AttackProperty[size];
           }
       };
	
	
	

}
