package com.example.combatcalculator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class AttackPropertyXmlParser extends XmlParser {
	
	private static AttackPropertyXmlParser instance = null;
	
	public AttackPropertyXmlParser(){
	}

	public static AttackPropertyXmlParser getInstance() {
		
		if (instance == null)
			return createInstance();
		
		return instance;
	}
	
	private static AttackPropertyXmlParser createInstance() {
		
		instance = new AttackPropertyXmlParser();
		return instance;
	
	}
	
	

	@Override
	protected List<AttackProperty> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
	    List<AttackProperty> entries = new ArrayList<AttackProperty>();

	    parser.require(XmlPullParser.START_TAG, null, "Options");
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        // Starts by looking for the entry tag
	        if (name.equals("Variable")) {
	            entries.add(readVariable(parser));
	        } else {
	            skip(parser);
	        }
	    }  
	    return entries;
	}
	
	protected AttackProperty readVariable(XmlPullParser parser) throws XmlPullParserException, IOException {
	    parser.require(XmlPullParser.START_TAG, null, "Variable");
	    String Name = null;
	    String Display = null;
	    String Type = null;
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        
	        String name = parser.getName();

	        if (name.equals("Name")) {
	        	Name = readEntry(parser, "Name");
	        } else if (name.equals("Display")) {
	        	Display = readEntry(parser, "Display");
	        } else if (name.equals("Type")) {
	        	Type = readEntry(parser, "Type");
	        } else {
	            skip(parser);
	        }

	    }
	    
	    return new AttackProperty(Name, Display, Type);
	}
		

}
