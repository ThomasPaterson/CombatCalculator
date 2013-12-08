package com.example.combatcalculator;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tree.combatcalculator.AttackResult;

/**
 * A fragment for displaying info in the tabs of calc display
 */

public abstract class DisplayFragment extends Fragment {

	public static final String ARG_SECTION_NUMBER = "section_number";
	public static final int ALL = 0;
	public static final int SPECIFIC = 1;
	protected boolean finished = false;
	protected String allAttacks = "blank";
	protected String seperateAttacks = "blank";
	protected View displayView;
	protected List<AttackResult> results;
	protected int type = 0;
	protected boolean critsOn = true;
	
	public DisplayFragment() {
		
	}


	
	 @Override
	    public void onStart() {
	        super.onStart();

	        System.out.println("on start");
	        checkParent();
	        
	        displayResults(critsOn);
	    }
	 
	 private void checkParent(){
		 System.out.println("checking Parents");
		 
		 Bundle args = getArguments();
	        if (args != null) {
	        	finished = args.getBoolean(CalcDisplayActivity.PATH_STATE);
	        	type = args.getInt(CalcDisplayActivity.FRAG_TYPE);
	        	
	        
	        	if (finished){
		        	results = (List<AttackResult>)args.get(CalcDisplayActivity.RESULTS);
	        	}
	        	displayResults(critsOn);
	        } 
	 }
	 
	 /**
	 * Sets the correct data for the fragment, when it is asynchronously displayed 
	 */
	 public void setAttackResults(List<AttackResult> results){
		 
		 checkParent();
		 this.results = results;
		 
	 }
	 
	 /**
	  * Displays the results when switched to 
	 */
	 public void displayResults(boolean critsOn){
		 
		 this.critsOn = critsOn;

		 if (finished){
			 display();
		 
		 }else{
			 //displayedText.setText("not done");
		 }


	 }
	 
	 abstract void display();
		 
	 abstract View createLayout(LayoutInflater inflater, ViewGroup container);
		 
		 
	 



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("creating view");
		displayView = createLayout(inflater, container);
		checkParent();

		
		return displayView;
	}



	public void setData(int type, boolean finished, List<AttackResult> results) {
		this.type = type;
		this.finished = finished;
		this.results = results;
		
	}



	public static DisplayFragment createFragment(int position) {
		if (position == ALL)
			return new OverviewDisplayFragment();
		else if (position == SPECIFIC)
			return new SpecificDisplayFragment();
		else
			return null;
	}


}


