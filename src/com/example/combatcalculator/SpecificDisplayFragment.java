package com.example.combatcalculator;

import java.util.ArrayList;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.tree.combatcalculator.DetailedAttackResult;

public class SpecificDisplayFragment extends DisplayFragment{
	
	private ViewGroup mAttackGroup;
	private boolean setup = false;

	@Override
	void display() {
		
		try {
			
		if (results != null){
			deleteOldAttacks();
			for (DetailedAttackResult result : results)
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




	private void setupAttack(DetailedAttackResult result) {
		final ViewGroup newView = (ViewGroup) LayoutInflater.from(this.getActivity()).inflate(
                R.layout.specific_attack_result, mAttackGroup, false);
		
		TextView weaponName = (TextView)newView.findViewById(R.id.weapon_name);
		weaponName.setText(result.weapon.getName());

		TextView hitChance = (TextView)newView.findViewById(R.id.specific_hit_chance);
		if (critsOn)
			hitChance.setText(result.hitChancePer);
		else
			hitChance.setText(result.hitAndCritChancePer);
		
		TextView damage = (TextView)newView.findViewById(R.id.specific_damage);
		damage.setText(DetailedAttackResult.formatResult(result.hitDamage));

		TextView expDamage = (TextView)newView.findViewById(R.id.specific_expected_damage);
		if (!result.hasCrit || !critsOn)
			expDamage.setText(DetailedAttackResult.formatResult(result.getTotalExpectedDamageWithHitChance(false)));
		else
			expDamage.setText(result.getTruncExpectedHitDamage());
		
		TextView attackValues = (TextView)newView.findViewById(R.id.attack_values);
		attackValues.setText(result.constructAttackValues());
		
		
		TableRow critRow = (TableRow)newView.findViewById(R.id.crit_row);
		TableRow critAndNormalRow = (TableRow)newView.findViewById(R.id.crit_and_normal_row);
		if (!result.hasCrit || !critsOn){	
			critRow.setVisibility(View.GONE);
			critAndNormalRow.setVisibility(View.GONE);
		}else{
			critRow.setVisibility(View.VISIBLE);
			critAndNormalRow.setVisibility(View.VISIBLE);
			
			//crit row
			TextView critChance = (TextView)newView.findViewById(R.id.specific_hit_chance_crit);
			critChance.setText(result.critChancePer);
			
			TextView critDamage = (TextView)newView.findViewById(R.id.specific_damage_crit);
			critDamage.setText(DetailedAttackResult.formatResult(result.critDamage));

			TextView critExpDamage = (TextView)newView.findViewById(R.id.specific_expected_damage_crit);
			critExpDamage.setText(result.getTruncExpectedCritDamage());
			
			//crit and normal row
			TextView critAndNormalChance = (TextView)newView.findViewById(R.id.specific_hit_chance_crit_and_normal);
			critAndNormalChance.setText(result.hitAndCritChancePer);
			
			TextView critAndNormalDamage = (TextView)newView.findViewById(R.id.specific_damage_crit_and_normal);
			critAndNormalDamage.setText(DetailedAttackResult.formatResult(result.getTotalExpectedDamageAssumeHit()));

			TextView critAndNormalExpDamage = (TextView)newView.findViewById(R.id.specific_expected_damage_crit_and_normal);
			critAndNormalExpDamage.setText(DetailedAttackResult.formatResult(result.getTotalExpectedDamageWithHitChance(true)));
			
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
