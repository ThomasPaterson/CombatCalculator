package com.example.combatcalculator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment for displaying info in the tabs of calc display
 */

public class DisplayFragment extends Fragment {

	public static final String ARG_SECTION_NUMBER = "section_number";
	public static final int ALL = 0;
	public static final int SPECIFIC = 1;
	private boolean finished = false;
	private String allAttacks = "blank";
	private String seperateAttacks = "blank";
	private TextView displayedText;
	private int type = 0;
	
	public DisplayFragment() {
		
	}


	
	 @Override
	    public void onStart() {
	        super.onStart();

	        // During startup, check if there are arguments passed to the fragment.
	        // onStart is a good place to do this because the layout has already been
	        // applied to the fragment at this point so we can safely call the method
	        // below that sets the article text.
	        Bundle args = getArguments();
	        if (args != null) {
	        	finished = args.getBoolean(CalcDisplayActivity.PATH_STATE);
	        	type = args.getInt(CalcDisplayActivity.FRAG_TYPE);
	        	
	        	
	        	
	        	if (finished){
		        	allAttacks = args.getString(CalcDisplayActivity.OVERVIEW);
		        	seperateAttacks = args.getString(CalcDisplayActivity.ATTACKS_OVERVIEW);
	        	}
	   
	        }
	        
	        displayResults();
	    }
	 
	 /**
	 * Sets the correct data for the fragment, when it is asynchronously displayed 
	 */
	 public void setResults(String overall, String specific){
		 
		 allAttacks = overall;
		 seperateAttacks = specific;
		 
		 if (type == ALL)
			 displayedText.setText(allAttacks);
		 else
			 displayedText.setText(seperateAttacks);
		 
		 
	 }
	 
	 /**
	  * Displays the results when switched to 
	 */
	 public void displayResults(){

		 if (finished){
			 if (type == ALL)
				 displayedText.setText(allAttacks);
			 else
				 displayedText.setText(seperateAttacks);
		 
		 }else
			 displayedText.setText("not done");


	 }



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Create a new TextView and set its text to the fragment's section
		// number argument value.

		displayedText = new TextView(getActivity());
		displayedText.setGravity(Gravity.CENTER);
		
		if (type == ALL)
			displayedText.setText(allAttacks);
		else
			 displayedText.setText(seperateAttacks);

		
		return displayedText;
	}
}


