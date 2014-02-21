package com.example.combatcalculator;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public abstract class XmlParser {
	
	public enum Type {
		ATTACK_PROPERTY,
		ATK_VAR;
	}

	
	public static XmlParser getXmlParser(Type type){
		
		switch (type) {
     		case ATTACK_PROPERTY:  	
     			return AttackPropertyXmlParser.getInstance();     			
     		case ATK_VAR:
     			return AtkVarXmlParser.getInstance();     			
     		default:
     			Log.e("INVALID", "No xml parsers of this type found");
     			return null;
     			
		}
     		
	}
	
	


	public <E> List<E> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        }catch (Exception  e){
        	Log.e("XML_ERROR", "Error parsing Xml",  e);
        } finally {
            in.close();
        }
        return null;
    }
	
	protected abstract <E> List<E> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException;
	   

	
	protected abstract <E> E readVariable(XmlPullParser parser) throws XmlPullParserException, IOException;
	

	protected String readEntry(XmlPullParser parser, String variableName) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, null, variableName);
	    String entry = readText(parser);
	    parser.require(XmlPullParser.END_TAG, null, variableName);
	    return entry;
	}

	
	protected String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return result;
	}


	
	protected void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
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
