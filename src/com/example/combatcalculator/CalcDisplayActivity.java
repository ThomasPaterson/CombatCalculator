package com.example.combatcalculator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.AttackCalculator;
import com.tree.combatcalculator.AttackModel;
import com.tree.combatcalculator.DetailedAttackResult;
import com.tree.combatcalculator.DefendModel;
import com.tree.combatcalculator.DiceReader;
import com.tree.combatcalculator.Node;
import com.tree.combatcalculator.TreeManager;

public class CalcDisplayActivity extends FragmentActivity implements
		ActionBar.TabListener {
	
	public final static String OPTIMAL_PATH = "com.example.myfirstapp.OPTIMAL_PATH";
	public final static String PATH_STATE = "com.example.myfirstapp.PATH_STATE";
	public final static String RESULTS = "com.example.myfirstapp.RESULTS";
	public final static String FRAG_TYPE = "com.example.myfirstapp.FRAG_TYPE";
	
	AttackModel attacker;
	DefendModel defender;
	ArrayList<AtkVar> situation;
	int focus;
	int optimization;
	ArrayList<Node> optimalPath;
	boolean pathSet;
	List<DetailedAttackResult> attackResults;
	private boolean critsOn = true;
	
	float totalNumHit;
	float totalExpDam;
	float totalExpDamAllHit;
	float numberOfCrits;


	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current tab position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		
		
		attacker = intent.getParcelableExtra(MainActivity.ATTACKER);
		defender = intent.getParcelableExtra(MainActivity.DEFENDER);
		situation = intent.getParcelableArrayListExtra(MainActivity.SITUATION);
		focus = intent.getIntExtra(MainActivity.FOCUS, 0);
		optimization = intent.getIntExtra(MainActivity.OPTIMIZATION, 0);
		
		setContentView(R.layout.activity_calc_display);
		
		// Check whether we're recreating a previously destroyed instance
	    if (savedInstanceState != null) {
	        // Restore value of members from saved state
	    	pathSet = savedInstanceState.getBoolean(PATH_STATE);
	    	
	    	if (pathSet){
	    		//optimalPath = savedInstanceState.getParcelableArrayList(OPTIMAL_PATH);
	    		attackResults = (List<DetailedAttackResult>) savedInstanceState.get(RESULTS);
	    		
	    	}

	    	
	    	
	    	
	    } else {
	        //otherwise create new ones when a new activity is created
	    	
	    	AttackCalculator atkCalc = null;
	    	pathSet = false;
	    	
	    	//TODO: may need to move onto a separate thread for slower phones
			DiceReader diceReader = new DiceReader();
	    	try {
	    		System.out.println("reading file");
	    		atkCalc = diceReader.readFile("config2.xml", getBaseContext());
	    		System.out.println("starting to make holder, focus is :" + focus);
	    		AttackHolder holder = new AttackHolder(attacker, defender, situation, focus, atkCalc, optimization);
	    		System.out.println("starting tree search");
	        	new SearchTreeTask().execute(holder);
	        } catch (Exception e) {
	            System.out.println(e.getMessage());
	        } 

	    	
	    	
	    }
	    
	    
	    
		
		

		// Set up the action bar to show tabs.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// For each of the sections in the app, add a tab to the action bar.
		actionBar.addTab(actionBar.newTab().setText("Overview")
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("Attack Chain")
				.setTabListener(this));
		
		setAttackOverview();

	}
	
	/**
	 * Puts down the data for the general information on the attack
	 */
	private void setAttackOverview(){
		//TODO: add attacker and defender names
		//TextView attackerText = (TextView) findViewById(R.id.attacker_name);
		//TextView defenderText = (TextView) findViewById(R.id.defender_name);
		TextView focusText = (TextView) findViewById(R.id.focus_amount);
		
		//attackerText.setText("Attacker: " + attacker.getName());
		//defenderText.setText("Defender: " + defender.getName());
		
		if (focus > 0)
			focusText.setText("Focus Used: " + focus);
		else
			focusText.setText("");
		
		
	}
	
	
	
private void setResults(List<DetailedAttackResult> attackResults){
		
		this.attackResults = attackResults;
		
		pathSet = true;
		
		DisplayFragment details = (DisplayFragment)
                getSupportFragmentManager().findFragmentById(R.id.container);
		
		
		setDetailsFragmentData(details);
		
		details.displayResults(critsOn);

		
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
		
		// Save the user's current state
		outState.putParcelableArrayList(MainActivity.SITUATION_VARS, situation);
		outState.putParcelable(MainActivity.ATTACKER, attacker);
		outState.putParcelable(MainActivity.DEFENDER, defender);
		outState.putInt(MainActivity.FOCUS, focus);
		outState.putInt(MainActivity.OPTIMIZATION, optimization);
		outState.putBoolean(PATH_STATE, pathSet);
	    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_calc_display, menu);
		return true;
	}
	
	
	private void setDetailsFragmentData(DisplayFragment details) {
		
		details.setData(
				getActionBar().getSelectedNavigationIndex(),
				pathSet,
				attackResults
				);
		
		
	}
	

	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, show the tab contents in the
		// container view.
		
		DisplayFragment fragment = DisplayFragment.createFragment(tab.getPosition());
		Bundle args = new Bundle();

		if (pathSet){
			args.putSerializable(RESULTS, (Serializable) attackResults);
		}
		
		args.putInt(FRAG_TYPE, tab.getPosition());
		args.putBoolean(PATH_STATE, pathSet);
		
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
		
		fragment.displayResults(critsOn);
		
		
	}

	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		
		DisplayFragment details = (DisplayFragment)
                getSupportFragmentManager().findFragmentById(R.id.container);
		
		details.displayResults(critsOn);

				
	}
	
	public void toggleCrit(View view){
		
		critsOn = critsOn == true ? false : true;
		
		if (critsOn)
			((Button) view).setText(R.string.crit_on);
		else
			((Button) view).setText(R.string.crit_off);
		
		DisplayFragment details = (DisplayFragment)
                getSupportFragmentManager().findFragmentById(R.id.container);
		
		details.displayResults(critsOn);
		
		
	}


	
	
	/**
	 * Calculates and returns the best decisions a user should make, given the input of the previous screen
	 */
	private class SearchTreeTask extends AsyncTask<AttackHolder, Void, List<DetailedAttackResult>> {
		

		@Override
		protected List<DetailedAttackResult> doInBackground(AttackHolder... holders) {
			System.out.println("starting asynch");
			AttackHolder holder = holders[0];
			System.out.println(holder.situation.toString());
			ArrayList<Node> bestPath = null;
			ArrayList<DetailedAttackResult> attackResults = null;
			
			try {
				
			TreeManager tree = new TreeManager( holder.atkCalc, holder.optimization, holder.attacker,
					holder.defender, new ArrayList<AtkVar>(holder.situation));
			
			
			System.out.println("starting tree");
			
				bestPath = tree.makeTree(holder.focus);
				attackResults = tree.setAttackResults(bestPath, holder.focus);
				
	        } catch (Exception e) {
	            System.out.println(e.getMessage());
	        } 
			
			
			
			System.out.println("finished tree");
			
	        return attackResults;
	    }
		
		@Override
		protected void onPostExecute(List<DetailedAttackResult> results) {
			System.out.println("setting results");
			
	        setResults(results);
	        System.out.println("finished results");

	    }

		

	}
	
	

}
