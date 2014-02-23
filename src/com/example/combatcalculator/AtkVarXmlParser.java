package com.example.combatcalculator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.tree.combatcalculator.AtkVarCopy;
import com.tree.combatcalculator.AtkVarCopy.VariableType;

public class AtkVarXmlParser extends XmlParser{
	
	private static AtkVarXmlParser instance = null;

	private static final String ID = "Id";
	private static final String STATE = "State";
	private static final String VALUE_TYPE = "ValueType";
	private static final String  CONDITIONAL = "Conditional";
	private static final String TRIGGERED = "Triggered";
	private static final String CONTINUOUS = "Continuous";
	private static final String ATK_VAR_START = "AtkVar";
	private static final String ATK_VARS_START = "AtkVars";
	
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

	@Override
	protected  List<AtkVarCopy> readFeed(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		
		List<AtkVarCopy> entries = new ArrayList<AtkVarCopy>();

		parser.require(XmlPullParser.START_TAG, null, ATK_VARS_START);
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String tag = parser.getName();
	        // Starts by looking for the entry tag
	        if (tag.equals(ATK_VAR_START)) {
	            entries.add(readVariable(parser));
	        } else {
	            skip(parser);
	        }
	    }  
	    return entries;
	}

	@Override
	protected  AtkVarCopy readVariable(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		
	    parser.require(XmlPullParser.START_TAG, null, ATK_VAR_START);
	    AtkVarCopy atkVar = new AtkVarCopy();
	    List<AtkVarCopy.VariableType> variableTypes = Arrays.asList(VariableType.values());
	    
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        
	        String tag = parser.getName();
	        
	        boolean noMatch = true;
	        
	        for (AtkVarCopy.VariableType type : variableTypes){
	        	
	        	if(type.toString().equals(tag)){
	        		atkVar.addVariable(type, readEntry(parser, tag));
	        		noMatch = false;
	        		break;
	        	}	
	        }
	        
	        if (noMatch)
	            skip(parser);
	       

	    }
	    
	    return atkVar;
	}

}
