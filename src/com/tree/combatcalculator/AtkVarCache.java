package com.tree.combatcalculator;

import java.io.InputStream;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;

import com.example.combatcalculator.AttackPropertyXmlParser;
import com.example.combatcalculator.ConfigManager;

public class AtkVarCache {
	
	private static Map<AtkVar.Id, AtkVar> atkVarCache = null;
		
	public static void initialize(Context context) {
		if (atkVarCache == null){
			try {
				String file = ConfigManager.SITUATION_XML;
				InputStream istr = context.getAssets().open(file);
				AttackPropertyXmlParser xmlReader = AttackPropertyXmlParser.getInstance();
				atkVarCache = (Map<AtkVar.Id, AtkVar>)xmlReader.parse(istr);
			} catch (XmlPullParserException e) {
				Log.e("parser", "Problem parsing xml", e);
			} catch (Exception e) {
				Log.e("other", "Problem parsing xml", e);
			}
		}
		
	}
	
	public static Map<AtkVar.Id, AtkVar> getInstance() {
		if (atkVarCache != null)
			return atkVarCache;
		else
			throw new IllegalStateException();
	}

}
