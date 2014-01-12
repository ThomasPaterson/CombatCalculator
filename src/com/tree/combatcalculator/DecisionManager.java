package com.tree.combatcalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DecisionManager {
	
	//format is makeNormal, makeCharge, makeBoost
	//checks a large number of logical possibilities to see if it should be doing a charge damage roll, normal one, or
	//a boosted one
    private boolean[] decideFocusBoost(int curFocus, Node prevNode, int[] initWeapons,
    	boolean doneInits, int weaponToUse, ArrayList<AtkVar> modSit){


    		boolean makeNormal;
			boolean[] holder = new boolean[3];

			boolean isCharging = AtkVar.checkContainsName(modSit, AtkVar.CHARGING);

			//check to see if already boosted
			boolean alreadyBoosted = AtkVar.checkContainsName(sit, weapons.get(weaponToUse).getVariables(),
				atkModel.getVariables(), AtkVar.BOOSTED_DAM);


			//if it using heuristics, don't make a non boosted if it wants only boosted
			if ((usingHeuristics(doneInits) && checkOptimalChoice(false, true, curFocus)))
				makeNormal = false;
			else
				makeNormal = true;

			//make a boosted if using heuristics and not making a normal, or if not charging
			//and if not already boosted
			if (!alreadyBoosted && ((usingHeuristics(doneInits) && !makeNormal) ||
				(curFocus > 0 && !isCharging && ((ResultNode)prevNode).getHitType() != ResultNode.MISS)))
				holder[2] = true;


    		//make a not boosted damage node if not charging
    		//if charging, make boosted damage roll
    		if ((!isCharging || (isCharging && weapons.get(weaponToUse).getRanged())) && (makeNormal ||
    			((ResultNode)prevNode).getHitType() == ResultNode.MISS) || alreadyBoosted){

	    		holder[0] = true;

	    	}else if (isCharging){

    			holder[1] = true;
    		}

			return holder;

    }//end decideFocusBoost
    
  //determines what is the optimal weapon
    private void findOptimalWeapon(ArrayList<AtkVar> modSit, int[] initWeapons, int curFocus){

		//save the current situation so that we can check if it is the same later
    	curOptSit = new ArrayList<AtkVar>(modSit);

		//create the various situation variables for each possibility
    	ArrayList<AtkVar> boostedHit = new ArrayList<AtkVar>(modSit);
    	boostedHit.add(new AtkVar(AtkVar.BOOSTED_HIT_FOCUS));



    	ArrayList<AtkVar> boostedHit_Dam = new ArrayList<AtkVar>(modSit);
    	boostedHit_Dam.add(new AtkVar(AtkVar.BOOSTED_HIT_FOCUS));
    	boostedHit_Dam.add(new AtkVar(AtkVar.BOOSTED_DAM_FOCUS));

    	ArrayList<AtkVar> boostedDam = new ArrayList<AtkVar>(modSit);
    	boostedDam.add(new AtkVar(AtkVar.BOOSTED_DAM_FOCUS));

    	ArrayList<AtkVar> normal = new ArrayList<AtkVar>(modSit);



    	//checks each weapon for how much is the optimal amount of damage squeezed out of 1 focus

    	float bestDamage = -1;
    	int bestWeaponIndex = -1;

		//go through each weapon and figure out the best one for damage per focus spent
    	for (int i = 0; i < weapons.size(); i++){

    		//make sure that the weapon has attacks left
    		if (availableForAtk(i, initWeapons)){

    			float[] damArr = findWeaponDamage(i, normal, boostedHit, boostedDam, boostedHit_Dam);

    			float bestTempDam = Math.max(Math.max(damArr[0], damArr[2]), Math.max(damArr[2], damArr[3]));


				//if any of these beat out the next best weapon for optimal damage for 1 focus, take it
				if (bestTempDam > bestDamage){

					bestWeaponIndex = i;
					//get the best damage
					bestDamage = bestTempDam;

					optimalCombo = findOptimalCombo(damArr[0], damArr[1], damArr[2], damArr[3], initWeapons, curFocus);

				}

    		}


		}

		if (bestWeaponIndex >= 0)
			optimalWeapon = weapons.get(bestWeaponIndex);
		else
			optimalWeapon = null;

		//System.out.println("Best weapon is " + bestWeaponIndex);



    }//end findOptimalWeapon
    
  //determines what is the optimal weapon
    private void findOptimalWeapon(ArrayList<AtkVar> modSit, int[] initWeapons, int curFocus){

		//save the current situation so that we can check if it is the same later
    	curOptSit = new ArrayList<AtkVar>(modSit);

		//create the various situation variables for each possibility
    	ArrayList<AtkVar> boostedHit = new ArrayList<AtkVar>(modSit);
    	boostedHit.add(new AtkVar(AtkVar.BOOSTED_HIT_FOCUS));



    	ArrayList<AtkVar> boostedHit_Dam = new ArrayList<AtkVar>(modSit);
    	boostedHit_Dam.add(new AtkVar(AtkVar.BOOSTED_HIT_FOCUS));
    	boostedHit_Dam.add(new AtkVar(AtkVar.BOOSTED_DAM_FOCUS));

    	ArrayList<AtkVar> boostedDam = new ArrayList<AtkVar>(modSit);
    	boostedDam.add(new AtkVar(AtkVar.BOOSTED_DAM_FOCUS));

    	ArrayList<AtkVar> normal = new ArrayList<AtkVar>(modSit);



    	//checks each weapon for how much is the optimal amount of damage squeezed out of 1 focus

    	float bestDamage = -1;
    	int bestWeaponIndex = -1;

		//go through each weapon and figure out the best one for damage per focus spent
    	for (int i = 0; i < weapons.size(); i++){

    		//make sure that the weapon has attacks left
    		if (availableForAtk(i, initWeapons)){

    			float[] damArr = findWeaponDamage(i, normal, boostedHit, boostedDam, boostedHit_Dam);

    			float bestTempDam = Math.max(Math.max(damArr[0], damArr[2]), Math.max(damArr[2], damArr[3]));


				//if any of these beat out the next best weapon for optimal damage for 1 focus, take it
				if (bestTempDam > bestDamage){

					bestWeaponIndex = i;
					//get the best damage
					bestDamage = bestTempDam;

					optimalCombo = findOptimalCombo(damArr[0], damArr[1], damArr[2], damArr[3], initWeapons, curFocus);

				}

    		}


		}

		if (bestWeaponIndex >= 0)
			optimalWeapon = weapons.get(bestWeaponIndex);
		else
			optimalWeapon = null;

		//System.out.println("Best weapon is " + bestWeaponIndex);



    }//end findOptimalWeapon
    
  //finds the optimal order of spending focus
    private int[] findOptimalCombo(float normal, float hit, float dam, float both, int[] initWeapons, int curFocus){

    	int numAtks = checkNumAttacks(initWeapons);

    	float[] order = {normal, hit, dam, both};

    	//if there are 3x as much focus as available attacks, boost them all
    	if (numAtks <= curFocus/3){

    		order[3] = both;
    		order[2] = Math.max(hit, dam);
    		order[1] = Math.min(hit, dam);
    		order[0] = normal;

    	//if there is 2x as much focus, must not take a buy for the optimal action
    	}else{

    		Arrays.sort(order);

    		//if there is a cap to the number of attacks, make sure to spend some focus
    		if (curFocus > numAtks){

	    		if (order[3] == normal){

	    			float temp = order[2];
	    			order[2] = normal;
	    			order[3] = temp;

	    		}

    		}

    	}

			int[] optCombo = new int[4];

			//find the correct order, in descending order
			//so the first choice is the best
			for (int i = 0; i < order.length; i++){

				if (order[i] == normal)
					optCombo[order.length-1-i] = BUY;
				else if (order[i] == hit)
					optCombo[order.length-1-i] = BOOST_ATK;
				else if (order[i] == dam)
					optCombo[order.length-1-i] = BOOST_DAM;
				else
					optCombo[order.length-1-i] = BOOST_BOTH;


			}

		return optCombo;

    }//end findOptimalCombo



    //called when checking to boost attack or boost damage, checks to see if it should, returns if there
    // is a boost path or should be a non boost path
    private boolean checkOptimalChoice(boolean boostAtk, boolean boostDam, int numFocus){

		//if the best choice is just buy attacks, no bos
    	if (optimalCombo[0] == BUY || numFocus == 0)
    		return false;
    	else if (boostAtk){

			//if checking if it should boost attack, if it is either the boost attack choice, or the boost both with enough focus
			//or the boost both with only enough focus to boost 1, but prefence of boost attack over damage
    		if (optimalCombo[0] == BOOST_ATK || (optimalCombo[0] == BOOST_BOTH && numFocus >= 2))
    			return true;
    		else if (optimalCombo[1] == BOOST_ATK && optimalCombo[0] == BOOST_BOTH && numFocus == 1)
    			return true;
    		else
    			return false;

		//if it is boost damage, boost damage if there is enough focus and it is best, do so
    	}else if (boostDam){

    		if (optimalCombo[0] == BOOST_DAM || optimalCombo[0] == BOOST_BOTH)
    			return true;
    		else
    			return false;

    	}else
    		return false;




    }//end checkOptimalChoice
    
    //returns if it should be using the heuristic measurements yet
    private boolean usingHeuristics(boolean doneInits){

    	if (doneInits && useHeuristics)
    		return true;
    	else
    		return false;

    }//end usingHeuristics


    //checks to see if there are too many possible attacks for high focus/weapons
    //returns false if there are oo many possible
    private int checkNumAttacks(int[] initWeapons){

    	int maxAttacks = 0;

    	for (int i = 0; i < weapons.size(); i++){

    		if (weapons.get(i).getROF() == -1 && initWeapons[i] != ILLEGAL)
    			maxAttacks += ILLEGAL + weapons.get(i).getNumAtks();
    		else
    			maxAttacks += weapons.get(i).getROF()*weapons.get(i).getNumAtks();

    	}

    	return maxAttacks;


    }//end checkNumAttacks
    
	//turn on heuristics if there is a large number of possibilities
	//check to see if a lot of attacks can be generated
	public static boolean checkNeedHeuristics(List<Map<String, AtkVar>> permState, int numFocus){

		if (!(checkNumAttacks(permState)%ILLEGAL > 3)){

    	if (weapons.size() > 2 && numFocus > 2)
    		return true;
    	else if (weapons.size() > 3)
    		return true;
    	else if (numFocus > 3)
    		return true;
    	else
    		return false;

		}else
    		return true;

	}//end checkNeedHeuristics

	public boolean checkNeedHeuristics(int numFocus) {
		// TODO Auto-generated method stub
		return false;
	}

}
