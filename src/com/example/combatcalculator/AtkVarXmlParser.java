package com.example.combatcalculator;

public class AtkVarXmlParser extends XmlParser{
	
	private static AtkVarXmlParser instance = null;
	
	public AtkVarXmlParser(){
	}

	public static AtkVarXmlParser getInstance() {
		
		if (instance == null)
			return createInstance();
		
		return instance;
	}
	
	private static AtkVarXmlParser createInstance() {
		
		instance = new AtkVarXmlParser();
		return instance;
	
	}

}
