package com.example.combatcalculator;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.AttackCalculator;
import com.tree.combatcalculator.AttackModel;
import com.tree.combatcalculator.DefendModel;
import com.tree.combatcalculator.Weapon;

public class MainActivity extends FragmentActivity {
	public final static String ATTACKER = "com.example.myfirstapp.ATTACKER";
	public final static String DEFENDER = "com.example.myfirstapp.DEFENDER";
	public final static String WEAPONS = "com.example.myfirstapp.WEAPONS";
	public final static String SITUATION = "com.example.myfirstapp.SITUATION";
	public final static String FOCUS = "com.example.myfirstapp.FOCUS";
	public final static String ATTACKER_VARS = "com.example.myfirstapp.ATTACKER_VARS";
	public final static String DEFENDER_VARS = "com.example.myfirstapp.DEFENDER_VARS";
	public final static String WEAPONS_VARS = "com.example.myfirstapp.WEAPONS_VARS";
	public final static String SITUATION_VARS = "com.example.myfirstapp.SITUATION_VARS";
	public final static String ATK_CALC = "com.example.myfirstapp.ATK_CALC";
	public final static String OPTIMIZATION = "com.example.myfirstapp.OPTIMIZATION";
	
	//codes for starting activities for their advanced options screens
	public final static int ATTACKER_REQ = 1;
	public final static int DEFENDER_REQ = 2;
	public final static int WEAPON_REQ = 3;
	public final static int SITUATION_REQ = 4;
	
	//initialized at the beginning of the program because it has to read xml to initialize
	private AttackCalculator atkCalc;
	
	//variable lists for the atkvar users
	private ArrayList<AtkVar> situation;
	private ArrayList<AtkVar> attackerVars;
	private ArrayList<AtkVar> defenderVars;
	private ArrayList<ArrayList<AtkVar>> weaponsVars;
	
	private ViewGroup mWeaponGroup;


	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		mWeaponGroup = (ViewGroup) findViewById(R.id.container);
		
		System.out.println("children number: " + mWeaponGroup.getChildCount());
		
		 // Check whether we're recreating a previously destroyed instance
	    if (savedInstanceState != null) {
	        // Restore value of members from saved state
	    	situation = savedInstanceState.getParcelableArrayList(SITUATION_VARS);
	    	attackerVars = savedInstanceState.getParcelableArrayList(ATTACKER_VARS);
	    	defenderVars = savedInstanceState.getParcelableArrayList(DEFENDER_VARS);
	    	weaponsVars = (ArrayList<ArrayList<AtkVar>>) savedInstanceState.getSerializable(WEAPONS_VARS);
	    	atkCalc = savedInstanceState.getParcelable(ATK_CALC);
	    	ArrayList<Weapon> weapons = savedInstanceState.getParcelableArrayList(WEAPONS);
	    	
	    	if (weapons.size() > mWeaponGroup.getChildCount()){
	    		mWeaponGroup.removeAllViews();
	    		initWeapons(weapons);
	    	}
	    	
	    	
	    } else {
	        //otherwise create new ones when a new activity is created
	    	mWeaponGroup.removeAllViews();
	    	situation = new ArrayList<AtkVar>();
	    	attackerVars = new ArrayList<AtkVar>();
	    	defenderVars = new ArrayList<AtkVar>();
	    	weaponsVars = new ArrayList<ArrayList<AtkVar>>();
	    	
	    	
	    	
	    	
	    	
	    }
		
		

	}
	
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    // Save the user's current state
	    savedInstanceState.putParcelableArrayList(SITUATION_VARS, situation);
	    savedInstanceState.putParcelableArrayList(ATTACKER_VARS, attackerVars);
	    savedInstanceState.putParcelableArrayList(DEFENDER_VARS, defenderVars);
	    savedInstanceState.putParcelableArrayList(WEAPONS, getWeapons(mWeaponGroup));
	    savedInstanceState.putSerializable(WEAPONS_VARS, weaponsVars);
	    
	    
	    // Always call the superclass so it can save the view hierarchy state
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	public void onDestroy() {
		
		//mWeaponGroup.removeAllViews();
		
        super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	
	
	/**
     * Creates the various objects from the fields that were filled
     * and sends them to the CalcDisplayActivity for processing and display
    */
	public void sendToCalc(View view){
		
		String errorMessage = checkValidity();
		
		if (errorMessage == null){
		
			Intent intent = new Intent(this, CalcDisplayActivity.class);
			
			int focus = prepAttacker(intent);
			prepDefender(intent);
			prepSituation(intent);
			//prepWeapons(intent);
			
			intent.putExtra(OPTIMIZATION, 0);
			intent.putExtra(FOCUS, focus);
			
		    startActivity(intent);
	    
		}else{
			
			DialogFragment newFragment = new ValidFragment();
			Bundle args = new Bundle();

			args.putString(ValidFragment.TYPE_ERROR, errorMessage);

			newFragment.setArguments(args);
			
			newFragment.show(getSupportFragmentManager(), "validity");

		}
		
	}
	
	
	/**
     * Checks to make sure all the variables needed for the calculator to work are present, returns null if they
     * are, otherwise returns a string with the correct message to tell the user what is wrong
    */
	private String checkValidity(){
		
		if (mWeaponGroup.getChildCount() == 0)
			return "Need at least one weapon";
		
		
		return null;
	}
	
	/**
     * Creates the various objects from the fields that were filled
     * and sends them to the CalcDisplayActivity for processing and display
    */
	public void sendToAttackerOptions(View view){
		
		Intent intent = new Intent(this, OptionsActivity.class);
		
		intent.putExtra(OptionsActivity.PREV_CHOICE, attackerVars);
		intent.putExtra(OptionsActivity.TYPE_CHOSEN, ATTACKER);
		
	    startActivityForResult(intent, ATTACKER_REQ);
		
	}
	

	@Override
    protected void onActivityResult(int pRequestCode, int resultCode, Intent data){
	
		if (resultCode == -1)
			finish();
		else if (resultCode == ATTACKER_REQ)
			attackerVars = data.getParcelableArrayListExtra(OptionsActivity.CHANGED);
			
		
			
	}

	
	/**
     * Sets up the correct variables for the attacker, based on entries
    */
	private int prepAttacker(Intent intent){
		
		int focus = 0;
		
		AttackModel attacker = new AttackModel(0, 0);
		
		EditText matText = (EditText) findViewById(R.id.mat_entry);
	    EditText ratText = (EditText) findViewById(R.id.rat_entry);
	    EditText focusText = (EditText) findViewById(R.id.focus_entry);
	    //EditText numAtkText = (EditText) findViewById(R.id.num_atk_entry);

	    
	    if (matText.getText().toString() != "")
	    	attacker.setMat(Integer.parseInt(matText.getText().toString()));
	    
	    if (ratText.getText().toString() != "")
	    	attacker.setRat(Integer.parseInt(ratText.getText().toString()));
	    
	    if (ratText.getText().toString() != "")
	    	focus = Integer.parseInt(focusText.getText().toString());
	    
	    //if (numAtkText.getText().toString() != "")
	    //	attacker.setFocus(Integer.parseInt(numAtkText.getText().toString()));
	    
	    attacker.setVariables(attackerVars);
	    
	    prepWeapons(attacker);
	    
	    intent.putExtra(ATTACKER, attacker);
	    
	    return focus;
	    
	}
	
	
	/**
     * Sets up the correct variables for the defender, based on entries
    */
	private void prepDefender(Intent intent){
		
		
		DefendModel defender = new DefendModel(0,0);
		
		EditText defText = (EditText) findViewById(R.id.def_entry);
	    EditText ArmText = (EditText) findViewById(R.id.arm_entry);
	    EditText healthText = (EditText) findViewById(R.id.health_entry);

	    
	    if (defText.getText().toString() != "")
	    	defender.setDef(Integer.parseInt(defText.getText().toString()));
	    
	    if (ArmText.getText().toString() != "")
	    	defender.setArm(Integer.parseInt(ArmText.getText().toString()));
	    
	    if (healthText.getText().toString() != "")
	    	defender.setHealth(Integer.parseInt(healthText.getText().toString()));
	    
	    intent.putExtra(DEFENDER, defender);
	    
		
	}
	
	/**
     * Sets up the correct variables for the situation, based on entries
     * if it is charging, add it, since it will not have been added prior to this
    */
	private void prepSituation(Intent intent){
		
		situation = new ArrayList<AtkVar>();
		
		CheckBox checkCharging = (CheckBox) findViewById(R.id.charging_entry);
		
		if (checkCharging.isChecked()){
			situation.add(new AtkVar(AtkVar.CHARGING));
			situation.add(new AtkVar(AtkVar.CHARGE_DAMAGE));
			//AtkVar.removeVariable(situation, AtkVar.CHARGING);
		}
		
		intent.putParcelableArrayListExtra(SITUATION, situation);
		
	}
	
	private void setSituationVars(ArrayList<AtkVar> newVars){ 
		situation = new ArrayList<AtkVar>(newVars);
	}
	
	/**
     * Gathers all the weapon variables from the various viewgroups and converts it into an arraylist for the attacker
    */
	private void prepWeapons(AttackModel attacker){
		
		ArrayList<Weapon> weapons = new ArrayList<Weapon>();
		
		//loop through entire group of weapons and add each one
		for (int i = 0; i < mWeaponGroup.getChildCount(); i++){
		
			ViewGroup curView = (ViewGroup) mWeaponGroup.getChildAt(i);
			
			ArrayList<AtkVar> weaponVars = new ArrayList<AtkVar>();
			
			CheckBox checkRanged = (CheckBox) curView.findViewById(R.id.ranged_entry);
			EditText rofText = (EditText) curView.findViewById(R.id.rof_entry);
		    EditText powText = (EditText) curView.findViewById(R.id.pow_entry);
			
			Weapon w = new Weapon(Integer.parseInt(powText.getText().toString()),
								  checkRanged.isChecked(),
								  weaponVars);
		    
		   weapons.add(w);
		    
		}
		
		attacker.setWeapons(weapons);
		
	}
	
	
	/**
     * Gets all the weapons from the given viewgroup, or null if there aren't any
    */
	private ArrayList<Weapon> getWeapons(ViewGroup weaponViews){
		
		ArrayList<Weapon> weapons = new ArrayList<Weapon>();
		
		//loop through entire group of weapons and add each one
		for (int i = 0; i < mWeaponGroup.getChildCount(); i++){
		
			ViewGroup curView = (ViewGroup) mWeaponGroup.getChildAt(i);
			
			ArrayList<AtkVar> weaponVars = new ArrayList<AtkVar>();
			
			CheckBox checkRanged = (CheckBox) curView.findViewById(R.id.ranged_entry);
			EditText rofText = (EditText) curView.findViewById(R.id.rof_entry);
		    EditText powText = (EditText) curView.findViewById(R.id.pow_entry);
		    
		    System.out.println(powText.getText().toString());
			
			Weapon w = new Weapon(Integer.parseInt(powText.getText().toString()),
								  checkRanged.isChecked(),
								  weaponVars);
		    
		   weapons.add(w);
		    
		}
		
		return weapons;
		
		
	}
	
	/**
     * Adds a weapon list item to the view group for the weapons
    */
	public void addWeapons(View view) {
        // Instantiate a new "row" view.
        final ViewGroup newView = (ViewGroup) LayoutInflater.from(this).inflate(
                R.layout.weapon_item, mWeaponGroup, false);


        // Set a click listener for the "X" button in the row that will remove the row.
        newView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
           
        	@Override
            public void onClick(View view) {
                // Remove the row from its parent (the container view).
                // Because mContainerView has android:animateLayoutChanges set to true,
                // this removal is automatically animated.
            	mWeaponGroup.removeView(newView);

                // If there are no rows remaining, show the empty view.
                //if (mWeaponGroup.getChildCount() == 0) {
                    //findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
                //}
            }
        });

        // Because mContainerView has android:animateLayoutChanges set to true,
        // adding this view is automatically animated.
        mWeaponGroup.addView(newView, 0);
    }
	
	/**
     * Adds the weapons to the viewgroup for the weapons
    */
	private void initWeapons(ArrayList<Weapon> weapons){
		
		
		System.out.println("number of weapons groups: " + mWeaponGroup.getChildCount());
		
		for (int i = weapons.size()-1; i >= 0; i--){
			
			addWeapons(findViewById(R.id.Button20));
			
			ViewGroup curView = (ViewGroup) mWeaponGroup.getChildAt(0);
			
			
			CheckBox checkRanged = (CheckBox) curView.findViewById(R.id.ranged_entry);
			checkRanged.setChecked(weapons.get(i).getRanged());
			EditText rofText = (EditText) curView.findViewById(R.id.rof_entry);
			rofText.setText(Integer.toString(weapons.get(i).getROF()));
			System.out.println("here " + weapons.get(i).getPow());
		    EditText powText = (EditText) curView.findViewById(R.id.pow_entry);
			powText.setText(Integer.toString(weapons.get(i).getPow()));
				
			
		}
		
		
	}
	

}