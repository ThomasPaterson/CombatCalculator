package com.tree.combatcalculator.Dice;

/**
 * @(#)DiceReader.java
 *
 *Reads the xml file for the dice values stored, then constructs an attack calculator and returns it
 *
 *
 * @author
 * @version 1.00 2012/11/13
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.tree.combatcalculator.AttackCalculator;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Xml;

public class DiceReader {

	private static String startDiceCalc = "dicecalculator";

    public DiceReader() {
    }

	//stores all the data for the dice rolls and creates an attack calculator with it, then passes it back
    public AttackCalculator readFile(String path, Context context) throws XmlPullParserException, IOException{
			System.out.println("started reading");

			//for storing the arrays in a container
			DiceHolder diceBag = new DiceHolder();
			ArrayList<DiceCalculator> diceCalcs = readDiceXML(path, context);


			int diceCounter = 0;
			int addCounter = 0;

			//store the diceCalculators in a diceHolder
		    for (int i = 0; i < diceCalcs.size(); i++){

		    	diceBag.addDice(diceCounter, addCounter, diceCalcs.get(i));


		    	//move the index up
		    	if (addCounter == 2){
		    		addCounter = 0;
		    		diceCounter++;

		    	}else{
		    		addCounter++;
		    	}

		    }
		    
		    System.out.println("finished reading");


			return new AttackCalculator(diceBag);



		  }//end readFile


	//reads the xml file to get all the dice rolls
	private ArrayList<DiceCalculator> readDiceXML(String path, Context context) throws XmlPullParserException, IOException{

		ArrayList<Double> numbers = new ArrayList<Double>();
		ArrayList<Double> crits = new ArrayList<Double>();

		ArrayList<DiceCalculator> diceCalcs = new ArrayList<DiceCalculator>();

	    AssetManager am = context.getAssets();
		InputStream in = am.open(path);

		try {
			// First create a new parser
			XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			   
		      parser.setInput(in, null);
		      parser.nextTag();

		      while (parser.next() != XmlPullParser.END_TAG) {
		    	  
		    	  if (parser.getEventType() != XmlPullParser.START_TAG) {
		              continue;
		          }
		    	  
		    	  String name = parser.getName();
		    	  
		    	
		    	  //if we hit a dice calculator entry, read it and store it
		    	  if (name.equals(startDiceCalc)){
		    		  
		    		  numbers = new ArrayList<Double>();
			    	  crits = new ArrayList<Double>();
		    		  
		    		  ReadEntry(parser, numbers, crits);
		    	
			    	  DiceCalculator d = new DiceCalculator();
			          d.setValues(numbers, crits);
			          diceCalcs.add(d);
		          
		    	  }else {
		              skip(parser);
		          }
		          
		          

		      }//end while
		      


			} finally {
				in.close();
			}


		return diceCalcs;

	}
	
	
	/**
     * Reads all the entries for a dice calculator, puts them in the number and crit arrays
    */
	private void ReadEntry(XmlPullParser parser, ArrayList<Double> numbers, ArrayList<Double> crits) throws IOException, XmlPullParserException{
		
		while (parser.next() != XmlPullParser.END_TAG) {
			
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        // Starts by looking for the entry tag
	        if (name.equals("number")) {
	            numbers.add(readDouble(parser));
	        } else if (name.equals("crit")) {
	            crits.add(readDouble(parser));
	        }else {
	            skip(parser);
	        }
	        
		}
		
		
	}
	
	private Double readDouble(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return Double.parseDouble(result);
	}
	
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
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