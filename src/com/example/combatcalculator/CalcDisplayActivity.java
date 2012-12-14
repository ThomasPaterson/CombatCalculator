package com.example.combatcalculator;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.AttackCalculator;
import com.tree.combatcalculator.AttackModel;
import com.tree.combatcalculator.DefendModel;
import com.tree.combatcalculator.DiceReader;
import com.tree.combatcalculator.Node;
import com.tree.combatcalculator.TreeManager;

public class CalcDisplayActivity extends FragmentActivity implements
		ActionBar.TabListener {
	
	public final static String OPTIMAL_PATH = "com.example.myfirstapp.OPTIMAL_PATH";
	public final static String PATH_STATE = "com.example.myfirstapp.PATH_STATE";
	public final static String OVERVIEW = "com.example.myfirstapp.OVERVIEW";
	public final static String ATTACKS_OVERVIEW = "com.example.myfirstapp.ATTACKS_OVERVIEW";
	public final static String FRAG_TYPE = "com.example.myfirstapp.FRAG_TYPE";
	
	AttackModel attacker;
	DefendModel defender;
	ArrayList<AtkVar> situation;
	int focus;
	int optimization;
	ArrayList<Node> optimalPath;
	boolean pathSet;
	String overview;
	String attacksOverview;


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
	    		overview = savedInstanceState.getString(OVERVIEW);
	    		attacksOverview = savedInstanceState.getString(ATTACKS_OVERVIEW);
	    		
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
		
		TextView attackerText = (TextView) findViewById(R.id.attacker_name);
		TextView defenderText = (TextView) findViewById(R.id.defender_name);
		TextView focusText = (TextView) findViewById(R.id.focus_amount);
		
		attackerText.setText("Attacker: " + attacker.getName());
		defenderText.setText("Defender: " + defender.getName());
		
		if (focus > 0)
			focusText.setText("Focus Used: " + focus);
		else
			focusText.setText("");
		
		
	}
	
	
	/**
	 * sets the variables calculated by the background thread, passes it to the current fragment as well
	 */
	private void setResults(String overall, String specific){
		
		overview = overall;
		attacksOverview = specific;
		
		pathSet = true;
		
		DisplayFragment details = (DisplayFragment)
                getSupportFragmentManager().findFragmentById(R.id.container);
		
		details.setResults(overall, specific);
		
		
		//TextView helloText = (TextView) findViewById(R.id.hello_world);
		//helloText.setText(attacksOverview);
		
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
	    
	    //if the path has been found, save the strings representing data as well
	    if (pathSet){
	    	//savedInstanceState.putParcelableArrayList(OPTIMAL_PATH, optimalPath);
	    	outState.putString(OVERVIEW, overview);
	    	outState.putString(ATTACKS_OVERVIEW, attacksOverview);
	    	
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_calc_display, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, show the tab contents in the
		// container view.
		Fragment fragment = new DisplayFragment();
		Bundle args = new Bundle();

		if (pathSet){
			args.putString(OVERVIEW, overview);
			args.putString(ATTACKS_OVERVIEW, attacksOverview);
		}
		
		args.putInt(FRAG_TYPE, tab.getPosition());
		args.putBoolean(PATH_STATE, pathSet);
		
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		
		DisplayFragment details = (DisplayFragment)
                getSupportFragmentManager().findFragmentById(R.id.container);
		
		details.displayResults();

				
	}


	
	
	/**
	 * Calculates and returns the best decisions a user should make, given the input of the previous screen
	 */
	private class SearchTreeTask extends AsyncTask<AttackHolder, Void, String[]> {
		

		@Override
		protected String[] doInBackground(AttackHolder... holders) {
			System.out.println("starting asynch");
			AttackHolder holder = holders[0];
			System.out.println(holder.situation.toString());
			ArrayList<Node> bestPath = null;
			String[] resultStrings = {"error", "error"};
			
			try {
				
			TreeManager tree = new TreeManager( holder.atkCalc, holder.optimization, holder.attacker,
					holder.defender, new ArrayList<AtkVar>(holder.situation));
			
			
			System.out.println("starting tree");
			
				bestPath = tree.makeTree(holder.focus);
				resultStrings = tree.printResults(bestPath, holder.focus);
				
	        } catch (Exception e) {
	            System.out.println(e.getMessage());
	        } 
			
			
			
			System.out.println("finished tree");
			
	        return resultStrings;
	    }
		
		@Override
		protected void onPostExecute(String[] result) {
			System.out.println("setting results");
			
	        setResults(result[1], result[0]);
	        System.out.println("finished results");

	    }

		

	}
	
	

}
