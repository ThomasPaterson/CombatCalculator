package com.example.combatcalculator;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.AttackCalculator;
import com.tree.combatcalculator.AttackModel;
import com.tree.combatcalculator.DefendModel;
import com.tree.combatcalculator.DiceReader;
import com.tree.combatcalculator.Node;
import com.tree.combatcalculator.TreeManager;

public class DisplayCalcActivity extends Activity {
	
	public final static String OPTIMAL_PATH = "com.example.myfirstapp.OPTIMAL_PATH";
	public final static String PATH_STATE = "com.example.myfirstapp.PATH_STATE";
	public final static String OVERVIEW = "com.example.myfirstapp.OVERVIEW";
	public final static String ATTACKS_OVERVIEW = "com.example.myfirstapp.ATTACKS_OVERVIEW";
	
	AttackModel attacker;
	DefendModel defender;
	ArrayList<AtkVar> situation;
	int focus;
	int optimization;
	ArrayList<Node> optimalPath;
	boolean pathSet;
	String overview;
	String attacksOverview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		
		
		attacker = intent.getParcelableExtra(MainActivity.ATTACKER);
		defender = intent.getParcelableExtra(MainActivity.DEFENDER);
		situation = intent.getParcelableArrayListExtra(MainActivity.SITUATION);
		focus = intent.getIntExtra(MainActivity.FOCUS, 0);
		optimization = intent.getIntExtra(MainActivity.OPTIMIZATION, 0);
		
		setContentView(R.layout.activity_display_calc);
		
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
	    
	    if (pathSet)
	    	setResults(overview, attacksOverview);
		
    	
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    // Save the user's current state
	    savedInstanceState.putParcelableArrayList(MainActivity.SITUATION_VARS, situation);
	    savedInstanceState.putParcelable(MainActivity.ATTACKER, attacker);
	    savedInstanceState.putParcelable(MainActivity.DEFENDER, defender);
	    savedInstanceState.putInt(MainActivity.FOCUS, focus);
	    savedInstanceState.putInt(MainActivity.OPTIMIZATION, optimization);
	    
	    //if the path has been found, save the path as well
	    if (pathSet){
	    	//savedInstanceState.putParcelableArrayList(OPTIMAL_PATH, optimalPath);
	 	    savedInstanceState.putBoolean(PATH_STATE, pathSet);
	 	    savedInstanceState.putString(OVERVIEW, overview);
	 	    savedInstanceState.putString(ATTACKS_OVERVIEW, attacksOverview);
	    	
	    }
	   
	    
	    // Always call the superclass so it can save the view hierarchy state
	    super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_display_calc, menu);
		return true;
	}
	
	/**
	 * Sets the new tree path, and changes the view to display the results
	 * Also sets the variable to let the activity now it has a result now
	 */
	private void setResults(String overall, String specific){
		
		overview = overall;
		attacksOverview = specific;
		
		pathSet = true;
		
		TextView helloText = (TextView) findViewById(R.id.hello_world);
		helloText.setText(attacksOverview);
		
	}
	
	
	
	
	/**
	 * Calculates and returns the best decisions a user should make, given the input of the previous screen
	 */
	private class SearchTreeTask extends AsyncTask<AttackHolder, Void, String[] >{
		

		@Override
		protected String[] doInBackground(AttackHolder... holders) {
			System.out.println("starting asynch");
			AttackHolder holder = holders[0];
			System.out.println(holder.situation.toString());
			ArrayList<Node> bestPath = null;
			String[] resultStrings = null;
			
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
