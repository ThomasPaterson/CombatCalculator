package com.example.combatcalculator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tree.combatcalculator.AttackResult;

public class OverviewDisplayFragment extends DisplayFragment{

	@Override
	void display() {
		 
		 //TODO: get crits set and working
		 try {
		 TextView numAttacks = (TextView)displayView.findViewById(R.id.num_attacks_hit_entry);
		 numAttacks.setText(AttackResult.totalHits(results, true));
		 
		 TextView expDam = (TextView)displayView.findViewById(R.id.expected_damage_entry);
		 expDam.setText(AttackResult.expDamage(results, true));
		 
		 TextView expDamAllHit = (TextView)displayView.findViewById(R.id.expected_damage_all_hit_entry);
		 expDamAllHit.setText(AttackResult.expDamageAllHit(results, true));
		 
		 TextView numCrits = (TextView)displayView.findViewById(R.id.num_crits_entry);
		 numCrits.setText(AttackResult.totalCrits(results, true));
		 
		 }catch (Exception e) {
			 Log.e("error setting up overview", e.getMessage());
		
		 }
	}

	@Override
	View createLayout(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.attack_result_overview_fragment, container, false);
	}

}
