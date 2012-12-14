package com.example.combatcalculator;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

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
		prevSet = intent.getParcelableArrayListExtra(PREV_CHOICE);
		
		if (prevSet == null)
			prevSet = new ArrayList<AtkVar>();
		
		System.out.println("number of variables: " + prevSet.size());
		
		for (AtkVar a : prevSet)
			System.out.println(a.getName());
		
		
		setContentView(R.layout.activity_options);
		
		
		
		setupSpinner(R.id.val_atk_spinner, R.array.add_value_array, false, 0, 5);
		setupSpinner(R.id.val_dam_spinner, R.array.add_value_array, false, 0, 5);
		setupSpinner(R.id.add_atk_spinner, R.array.add_dice_array, false, 0, 1);
		setupSpinner(R.id.add_dam_spinner, R.array.add_dice_array, false, 0, 1);
		setupSpinner(R.id.discard_atk_spinner, R.array.discard_dice_array, false, 0, 0);
		setupSpinner(R.id.discard_dam_spinner, R.array.discard_dice_array, false, 0, 0);
		
		if (typeChosen.equals(MainActivity.ATTACKER))
			fillAttackerPrevious();
		
		//TODO: make it fill in previous options selected
	}
	
	
	/**
     *Initializes all the variables from the array on the attackrer from last time
    */
	private void fillAttackerPrevious(){
		
		if (AtkVar.checkContainsName(prevSet, AtkVar.GUNFIGHTER))
			((CheckBox)findViewById(R.id.gunfighter_box)).setChecked(true);
		
		if (AtkVar.checkContainsName(prevSet, AtkVar.POINT_BLANK))
			((CheckBox)findViewById(R.id.point_blank_box)).setChecked(true);
		
		if (AtkVar.checkContainsName(prevSet, AtkVar.CMA))
			((CheckBox)findViewById(R.id.cma_box)).setChecked(true);
		
		if (AtkVar.checkContainsName(prevSet, AtkVar.CRA))
			((CheckBox)findViewById(R.id.cra_box)).setChecked(true);
		
		if (AtkVar.checkContainsName(prevSet, AtkVar.ASSAULT))
			((CheckBox)findViewById(R.id.assault_box)).setChecked(true);
		
		if (AtkVar.checkContainsName(prevSet, AtkVar.FREE_CHARGE))
			((CheckBox)findViewById(R.id.free_charge_box)).setChecked(true);
		
		if (AtkVar.checkContainsName(prevSet, AtkVar.REROLL_ATK))
			((CheckBox)findViewById(R.id.reroll_atk_box)).setChecked(true);
		
		if (AtkVar.checkContainsName(prevSet, AtkVar.REROLL_DAM))
			((CheckBox)findViewById(R.id.reroll_dam_box)).setChecked(true);
		
		if (AtkVar.checkContainsName(prevSet, AtkVar.BOOSTED_HIT))
			((CheckBox)findViewById(R.id.boosted_hit_box)).setChecked(true);
		
		if (AtkVar.checkContainsName(prevSet, AtkVar.BOOSTED_DAM))
			((CheckBox)findViewById(R.id.boosted_dam_box)).setChecked(true);
		
		//check spinner values, not boolean so check to see if there by null
		AtkVar modAtk = AtkVar.getAtkVarByName(prevSet, AtkVar.MOD_ATK);
		if (modAtk != null)
			((Spinner)findViewById(R.id.val_atk_spinner)).setSelection(modAtk.getValue()+5);
		
		AtkVar modDam = AtkVar.getAtkVarByName(prevSet, AtkVar.MOD_POW);
		if (modDam != null)
			((Spinner)findViewById(R.id.val_dam_spinner)).setSelection(modDam.getValue()+5);
		
		AtkVar modAtkDice = AtkVar.getAtkVarByName(prevSet, AtkVar.ADD_HIT);
		if (modAtkDice != null)
			((Spinner)findViewById(R.id.add_atk_spinner)).setSelection(modAtkDice.getValue()+1);
		
		AtkVar modDamDice = AtkVar.getAtkVarByName(prevSet, AtkVar.ADD_DAM);
		if (modDamDice != null)
			((Spinner)findViewById(R.id.add_dam_spinner)).setSelection(modDamDice.getValue()+1);
		
		AtkVar modAtkDiscard = AtkVar.getAtkVarByName(prevSet, AtkVar.DISCARD_ATK);
		if (modAtkDiscard != null)
			((Spinner)findViewById(R.id.discard_atk_spinner)).setSelection(modAtkDiscard.getValue());
		
		AtkVar modDamDiscard = AtkVar.getAtkVarByName(prevSet, AtkVar.DISCARD_DAM);
		if (modDamDiscard != null)
			((Spinner)findViewById(R.id.discard_dam_spinner)).setSelection(modDamDiscard.getValue());
			
		
		
	
		
		
		
	}
	
	
	/**
     *Takes all the filled in variables and returns them, for the attacker
    */
	private ArrayList<AtkVar> returnAttackerArray(){
		
		ArrayList<AtkVar> newList = new ArrayList<AtkVar>();
		
		if (((CheckBox)findViewById(R.id.gunfighter_box)).isChecked())
			newList.add(new AtkVar(AtkVar.GUNFIGHTER));
		
		if (((CheckBox)findViewById(R.id.point_blank_box)).isChecked())
			newList.add(new AtkVar(AtkVar.POINT_BLANK));
		
		if (((CheckBox)findViewById(R.id.cma_box)).isChecked())
			newList.add(new AtkVar(AtkVar.CMA));
		
		if (((CheckBox)findViewById(R.id.cra_box)).isChecked())
			newList.add(new AtkVar(AtkVar.CRA));
		
		if (((CheckBox)findViewById(R.id.assault_box)).isChecked())
			newList.add(new AtkVar(AtkVar.ASSAULT));
		
		if (((CheckBox)findViewById(R.id.free_charge_box)).isChecked())
			newList.add(new AtkVar(AtkVar.FREE_CHARGE));
		
		if (((CheckBox)findViewById(R.id.reroll_atk_box)).isChecked())
			newList.add(new AtkVar(AtkVar.REROLL_ATK));
		
		if (((CheckBox)findViewById(R.id.reroll_dam_box)).isChecked())
			newList.add(new AtkVar(AtkVar.REROLL_DAM));
		
		if (((CheckBox)findViewById(R.id.boosted_hit_box)).isChecked())
			newList.add(new AtkVar(AtkVar.BOOSTED_HIT));
		
		if (((CheckBox)findViewById(R.id.boosted_dam_box)).isChecked())
			newList.add(new AtkVar(AtkVar.BOOSTED_DAM));
		
		//if the value isn't at zero, record what it has been changed to
		int modAtk = Integer.parseInt(((Spinner)findViewById(R.id.val_atk_spinner)).getSelectedItem().toString());
		if (modAtk != 0)
			newList.add(new AtkVar(AtkVar.MOD_ATK, modAtk));
		
		//if the value isn't at zero, record what it has been changed to
		int modDam = Integer.parseInt(((Spinner)findViewById(R.id.val_dam_spinner)).getSelectedItem().toString());
		if (modDam != 0)
			newList.add(new AtkVar(AtkVar.MOD_POW, modDam));
		
		//if the value isn't at zero, record what it has been changed to
		int addAtk = Integer.parseInt(((Spinner)findViewById(R.id.add_atk_spinner)).getSelectedItem().toString());
		if (addAtk != 0)
			newList.add(new AtkVar(AtkVar.ADD_HIT, addAtk));
		
		
		//if the value isn't at zero, record what it has been changed to
		int addDam = Integer.parseInt(((Spinner)findViewById(R.id.add_dam_spinner)).getSelectedItem().toString());
		if (addDam != 0)
			newList.add(new AtkVar(AtkVar.ADD_DAM, addDam));
				
		//if the value isn't at zero, record what it has been changed to
		int disAtk = Integer.parseInt(((Spinner)findViewById(R.id.discard_atk_spinner)).getSelectedItem().toString());
		if (disAtk != 0)
			newList.add(new AtkVar(AtkVar.DISCARD_ATK, disAtk));
				
		//if the value isn't at zero, record what it has been changed to
		int disDam = Integer.parseInt(((Spinner)findViewById(R.id.discard_dam_spinner)).getSelectedItem().toString());
		if (disDam != 0)
			newList.add(new AtkVar(AtkVar.DISCARD_DAM, disDam));
		
			
		
		
		return newList;
		
	}
	
	
	/**
     *Initializes a spinner with the given array id and spinner id, sets it to default value if it 
     *isn't coming from a previous screen
    */
	private void setupSpinner(int entry, int array, boolean saved, int position, int def){
		
		Spinner spinner = (Spinner) findViewById(entry);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        array, android.R.layout.simple_dropdown_item_1line);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		if (saved)
			spinner.setSelection(position);
		else
			spinner.setSelection(def);
		
		
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
		
		ArrayList<AtkVar> newList = returnAttackerArray();
		
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
