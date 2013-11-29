package com.example.combatcalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import java.util.Collections;

public class OptionsActivity extends Activity implements OnItemSelectedListener{
	
	public final static String TYPE_CHOSEN = "com.example.myfirstapp.TYPE_CHOSEN";
	public final static String NUM_CHOSEN = "com.example.myfirstapp.NUM_CHOSEN";
	public final static String PREV_CHOICE = "com.example.myfirstapp.PREV_CHOICE";
	public final static String CHANGED = "com.example.myfirstapp.CHANGED";
	


	private int numChosen = -1;
	private List<AttackProperty> options;
	private List<Map<String, AttackProperty>> optionsMap;
	private List<Map<String, AttackProperty>> savedOptions;
	private String optionType;
	private ViewGroup mContainerView;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		
		optionType = intent.getStringExtra(TYPE_CHOSEN);
		System.out.println("Type chosen: " + optionType);
		
		setContentView(R.layout.activity_options);
		mContainerView = (ViewGroup) findViewById(R.id.container);

		List<AttackProperty> prevSet = intent.getParcelableArrayListExtra(PREV_CHOICE);
		
		options = AttackProperty.getOptions(optionType, getBaseContext());
		
		if (optionType.equals(ConfigManager.WEAPONS))
			numChosen = intent.getIntExtra(NUM_CHOSEN, 0);
		
		optionsMap = AttackProperty.makeMapForSpinner(options);
		
		if (prevSet == null)
			prevSet = new ArrayList<AttackProperty>();
		else
			setupOldSet(prevSet);
		
	}
	

	
	
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    // Save the user's current state
			savedInstanceState.putSerializable(NUM_CHOSEN, numChosen);
	    
	    
	    // Always call the superclass so it can save the view hierarchy state
	    super.onSaveInstanceState(savedInstanceState);
	}
	

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;

            case R.id.action_add_item:
                // Hide the "empty" view since there is now at least one item in the list.
               
                addItem();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
	
	 private void addItem() {
	        addItem(null);
	    }
	 
	 private void addItem(AttackProperty newProperty) {
		 
		 	findViewById(android.R.id.empty).setVisibility(View.GONE);
		 	
	        // Instantiate a new "row" view.
	        final ViewGroup newView = (ViewGroup) LayoutInflater.from(this).inflate(
	                R.layout.list_special_rules, mContainerView, false);
	        
	        final Spinner optionsSpinner = (Spinner)newView.findViewById(R.id.options_selection_spinner);
	        final Spinner valueSpinner = (Spinner)newView.findViewById(R.id.values_selection_spinner);
	        
	        
	        setupSpinner(optionsSpinner, newProperty);
	        setupValueSpinner(valueSpinner, newProperty);
	        
	        toggleValueView(optionsSpinner, valueSpinner);
			
	        
	        
	        // Set a click listener for the "X" button in the row that will remove the row.
	        newView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
	            
	            public void onClick(View view) {
	                // Remove the row from its parent (the container view).
	                // Because mContainerView has android:animateLayoutChanges set to true,
	                // this removal is automatically animated.
	                mContainerView.removeView(newView);

	                // If there are no rows remaining, show the empty view.
	                if (mContainerView.getChildCount() == 0) {
	                    findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
	                }
	            }
	        });
	        
	     // Set a selection listener for the "type" button in the row that will make the value invisible for binary choices
	        optionsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

	            
	        	
	        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	        		
	        		toggleValueView(optionsSpinner, valueSpinner);
	        			
	            }
	        	
	        	
				public void onNothingSelected(AdapterView<?> arg0) {
					
				}
	        });
	        

	        mContainerView.addView(newView, 0);
	    }
	 
	 
	 
	 private void toggleValueView(Spinner optionsSpinner, Spinner valueSpinner){
		 
		 Map<String, AttackProperty> mappedProperty = (Map<String, AttackProperty>) optionsSpinner.getSelectedItem();
 		
 		
 		if (mappedProperty.get("Name").hasValues())
 			valueSpinner.setVisibility(View.VISIBLE);
 		else
 			valueSpinner.setVisibility(View.INVISIBLE);
		 
	 }
	 
	 public void onItemSelected(AdapterView<?> parent, View view, 
	            int pos, long id) {
	        // An item was selected. You can retrieve the selected item using
	        // parent.getItemAtPosition(pos)
		 
		 Map<String, AttackProperty> mappedProperty = (Map<String, AttackProperty>) parent.getItemAtPosition(pos);

	 }

	    public void onNothingSelected(AdapterView<?> parent) {
	        // Another interface callback
	    }

	 
	 
	 private void addPropertyToSpinner(Spinner spinner, AttackProperty newProperty){
		 if (newProperty != null){
			 
			 for (int i = 0; i < options.size(); i++)
				 if (options.get(i).getName().equals(newProperty.getName()))
					 spinner.setSelection(i); 
		 }
	 }
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_options, menu);
		return true;
	}

	
	


	public void returnList(View view){
		
		Intent result = new Intent();
		

		result.putParcelableArrayListExtra(CHANGED, (ArrayList<AttackProperty>)getAttackProperties());
		result.putExtra(TYPE_CHOSEN, optionType);
		
		if (optionType.equals(ConfigManager.WEAPONS))
			result.putExtra(NUM_CHOSEN, numChosen);
		
		setResult(getRequestType(), result);
		finish();

	    
	}
	
	
	private int getRequestType(){
		
		if (optionType.equals(ConfigManager.ATTACKER))
			return ConfigManager.ATTACKER_REQ;
		else if (optionType.equals(ConfigManager.DEFENDER))
			return ConfigManager.DEFENDER_REQ;
		else if (optionType.equals(ConfigManager.WEAPONS))
			return ConfigManager.WEAPON_REQ;
		
		return -1;
		
		
	}
	
private void setupSpinner(Spinner newSpinner, AttackProperty newProperty){
	
	  String[] from = new String[] { "Name" };
	  int[] to = new int[] { android.R.id.text1 };
		
	  SimpleAdapter simpleAdapter = new SimpleAdapter(this, optionsMap, android.R.layout.simple_spinner_item, from, to);
	  
	  simpleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		newSpinner.setAdapter(simpleAdapter);
		
	addPropertyToSpinner(newSpinner, newProperty);
	
	newSpinner.setOnItemSelectedListener(this);
		
		
}

private void setupValueSpinner(Spinner newSpinner, AttackProperty newProperty){
	
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.list_item_array, android.R.layout.simple_spinner_item);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		newSpinner.setAdapter(adapter);
		
		newSpinner.setSelection(findValue(newProperty, R.array.list_item_array));
		

}

private int findValue(AttackProperty newProperty, int listItemArrayId){
	
	String[] listItemArray = getResources().getStringArray(listItemArrayId);
	System.out.println("array: " + listItemArray.toString());
	
	int zeroMarker = 0;
	
	
	for (int i = 0; i < listItemArray.length; i++){
		
		if (newProperty != null) {
		if (newProperty.getValue().equals(listItemArray[i]))
			return i;
		}
		if (listItemArray[i].equals(AttackProperty.DEFAULT_VALUE))
			zeroMarker = i;
		
	}
	
	return zeroMarker;
}

private List<AttackProperty> getAttackProperties(){
	
	List<AttackProperty> properties = new ArrayList<AttackProperty>();
	
	//loop through entire group of weapons and add each one
	for (int i = 0; i < mContainerView.getChildCount(); i++){
	
		ViewGroup curView = (ViewGroup) mContainerView.getChildAt(i);
		
		Spinner optionsSpinner = (Spinner) curView.findViewById(R.id.options_selection_spinner);
		Map<String, AttackProperty> mappedProperty = (Map<String, AttackProperty>) optionsSpinner.getSelectedItem();
		
		AttackProperty property = mappedProperty.get("Name");
		
		if (property.hasValues()){
			Spinner valueSpinner = (Spinner) curView.findViewById(R.id.values_selection_spinner);
			property.setValue(findValueFromValueSpinner(valueSpinner.getSelectedItemPosition()));
		}else{
			property.setValue(AttackProperty.DEFAULT_VALUE);
		}
		
		properties.add(property);
	    
	}
	
	return properties;
	
}

private String findValueFromValueSpinner(int curPosition){
	
	String[] listItemArray = getResources().getStringArray(R.array.list_item_array);
	
	
	return Integer.toString(Integer.parseInt(listItemArray[0]) + curPosition);
	
	
}

	private void setupOldSet(List<AttackProperty> prevSet){
		
		for (int i = prevSet.size()-1; i >= 0; i--)
			addItem(prevSet.get(i));
		
		
	}

	
	
	/**
     * If canceled, returns nothing
    */
	public void returnEmptyList(View view){
		
		setResult(0);
		finish();
  
		
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	         finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}
	

}
