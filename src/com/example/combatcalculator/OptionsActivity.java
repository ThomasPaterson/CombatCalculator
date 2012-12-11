package com.example.combatcalculator;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.tree.combatcalculator.AtkVar;

public class OptionsActivity extends Activity {
	
	private String typeChosen;
	private int numChosen = -1;
	private ArrayList<AtkVar> prevSet;
	public final static String TYPE_CHOSEN = "com.example.myfirstapp.TYPE_CHOSEN";
	public final static String NUM_CHOSEN = "com.example.myfirstapp.NUM_CHOSEN";
	public final static String PREV_CHOICE = "com.example.myfirstapp.PREV_CHOICE";
	public final static String CHANGED = "com.example.myfirstapp.CHANGED";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		
		//gets type, can be an attacker, defender, weapon, situation
		typeChosen = intent.getStringExtra(TYPE_CHOSEN);
		
		//if it is a weapon, set the correct weapon
		if (typeChosen.equals(MainActivity.WEAPONS))
			numChosen = intent.getIntExtra(NUM_CHOSEN, 0);
		
		//get the previously set options
		prevSet = intent.getParcelableExtra(PREV_CHOICE);
		
		if (prevSet == null)
			prevSet = new ArrayList<AtkVar>();
		
		
		
		setContentView(R.layout.activity_options);
		
		//TODO: make it fill in previous options selected
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_options, menu);
		return true;
	}
	
	

	/**
     * Creates an arraylist of atkvars for the options selected, and sends that plus the
     * type of atkvar user that requested it
    */
	public void returnList(View view){
		
		Intent intent = new Intent(this, MainActivity.class);
		
		ArrayList<AtkVar> newList = readOptions();
		
		intent.putParcelableArrayListExtra(CHANGED, newList);
		intent.putExtra(TYPE_CHOSEN, typeChosen);
		
		if (numChosen != -1)
			intent.putExtra(NUM_CHOSEN, numChosen);
		
		setResult(MainActivity.ATTACKER_REQ, intent);
		finish();

	    
	}
	
	
	/**
     * If canceled, returns nothing
    */
	public void returnEmptyList(View view){
		
		setResult(0);
		finish();

	    
		
	}
	
	/**
     * Reads what has been filled in and returns it as an arraylist of atkvars
    */
	private ArrayList<AtkVar> readOptions(){
		
		ArrayList<AtkVar> holder = new ArrayList<AtkVar>();
		holder.add(new AtkVar(AtkVar.FREE_CHARGE));

		
		return holder;
		
	}
	
	
	public void onBackPressed(){
	// Return to main and close activity.
	setResult(-1);
	finish();
	return ;
	}
	

}
