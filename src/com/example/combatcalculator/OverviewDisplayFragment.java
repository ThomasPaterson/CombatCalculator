package com.example.combatcalculator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import com.tree.combatcalculator.DetailedAttackResult;

public class OverviewDisplayFragment extends DisplayFragment{
	
	

	@Override
	void display() {
		 
		 //TODO: get crits set and working
		 try {
		 TextView numAttacks = (TextView)displayView.findViewById(R.id.num_attacks_hit_entry);
		 numAttacks.setText(DetailedAttackResult.totalHits(results, critsOn));
		 
		 TextView expDam = (TextView)displayView.findViewById(R.id.expected_damage_entry);
		 expDam.setText(DetailedAttackResult.expDamage(results, critsOn));
		 
		 TextView expDamAllHit = (TextView)displayView.findViewById(R.id.expected_damage_all_hit_entry);
		 expDamAllHit.setText(DetailedAttackResult.expDamageAllHit(results, critsOn));
		 
		 
		 TableRow critRow = (TableRow)displayView.findViewById(R.id.crit_row);
		 if (critsOn){
			 TextView numCrits = (TextView)displayView.findViewById(R.id.num_crits_entry); 
			 numCrits.setText(DetailedAttackResult.totalCrits(results, critsOn));
			 critRow.setVisibility(View.VISIBLE);
		 }else{
			 critRow.setVisibility(View.GONE);
		 }
		 
		 }catch (Exception e) {
			 Log.e("error setting up overview", e.getMessage());
		
		 }
	}

	@Override
	View createLayout(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.attack_result_overview_fragment, container, false);
	}


}
