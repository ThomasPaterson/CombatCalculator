package com.example.combatcalculator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class AttackPropertyXmlParser {
	
	
	public static List<AttackProperty> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }
	
	private static List<AttackProperty> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
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
	
	private static AttackProperty readVariable(XmlPullParser parser) throws XmlPullParserException, IOException {
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
	

	private static String readEntry(XmlPullParser parser, String variableName) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, null, variableName);
	    String entry = readText(parser);
	    parser.require(XmlPullParser.END_TAG, null, variableName);
	    return entry;
	}

	
	private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return result;
	}


	
	private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }

	
	

}
