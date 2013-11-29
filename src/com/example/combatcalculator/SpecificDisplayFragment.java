package com.example.combatcalculator;

import java.util.ArrayList;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.tree.combatcalculator.AttackResult;

public class SpecificDisplayFragment extends DisplayFragment{
	
	private ViewGroup mAttackGroup;
	private boolean setup = false;

	@Override
	void display() {
		
		if (!setup) {
			setupAttacks();
		}
		 
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
			 Log.e("error setting up specfic attacks", "testing");
		
		 }
	}
	
	
	private void setupAttacks(){
		
		if (results != null){
			for (AttackResult result : results)
				setupAttack(result);
			
			setup = true;
		}
		
	}

	private void setupAttack(AttackResult result) {
		final ViewGroup newView = (ViewGroup) LayoutInflater.from(this.getActivity()).inflate(
                R.layout.specific_attack_result, mAttackGroup, false);

		TextView hitChance = (TextView)newView.findViewById(R.id.specific_hit_chance);
		hitChance.setText(result.hitChancePer);
		
		TextView damage = (TextView)newView.findViewById(R.id.specific_damage);
		damage.setText(AttackResult.formatResult(result.hitDamage));

		TextView expDamage = (TextView)newView.findViewById(R.id.specific_expected_damage);
		expDamage.setText(AttackResult.formatResult(result.getTotalExpectedDamageWithHitChance()));
		
		TextView attackValues = (TextView)newView.findViewById(R.id.attack_values);
		attackValues.setText(result.constructAttackValues());
		
		System.out.println("Setting up info: " + result.constructAttackValues());
		
		if (!result.hasCrit){
			TableRow critRow = (TableRow)newView.findViewById(R.id.crit_row);
			critRow.setVisibility(View.GONE);
		}
			
		
		mAttackGroup.addView(newView, 0);
		
	}


	@Override
	View createLayout(LayoutInflater inflater, ViewGroup container) {
		View specificView = inflater.inflate(R.layout.attack_result_specific_fragment, container, false);
		mAttackGroup = (ViewGroup) specificView.findViewById(R.id.container);
		setupAttacks();
		return specificView;
	}

}
