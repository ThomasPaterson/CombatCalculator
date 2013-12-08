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
		
		try {
			
		if (results != null){
			deleteOldAttacks();
			for (AttackResult result : results)
				setupAttack(result);
		}
		 
		 }catch (Exception e) {
			 Log.e("error setting up specfic attacks", "testing");
		 }
	}
	
	


	private void deleteOldAttacks() {
		
		for (int i = mAttackGroup.getChildCount()-1; i >= 0 ; i--){
			
			ViewGroup curView = (ViewGroup) mAttackGroup.getChildAt(i);
			mAttackGroup.removeView(curView);
			
		}
		
		
	}




	private void setupAttack(AttackResult result) {
		final ViewGroup newView = (ViewGroup) LayoutInflater.from(this.getActivity()).inflate(
                R.layout.specific_attack_result, mAttackGroup, false);

		TextView hitChance = (TextView)newView.findViewById(R.id.specific_hit_chance);
		if (critsOn)
			hitChance.setText(result.hitChancePer);
		else
			hitChance.setText(result.hitAndCritChancePer);
		
		TextView damage = (TextView)newView.findViewById(R.id.specific_damage);
		damage.setText(AttackResult.formatResult(result.hitDamage));

		TextView expDamage = (TextView)newView.findViewById(R.id.specific_expected_damage);
		expDamage.setText(AttackResult.formatResult(result.getTotalExpectedDamageWithHitChance(critsOn)));
		
		TextView attackValues = (TextView)newView.findViewById(R.id.attack_values);
		attackValues.setText(result.constructAttackValues());
		
		
		TableRow critRow = (TableRow)newView.findViewById(R.id.crit_row);
		if (!result.hasCrit || !critsOn){	
			critRow.setVisibility(View.GONE);
		}else{
			critRow.setVisibility(View.VISIBLE);
		}
			
		
		mAttackGroup.addView(newView, 0);
		
	}


	@Override
	View createLayout(LayoutInflater inflater, ViewGroup container) {
		View specificView = inflater.inflate(R.layout.attack_result_specific_fragment, container, false);
		mAttackGroup = (ViewGroup) specificView.findViewById(R.id.container);
		display();
		return specificView;
	}

}
