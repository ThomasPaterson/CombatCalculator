package com.tree.combatcalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AttackResultManager {
	
	
	  //returns a string with the data of the specific attacks and the overall picture
    public ArrayList<AttackResult> setAttackResults(ArrayList<Node> optimalPath, int curFocus){
    	
		ArrayList<AttackResult> results = new ArrayList<AttackResult>();
		
		for (int i = 1; i < optimalPath.size(); i += 3)
			results.add(setAttackResult(optimalPath, i));
		
		Collections.reverse(results);
		
		return results;


    }//end printResults
    
    
    private AttackResult setAttackResult(ArrayList<Node> optimalPath, int index){
    	
    	
    	Node buyNode = optimalPath.get(index);
    	int weaponIndex = (int)buyNode.getValue();
    	AttackResult result;
    	
    	Weapon weapon = getWeapon(optimalPath, index);
    	


		if (weapons.get(weaponIndex).getHasCA())
			return setCAAttackResults(optimalPath, index);
		else{
			
			float hitChance = getHitChance(optimalPath, index);
			float hitDamage = getHitDamage(optimalPath, index);
			boolean hasCrit = attackHasCrit(optimalPath, index);
			
			if (hasCrit){
				float[] critValues = getCritValues(optimalPath, index);
				result = new AttackResult(hitDamage, hitChance, hasCrit, critValues[0], critValues[1], weapon);
			}else{
				result = new AttackResult(hitDamage, hitChance, hasCrit, 0.0f, 0.0f, weapon);
			}
			
			setAttackResultPlayerDecisions(optimalPath, index, result);


			return result;
			
		}


    }
    
    private void setAttackResultPlayerDecisions(ArrayList<Node> optimalPath,
			int index, AttackResult result) {
		
		Node hitNode = getHitNode(optimalPath, index);
		DecisionNode attackDecNode = (DecisionNode) hitNode.getParent();
		result.boughtAttackBoost = attackDecNode.getBoost();
		
		if (index == FIRST_ATTACK){
			List<AtkVar> situation = optimalPath.get(index-1).getSit();
			if (situation != null)
				result.isCharge = AtkVar.checkContainsName(situation, AtkVar.CHARGING);
		}
		
		if (!result.isCharge){
			DecisionNode damDecNode = (DecisionNode) optimalPath.get(index+2);
			result.boughtDamageBoost = damDecNode.getBoost();
		}
		
		
		result.boughtAttack = ((DecisionNode) optimalPath.get(index)).getBoost();
		
	}


	private AttackResult setCAAttackResults(ArrayList<Node> optimalPath, int index) {



    	double[] hitChanceLeft;
    	float damLeft;

    	Node buyNode = optimalPath.get(index);
    	int weaponIndex = (int)buyNode.getValue();

    	int comboNum = ((DecisionNode)buyNode).getComboNum();

    	boolean hasLeft = false;

    	if (atkModel.getNumAttackers()%comboNum != 0)
    		hasLeft = true;

    	Node damNode = optimalPath.get(index+2);


    	ArrayList<AtkVar> mainSit = new ArrayList<AtkVar>(damNode.getChild(0).getSit());
		ArrayList<AtkVar> leftSit = new ArrayList<AtkVar>(damNode.getChild(0).getSit());

		addCABonuses(mainSit, leftSit, atkModel.getNumAttackers(), comboNum);

		int main = (int)Math.floor((float)atkModel.getNumAttackers()/comboNum);

		//get the values together for calculation
    	double[] hitChanceMain = calc.getHitChance(atkModel, defModel, weapons.get(weaponIndex), mainSit);



		//add the damage from the main attacks and left over
		float damMain = calc.getExpectedDamage(atkModel, defModel, weapons.get(weaponIndex), mainSit, false, optMethod)*main;

		if (hasLeft){

			hitChanceLeft = calc.getHitChance(atkModel, defModel, weapons.get(weaponIndex), leftSit);
			damLeft = calc.getExpectedDamage(atkModel, defModel, weapons.get(weaponIndex), leftSit, false, optMethod);

		}else{
			hitChanceLeft = new double[3];
			damLeft = 0;
		}
		
		float hitDamage = (float)(damMain*hitChanceMain[0]+damLeft*hitChanceLeft[0]);

		
		return new AttackResult(
				hitDamage, (float)hitChanceMain[0], false, 
				(float)hitChanceLeft[0], damLeft, 
				getWeapon(optimalPath, index),
				true, comboNum, atkModel.getNumAttackers());

	}

}
