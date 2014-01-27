package com.tree.combatcalculator;

import java.util.ArrayList;

import com.tree.combatcalculator.nodes.Node;

public class AttackCalcHelper {
	
	//gets the damage that will result from the resolved attack, includes the crit damage
    private float getResolveDamage(DecisionNode damNode, int index, ArrayList<AtkVar> modSit, double[] hitChance){


	    float expDam = calcExpectedDamage(damNode, index, modSit, false);

		//if it has a crit, get (normalDam*hitChance + critDam*critChance)/(critChance+hitChance)
		//this is equal to expected damage assuming it hits
		if (optimalWeapon.getCrit()){

	    	expDam *= hitChance[0]-hitChance[2];
	    	expDam += calcExpectedDamage(damNode, index, modSit, true)*hitChance[2];
	    	expDam /= hitChance[0];

	    }

	    return expDam;

    }//end getResolveDamage
    
  //determines the different boosts and focus spent when resolving based on optimal weapon
    private int getResolveBoosts(ArrayList<AtkVar> modSit, int[] initWeapons, int curFocus, boolean[] boostArr){

		int focusUsed = 1;

		//check to see if the optimal weapon has hit it's max attacks, if so find new weapon
		if (optimalWeapon == null)
			findOptimalWeapon(modSit, initWeapons, curFocus);
    	else if (!availableForAtk(weapons.indexOf(optimalWeapon), initWeapons))
    		findOptimalWeapon(modSit, initWeapons, curFocus);

    	//if there is a weapon left to attack with, continue attacking
    	if (optimalWeapon != null){


			//set up boosts and focus expenditure
			int type = checkAttackType(curFocus);

			if (type == BOOST_ATK){

				boostArr[0] = true;
				focusUsed = 2;

			}else if (type == BOOST_DAM){

				boostArr[1] = true;
				focusUsed = 2;

	    	}else if (type == BOOST_BOTH){

				boostArr[0] = true;
				boostArr[1] = true;
				focusUsed = 3;

			}
    	}

    	return focusUsed;

    }//end getResolveBoosts
    
  //checks type to use, to ensure there is enough focus for the most optimal attack type
    private int checkAttackType(int numFocus){


    	for (int i = 0; i < optimalCombo.length; i++){

    		if (optimalCombo[i] == BOOST_BOTH && numFocus >= 3)
    			return BOOST_BOTH;
    		else if (optimalCombo[i] == BOOST_ATK && numFocus >= 2)
    			return BOOST_ATK;
    		else if (optimalCombo[i] == BOOST_DAM && numFocus >= 2)
    			return BOOST_DAM;
    		else if (optimalCombo[i] == BUY && numFocus >= 1)
    			return BUY;

    	}

		return BUY;

    }//end checkAttackType
    
  //get the damage for a given situation
    private float calcExpectedDamage(Node prevNode, int weaponToUse, ArrayList<AtkVar> modSit, boolean hasCrit){


		//if the attack was a miss, zero damage
    	if (((ResultNode)prevNode.getParent()).getHitType() == ResultNode.MISS)
    		return 0;
    	else{

    		ArrayList<AtkVar> newSit = addBoosts(modSit, false, ((DecisionNode) prevNode).getBoost());

    		//add in crit if a crit was rolled, also handle shred
    		if (((ResultNode)prevNode.getParent()).getHitType() == ResultNode.CRIT || hasCrit){

	    		newSit.add(new AtkVar(AtkVar.CRIT));

	    		//if it is a crit and the weapon has crit knockdown, change the situation to include knockdown
	    		if (AtkVar.checkContainsName(weapons.get(weaponToUse).getVariables(), AtkVar.CRIT_KNOCKDOWN))
	    			newSit.add(new AtkVar(AtkVar.KNOCKDOWN));

    		}

	    	//if the attack is boosted, make a modified situation array for the damage roll
	    	if (((DecisionNode) prevNode).getBoost() && !AtkVar.checkContainsName(modSit, AtkVar.CHARGING))
					newSit.add(new AtkVar(AtkVar.BOOSTED_DAM_FOCUS));

			if (!weapons.get(weaponToUse).getHasCA())
				return  calc.getExpectedDamage(atkModel, defModel, weapons.get(weaponToUse), newSit, false, optMethod)*atkModel.getNumAttackers();
			else
				return calcAttackCA(prevNode, weaponToUse, modSit, ((DecisionNode)prevNode).getComboNum(), new double[3]);

    	}


    }//end calcExpectedDamage

    
  //returns an array with all the damage possibilities for a weapon in an array
  	//order is [normal, hit, dam, both]
      private float[] findWeaponDamage(int i, ArrayList<AtkVar> normal, ArrayList<AtkVar> hit, ArrayList<AtkVar> dam, ArrayList<AtkVar> both){

  		float[] damRecords = new float[4];

      	//find the hit chances
  		double[] hitChanceNormal = calc.getHitChance(atkModel, defModel, weapons.get(i), normal);
  	    double[] hitChanceBoost = calc.getHitChance(atkModel, defModel, weapons.get(i), hit);

  		//check weapon damage for each possibility
  		damRecords[0] = calc.getExpectedDamage(atkModel, defModel, weapons.get(i), normal, true, optMethod);
  		damRecords[1] = calc.getExpectedDamage(atkModel, defModel, weapons.get(i), hit, true, optMethod)/2;

  		damRecords[2] = calc.getExpectedDamage(atkModel, defModel, weapons.get(i), dam, true, optMethod)/2;

  		damRecords[3] = calc.getExpectedDamage(atkModel, defModel, weapons.get(i), both, true, optMethod)/3;

  		//add in the damage for missing for boosted damage attacks
  		float temp_damDamage = ((float)hitChanceNormal[1])*Math.max(Math.max(damRecords[0], damRecords[1]), Math.max(damRecords[2], damRecords[3]));
  		float temp_bothDamage = ((float)hitChanceBoost[1])*Math.max(Math.max(damRecords[0], damRecords[1]), Math.max(damRecords[2], damRecords[3]));

  		damRecords[1] += temp_damDamage/2;
  		damRecords[2] += temp_bothDamage/3;

  		//if it has powerful attack, boosting to hit is also boosting to damage for one focus
  		if (AtkVar.checkContainsName(weapons.get(i).getVariables(), AtkVar.POWERFUL_ATK_AB))
  			damRecords[1] = calc.getExpectedDamage(atkModel, defModel, weapons.get(i), both)/2;

  		return damRecords;


      }//end findWeaponDamage
      
    //calculates the hit chance of a given node, returns an array of [hit, miss, crit]
      private double[] calcHitChance(Node prevNode, int weaponToUse, ArrayList<AtkVar> modSit){


      	ArrayList<AtkVar> newSit = addBoosts(modSit, ((DecisionNode) prevNode).getBoost(), false);


  		//get the crit chance
      	double[] hitChance = new double[3];

  		//determine hit array if it is a combineed attack
      	if (weapons.get(weaponToUse).getHasCA()){

  			calcAttackCA(prevNode, weaponToUse, modSit, ((DecisionNode)prevNode).getComboNum(), hitChance);
  			hitChance[2] = 0;

  		//otherwise find it normally
      	}else{

  	    	hitChance = calc.getHitChance(atkModel, defModel, weapons.get(weaponToUse), newSit);

  	    	//if there is a crit effect, calculate the hit chance and crit chance seperately
  	    	if (weapons.get(weaponToUse).getCrit())
  	    		hitChance[0] = hitChance[0]-hitChance[2];

      	}

      	return hitChance;


      }//end calcHitChance

      
      private Weapon getWeapon(ArrayList<Node> optimalPath, int index) {
  		Node buyNode = optimalPath.get(index);
      	int weaponIndex = (int)buyNode.getValue();
  		return weapons.get(weaponIndex);
  	}


  	private float[] getCritValues(ArrayList<Node> optimalPath, int index) {
  		
  		float[] critValues = new float[2];
  		
  		if (attackHasCrit(optimalPath, index)){
  			
  			Node buyNode = optimalPath.get(index);
  	    	int weaponIndex = (int)buyNode.getValue();
  		    Node damNode = optimalPath.get(index+2);
  		    Node hitNode = getHitNode(optimalPath, index);
  		    
  	    	double[] tempChance = calcHitChance(hitNode.getParent(), weaponIndex, damNode.getChild(0).getSit());

  	    	critValues[0] = (float)tempChance[2];

  	    	//add in a crit, then calculate damage
  	    	ArrayList<AtkVar> newSit = new ArrayList<AtkVar>(damNode.getChild(0).getSit());
  	    	newSit.add(new AtkVar(AtkVar.CRIT));

  	    	critValues[1] = calcExpectedDamage(damNode, weaponIndex, newSit, true);

  	    	//to avoid dividing by zero later
  	    	if (critValues[0] == 0)
  	    		critValues[0] = 0.000001f;
  		}
  		
  		return critValues;
  	}


  	private boolean attackHasCrit(ArrayList<Node> optimalPath, int index) {
  		return getWeapon(optimalPath, index).getCrit();
  	}


  	private float getHitDamage(ArrayList<Node> optimalPath, int index) {
  		Node damNode = optimalPath.get(index+2);
  		return damNode.getChild(0).getValue();
  	}
  	
  	public Node getHitNode(ArrayList<Node> optimalPath, int index){
  		
  		ArrayList<Node> hitNodes = optimalPath.get(index+1).getChildren();
  		
  		for (Node n : hitNodes)
      		if (((ResultNode)n).getHitType() == ResultNode.HIT)
      			return n;	
  		
  		return null;
  	}


  	private float getHitChance(ArrayList<Node> optimalPath, int index) {
  		
  		Node hitNode = null;
  		
  		ArrayList<Node> hitNodes = optimalPath.get(index+1).getChildren();

      	for (Node n : hitNodes)
      		if (((ResultNode)n).getHitType() == ResultNode.HIT)
      			hitNode = n;

  		float hitChance = hitNode.getValue();
  		
      	if (hitNode.getParent().getValue() == 1 && getWeapon(optimalPath, index).getCrit()){
      		float[] critValues = getCritValues(optimalPath, index);
  			hitChance -= critValues[0];
      	}
  			
  		
  		return hitChance;
  	}
  	
  	


  	//determines if a weapon has a crit, if it does determines damage from it
      private boolean getCritValues(int weaponIndex, Node hitNode, Node damNode, float[] critValues){

  		//if weapon has crit, figure out crit damage and hit chance
     		if (weapons.get(weaponIndex).getCrit()){

  	    	double[] tempChance = calcHitChance(hitNode.getParent(), weaponIndex, damNode.getChild(0).getSit());

  	    	critValues[0] = (float)tempChance[2];

  	    	//add in a crit, then calculate damage
  	    	ArrayList<AtkVar> newSit = new ArrayList<AtkVar>(damNode.getChild(0).getSit());
  	    	newSit.add(new AtkVar(AtkVar.CRIT));

  	    	critValues[1] = calcExpectedDamage(damNode, weaponIndex, newSit, true);

  	    	//to avoid dividing by zero later
  	    	if (critValues[0] == 0)
  	    		critValues[0] = 0.000001f;

  	    	return true;

  	    }

  	    return false;


      }//end getCritValues

      //finds the best CMA or CRA number to do
      //splits attacks into a number of CAs of j size, then the leftovers in one CA
      private int bestCA(ArrayList<AtkVar> modSit, int weaponIndex){

  		int bestCMA = 1;
  		float bestDam = 0;


  		//check each combination of CA to see which is the best
  		for (int j = 1; j <= atkModel.getNumAttackers(); j++){

  			float dam = 0;

  			ArrayList<AtkVar> mainSit = new ArrayList<AtkVar>(modSit);
  			ArrayList<AtkVar> leftSit = new ArrayList<AtkVar>(modSit);

  			int main = (int)Math.floor((float)atkModel.getNumAttackers()/j);

  			addCABonuses(mainSit, leftSit, atkModel.getNumAttackers(), j);

  			//add the damage from the main attacks and left over
  			dam += calc.getExpectedDamage(atkModel, defModel, weapons.get(weaponIndex), mainSit, true, optMethod)*main;
  			dam += calc.getExpectedDamage(atkModel, defModel, weapons.get(weaponIndex), leftSit, true, optMethod);

  			//if the damage is higher then the previous best, save this combination
  			if (dam > bestDam){

  				bestCMA = j;
  				bestDam = dam;

  			}
  		}

  		return bestCMA;

  	}//end bestCA
      
    //adds the additonal points to hit and damage for CAs
  	private void addCABonuses(ArrayList<AtkVar> mainSit, ArrayList<AtkVar> leftSit, int total, int numCA){

  		//don't add bonus if not combining attacks
  		if (numCA > 1){

  			//set up the main attacks for a CA
  			System.out.println("Number of attacks in CMA: " + numCA);

  			mainSit.add(new AtkVar(AtkVar.MOD_ATK, numCA));
  			mainSit.add(new AtkVar(AtkVar.MOD_POW, numCA));

  			//calculate the leftovers as a single CA
  			int left = total%numCA;

  			System.out.println("Number of attacks last: " + left);

  			//if the last attack is only 1 attack, do not add bonus
  			if (left > 1){

  				leftSit.add(new AtkVar(AtkVar.MOD_ATK, left));
  				leftSit.add(new AtkVar(AtkVar.MOD_POW, left));

  			}

  		}


  	}//end addCABonuses


  	//returns the total damage expected with the number of attaacks specified
  	//also modifies the hit array sent to the proper value
  	private float calcAttackCA(Node prevNode, int weaponIndex, ArrayList<AtkVar> modSit, int numCombine, double[] hitArray){

  		//TODO: add crit properly to CAs

  		boolean hasLeft;
  		int numAtks;

  		if (atkModel.getNumAttackers()%numCombine != 0)
  			hasLeft =  true;
  		else
  			hasLeft = false;

  		ArrayList<AtkVar> newSit = addBoosts(modSit, ((DecisionNode) prevNode).getBoost(), false);

  		ArrayList<AtkVar> mainSit = new ArrayList<AtkVar>(newSit);
  		ArrayList<AtkVar> leftSit = new ArrayList<AtkVar>(newSit);

  		//determine number of main attacks and total number of attacks
  		int main = (int)Math.floor((float)atkModel.getNumAttackers()/numCombine);

  		numAtks = main;

  		if (hasLeft)
  			numAtks += 1;

  		addCABonuses(mainSit, leftSit, atkModel.getNumAttackers(), numCombine);

  		//get the values together for calculation
      	double[] hitChanceMain = calc.getHitChance(atkModel, defModel, weapons.get(weaponIndex), mainSit);
      	double[] hitChanceLeft = calc.getHitChance(atkModel, defModel, weapons.get(weaponIndex), leftSit);

  		float damMain = calc.getExpectedDamage(atkModel, defModel, weapons.get(weaponIndex), mainSit, false, optMethod);
  		float damLeft = calc.getExpectedDamage(atkModel, defModel, weapons.get(weaponIndex), leftSit, false, optMethod);


  		float totalDam = damMain*main + damLeft*(numAtks-main);
  		float expDam = damMain*(float)hitChanceMain[0]*main + damLeft*(float)hitChanceLeft[0]*damLeft*(numAtks-main);


  		//return the hit chance from the equation hitChance*totalDamage = expected Damage
  		//makes sure the tree gets the proper expected damage while searching
  		hitArray[0] =  expDam/totalDam;
  		hitArray[1] =  1-hitArray[0];


  		return totalDam;

  	}//end calcAttackCA

	public static float calcHitProbability(Node attackResNode,
			PermanentTreeData permData) {
		return 0.0f;
	}

	public static float calcDamage(Node damageResNode,
			PermanentTreeData permData) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static float calcFullAttack(Node finalResNode,
			PermanentTreeData permData) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static float calculateValue(PermanentTreeData permData,
			Node finalResNode) {
		// TODO Auto-generated method stub
		return 0.0f;
		
	}

}
