package com.tree.combatcalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttackManager {
	
	
	//makes the next nodes for a node
    private void makeBuyNode(int curFocus, Node prevNode, int[] initWeapons,
    	boolean doneInits, int weaponToUse, ArrayList<AtkVar> modSit, int depth){


    		//check to see if done with with the initial attacks
    		boolean finishedInits = checkDoneInits(initWeapons, doneInits, modSit, curFocus);

			//reduce focus if charging and it isn't free
    		int numFocus = checkCharging(modSit, finishedInits, curFocus);

    		//if it is either the initial attacks or there is focus left to buy, make a new node
    		if (!finishedInits || curFocus > 0){

				//if it is done initials and has found optimal weapon, use that
				if (usingHeuristics(finishedInits)){

					//if the optimal weapon has run out of attacks, find a new one
					if (!availableForAtk(weapons.indexOf(optimalWeapon), initWeapons))
						findOptimalWeapon(modSit, initWeapons, numFocus);

				    addBuyNode(numFocus-1, prevNode, initWeapons, finishedInits, weapons.indexOf(optimalWeapon), modSit, depth);


				}else
					addBuyAllWeapons(numFocus, prevNode, initWeapons, finishedInits, weaponToUse, modSit, depth);




    		}

    		//else if (usingHeuristics(finishedInits))
    		//	outputRest(curFocus, modSit);




    }//end makeBuyNode
    

    //loops through all weapons, adds a buy node for each one that is available
    //finishes up groups before moving onto a new group
    private void addBuyAllWeapons(int curFocus, Node prevNode, int[] initWeapons,
    	boolean doneInits, int weaponToUse, ArrayList<AtkVar> modSit, int depth){


		//loop through all the weapons, attack with any that are legal to be attacked with
	    for (int i = 0; i < initWeapons.length; i++){

			//if we still have this initial attack, don't deduct focus
			if ((doneInits || initWeapons[i] < weapons.get(i).getNumAtks())
				&& AttackCalculator.legalAttack(weapons.get(i), modSit, atkModel)){


				   //decide if focus should be used or not, keep attacking in a chain
				   //if done initials then remove focus
				   if (!doneInits && (!attacksLeft(weaponToUse, initWeapons) || weaponToUse == i))
				   	addBuyNode(curFocus, prevNode, initWeapons, doneInits, i, modSit, depth);
				else if (doneInits)
					addBuyNode(curFocus-1, prevNode, initWeapons, doneInits, i, modSit, depth);

			}


	    }//end for

    }//end addBuyAllWeapons
    
  //if there are shots left, make a buy node and start the attack node with correct variables
    private void addBuyNode(int curFocus, Node prevNode, int[] initWeapons,
    	boolean doneInits, int weaponToUse, ArrayList<AtkVar> modSit, int depth){

			//make attack if attacks left
    		if (availableForAtk(weaponToUse, initWeapons)){

	    		DecisionNode newNode = addDecisionNode(Node.BUY, prevNode, doneInits, weaponToUse, weaponToUse, modSit);

				int[] copyWeapons = addAttackRecord(initWeapons, weaponToUse);

				makeAttackNode(curFocus, newNode, copyWeapons, doneInits, weaponToUse, modSit, depth);

    		}


    }//end addBuyNode


	//creates a decision node for attacks, one for normal, one for boosted if focus available
    private void makeAttackNode(int curFocus, Node prevNode, int[] initWeapons,
    	boolean doneInits, int weaponToUse, ArrayList<AtkVar> modSit, int depth){

			boolean makeNormal = checkAttackNormal(weaponToUse, doneInits, curFocus, modSit);

    		//make a not boosted attack node, if it is not using heuristics, or if using heuristics and attack
    		//roll is boosted
    		if (makeNormal){
	
	    		DecisionNode newNode = addDecisionNode(Node.ATTACK, prevNode, false, 0, weaponToUse, modSit);
	
	    		makeResultAttackNode(curFocus, newNode, initWeapons, doneInits, weaponToUse, modSit, depth);

    		}

    		//make a boosted attack decision node if there is enough focus, allowed by heuristics
    		if (curFocus > 0 && (!usingHeuristics(doneInits) || !makeNormal || !doneInits)){

    			DecisionNode newNode = addDecisionNode(Node.ATTACK, prevNode, true, 0, weaponToUse, modSit);

    			makeResultAttackNode(curFocus-1, newNode, initWeapons, doneInits, weaponToUse, modSit, depth);
    		}

    }//end makeAttackNode


    //checks to see if an attack roll should be normal or not
    private boolean checkAttackNormal(int weaponToUse, boolean doneInits, int curFocus, List<AtkVar> modSit){

    	//check to see if already boosted
		boolean alreadyBoosted = AtkVar.checkContainsName(modSit, weapons.get(weaponToUse).getVariables(),
			atkModel.getVariables(), AtkVar.BOOSTED_HIT);
		
		System.out.println("alreadyBoosted: " + alreadyBoosted);

		//if it using heuristics, don't make a non boosted if it wants only boosted
		//or if it is already boosted
		if (alreadyBoosted)
			return true;
		else if ((usingHeuristics(doneInits) && checkOptimalChoice(true, false, curFocus) && doneInits))
			return false;

		return true;



    }//end checkAttackNormal



	//creates a node for each possibility (hit, miss, crit if crit available)
    private void makeResultAttackNode(int curFocus, Node prevNode, int[] initWeapons,
    	boolean doneInits, int weaponToUse, ArrayList<AtkVar> modSit, int depth){


    	ArrayList<AtkVar> nextSit = checkPowerfulAtk(prevNode, weapons.get(weaponToUse), modSit);

    	double[] hitChance = calcHitChance(prevNode, weaponToUse,  modSit);

    	//add in all the paths, crit is dependant on if the weapon has a crit effect
    	for (int i = 0; i < hitChance.length; i++){

    		if (i < 2 || weapons.get(weaponToUse).getCrit()){

    			ResultNode newNode = addResultNode(Node.RESULT_ATTACK, (float)hitChance[i], ResultNode.HIT_CAT[i], prevNode);

				makeDamageNode(curFocus, newNode, initWeapons, doneInits, weaponToUse, nextSit, depth);

    		}

    	}


    }//end makeResultAttackNode



    private void makeDamageNode(int curFocus, Node prevNode, int[] initWeapons,
    	boolean doneInits, int weaponToUse, ArrayList<AtkVar> modSit, int depth){

			ArrayList<AtkVar> nextSit = checkSustainedAtk(prevNode, weapons.get(weaponToUse), modSit);

			boolean[] decideAttack = decideFocusBoost(curFocus, prevNode, initWeapons, doneInits, weaponToUse, modSit);

    		//make a not boosted damage node if not charging
    		//if charging, make boosted damage roll
    		if (decideAttack[0])
				addDamageNode(curFocus, prevNode, initWeapons, doneInits, weaponToUse, nextSit, false, depth);
			else if (decideAttack[1])
    			addDamageNode(curFocus, prevNode, initWeapons, doneInits, weaponToUse, nextSit, true, depth);

    		//make a boosted damage decision node if there is enough focus
    		//and there is no miss, and it is allowed by heuristics
    		if (decideAttack[2])
    				addDamageNode(curFocus-1, prevNode, initWeapons, doneInits, weaponToUse, nextSit, true, depth);



    }//end makeDamageNode
    
    
  //adds the nodes for the resolve attack, then if there is focus left passes on the variables to do it again
    private void addResolveNodes(int curFocus, Node prevNode, int[] initWeapons, ArrayList<AtkVar> modSit,
    							boolean boostHit, boolean boostDam, int focusUsed){

    	int index = weapons.indexOf(optimalWeapon);

	    //record attack
		int[] copyWeapon = addAttackRecord(initWeapons, index);


		//add nodes
		DecisionNode buyNode = addDecisionNode(Node.BUY, prevNode, true, index, index, modSit);

		//value set to 1 to tell that this node is from a resolve path
		DecisionNode atkNode = addDecisionNode(Node.ATTACK, buyNode, boostHit, 1, index, modSit);

	    double[] hitChance = calcHitChance(atkNode, index, modSit);

		//if crit, add the damage for the crit and take the weighted average
	    if (optimalWeapon.getCrit())
	    	hitChance[0] += hitChance[2];

	   	ResultNode atkRNode = addResultNode(Node.RESULT_ATTACK, (float)hitChance[0], ResultNode.HIT_CAT[0], atkNode);

	   	DecisionNode damNode = addDecisionNode(Node.DAMAGE, atkRNode, boostDam, 0, index, modSit);

	   	float expDam = getResolveDamage(damNode, index, modSit, hitChance);

		ResultNode damRNode = new ResultNode(Node.RESULT_DAMAGE, expDam, curFocus-focusUsed, modSit, copyWeapon);
	   	damNode.addChild(damRNode);

		if (curFocus - focusUsed > 0)
			resolveRest(curFocus - focusUsed, damRNode, copyWeapon, modSit);


    }//end addResolveNodes

    
  //adds the damage nodes, either boosted or not boosted
    private void addDamageNode(int curFocus, Node prevNode, int[] initWeapons,
    	boolean doneInits, int weaponToUse, ArrayList<AtkVar> modSit, boolean boosted, int depth){

    	DecisionNode newNode = addDecisionNode(Node.DAMAGE, prevNode, boosted, 0, weaponToUse, modSit);

    	makeResultDamageNode(curFocus, newNode, initWeapons, doneInits, weaponToUse, modSit, depth);

    }//end addDamageNode


    //creates a node for each possibility (hit, miss, crit if crit available)
    private void makeResultDamageNode(int curFocus, Node prevNode, int[] initWeapons,
    	boolean doneInits, int weaponToUse, ArrayList<AtkVar> modSit, int depth){

		float expDam = calcExpectedDamage(prevNode, weaponToUse, modSit, false);


    	//for passing on the situation
    	ArrayList<AtkVar> nextSit = handleVariableCleanup(prevNode, doneInits, weaponToUse, modSit);

		//create the nodes
    	ResultNode newNode = new ResultNode(Node.RESULT_DAMAGE, expDam, curFocus, modSit, initWeapons);
    	prevNode.addChild(newNode);


		//expend the rest of the focus the optimal way
		if (doneInits && useHeuristics && curFocus > 0){

			resolveRest(curFocus, newNode, initWeapons, modSit);
			findOptimalWeapon(modSit, initWeapons, curFocus);

		//if not using heuristics, just keep going unless it is time for pruning
		}else if ((depth%MAX_DEPTH != 0 || depth == 0 || doneInits) || !useHeuristics)
			makeBuyNode(curFocus, newNode, initWeapons, doneInits, weaponToUse, nextSit, depth+1);
		else if (!doneInits || curFocus > 0){

			doneTree = false;
			nodesToExpand.add(newNode);

		}

    }//end makeResultDamageNode
    
  //cleans off all the variables that might have been added during the
    private ArrayList<AtkVar> handleVariableCleanup( Node prevNode, boolean doneInits, int weaponToUse, ArrayList<AtkVar> modSit){
    	

    	ArrayList<AtkVar> nextSit = new ArrayList<AtkVar>(modSit);

    	//if it is a weapon with the poweful attack ability, remove the powerful attack variable if it is in
		if (AtkVar.checkContainsName(weapons.get(weaponToUse).getVariables(), AtkVar.POWERFUL_ATK_AB))
			AtkVar.removeVariable(nextSit, AtkVar.POWERFUL_ATK);

		//if it is a crit and the weapon has crit knockdown, change the situation to include knockdown
		if (((ResultNode)prevNode.getParent()).getHitType() == ResultNode.CRIT)
    		if (AtkVar.checkContainsName(weapons.get(weaponToUse).getVariables(), AtkVar.CRIT_KNOCKDOWN))
    			nextSit.add(new AtkVar(AtkVar.KNOCKDOWN));

		//if an attack was made that doesn't have sustained attack, then lose the sustained attack
    	if (!AtkVar.checkContainsName(weapons.get(weaponToUse).getVariables(), AtkVar.SUSTAINED_ATK_AB))
    		AtkVar.removeVariable(nextSit, AtkVar.SUSTAINED_ATK);


    	//if the attack charging, remove charging for next time, only check if not done intitial attacks
    	//also remove first attack trigger
    	if (!doneInits){

    		AtkVar.removeVariable(nextSit, AtkVar.FIRST_ATTACK);

    		//if ( AtkVar.checkContainsName(modSit, AtkVar.CHARGING)){
    			
				AtkVar.removeVariable(nextSit, AtkVar.CHARGING);
				AtkVar.removeVariable(nextSit, AtkVar.CHARGE_DAMAGE);

    		//}
    	}

		return nextSit;

    }//end handleVariableCleanup
    
  //checks to see if any of the weapons in this array haven't been used yet
    //also finds the optimal weapon
    private boolean checkDoneInits(int[] weaponArr, boolean doneAlready, ArrayList<AtkVar> modSit, int curFocus){

    	if (doneAlready)
    		return true;

		//if any weapons have attacks left, then it isnt' done initials
		for (int i = 0; i < weaponArr.length; i++){

			if (weaponArr[i] < weapons.get(i).getNumAtks())
				return false;
		}


		//check for the optimal weapon after the initials are done.  Since it is a depth creation
		//tree, it only needs to find it once, then will finish this branch before moving on to another
		//this will only trigger when the initials are finished, but not recorded to be done
		if (useHeuristics && !AtkVar.equalAtkVarList(modSit, curOptSit))
    		findOptimalWeapon(modSit, weaponArr, curFocus);


		return true;

    }//end checkDoneInits
    
  //prechecks the weapon array and removes all the illegal weapons for the type of fight it is
  	//(melee or ranged)
  	private void removeIllegalAttacks(int[] weaponArr){

      	for (int i = 0; i < weapons.size(); i++){

      		if (!AttackCalculator.legalAttack(weapons.get(i), sit, atkModel))
      			weaponArr[i] = ILLEGAL;
      		else
      			weaponArr[i] = 0;
      	}

      }//end removeIllegalAttacks

	
    //takes the situation given, returns a new arraylist with the boosts requested
    private ArrayList<AtkVar> addBoosts(ArrayList<AtkVar> curSit, boolean boostAtk, boolean boostDam){

		ArrayList<AtkVar> nextSit = new ArrayList<AtkVar>(curSit);

		if (boostAtk)
			nextSit.add(new AtkVar(AtkVar.BOOSTED_HIT_FOCUS));

		if (boostDam)
			nextSit.add(new AtkVar(AtkVar.BOOSTED_DAM_FOCUS));

		return nextSit;


    }//end addBoosts
    

	//checks to see if the previous node was boosted, for if it has powerful attack then the damage
	//is boosted for free
    private ArrayList<AtkVar> checkPowerfulAtk(Node prevNode, Weapon used, ArrayList<AtkVar> modSit){


    	ArrayList<AtkVar> nextSit;

    	//if it is a boosted attack with a weapon with the powerful attack ability, then signify
		//for boosted damage too
    	if (((DecisionNode) prevNode).getBoost() &&AtkVar.checkContainsName(used.getVariables(), AtkVar.POWERFUL_ATK_AB)){

			nextSit = new ArrayList<AtkVar>(modSit);
			nextSit.add(new AtkVar(AtkVar.POWERFUL_ATK));


    	}else
    		nextSit = modSit;

   		return nextSit;


    }//end checkPowerfulAtk


    //checks to see if the weapon you hit with was a sustained attack weapon
    private ArrayList<AtkVar> checkSustainedAtk(Node prevNode, Weapon used, ArrayList<AtkVar> modSit){


    	ArrayList<AtkVar> nextSit;


    	//if it is a boosted attack with a weapon with the powerful attack ability, then signify
		//for boosted damage too
    	if ((((ResultNode) prevNode).getHitType() ==  ResultNode.HIT ||
    		((ResultNode) prevNode).getHitType() ==  ResultNode.CRIT) &&
    			AtkVar.checkContainsName(used.getVariables(), AtkVar.SUSTAINED_ATK_AB)){

			nextSit = new ArrayList<AtkVar>(modSit);
			nextSit.add(new AtkVar(AtkVar.SUSTAINED_ATK));


    	}else
    		nextSit = modSit;

   		return nextSit;


    }//end checkPowerfulAtk
    
	//returns if available for attack, because it has infinite rof or has shots left
    private boolean availableForAtk(int index, int[] initWeapons){

    	//if it is null, return false
    	if (index == -1)
    		return false;
	    else if (weapons.get(index).getROF() == -1 ||
	    	initWeapons[index] < weapons.get(index).getROF()*weapons.get(index).getNumAtks())
	    		return true;
	    else
	    	return false;



    }//end availableForAtk;

    
	//returns if there are attacks left before it finishes its initial attacks
	//-1 is the starting value, so one weapon group isn't always locked in to begin with
    private boolean attacksLeft(int index, int[] initWeapons){

    	if (index == -1)
    		return false;
    	else
    		if (weapons.get(index).getNumAtks() > initWeapons[index])
    			return true;

 		return false;

    }//end attacksLeft
    
	//properly sets the values for the weapon array that stores number of attacks for each weapon
	private int[] prepareWeapons(){

		//record what initials have been used
    	int[] usedWeapons = new int[weapons.size()];

    	removeIllegalAttacks(usedWeapons);

    	return usedWeapons;

	}//end prepareWeapons

	
	//checks to see if the model is charging
		//if it is, deducts focus, then returns focus
		private int checkCharging(ArrayList<AtkVar> modSit, boolean finishedInits, int curFocus){
			
		//mark off the focus if it is charging and doesn't have free charges
	    	if (!finishedInits){

				if (AtkVar.checkContainsName(modSit, AtkVar.CHARGING) &&
					!AtkVar.checkContainsName(atkModel.getVariables(), AtkVar.FREE_CHARGE)){
						return curFocus - 1;
				}
	    	}

	    	return curFocus;


		}//end checkCharging

		//creates a decision node and adds it to the parent node, also stores the proper values
		private DecisionNode addDecisionNode(int type, Node prevNode, boolean boost, float value, int weaponIndex, ArrayList<AtkVar> modSit){

			DecisionNode newNode = new DecisionNode(type, boost, value);

			if (weapons.get(weaponIndex).getHasCA())
				newNode.setComboNum(bestCA(modSit, weaponIndex));

			prevNode.addChild(newNode);

			return newNode;


		}//end addDecisionNode

		//adds a result node of the proper type to the parent, then passes it back for the next node
		private ResultNode addResultNode(int type, float value, int hitType, Node prevNode){

			ResultNode newNode;

			if (type == Node.RESULT_ATTACK)
				newNode = new ResultNode(type, value, hitType);
			else
				newNode = new ResultNode(type, value);

			prevNode.addChild(newNode);

			return newNode;


		}//end addResultNode

		//just adds a record to the array that an attack has been made
		private int[] addAttackRecord(int[] initWeapons, int index){

			int[] copy = Arrays.copyOf(initWeapons, initWeapons.length);
			copy[index] = copy[index]+1;

			return copy;

		}//end addAttackRecord



}


