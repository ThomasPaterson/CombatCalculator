package com.tree.combatcalculator;

/**
 * @(#)TreeManager.java
 *
 *Creates the tree, then manages it, getting the results requested, then returning the information gathered
 * @author
 * @version 1.00 2012/10/15
 */

import java.util.ArrayList;
import java.util.Arrays;

public class TreeManager {
	
	static private String nl = "\n";

	private AttackModel atkModel;
	private DefendModel defModel;
	private AttackCalculator calc;
	private int optMethod;
	private ArrayList<AtkVar> sit;
	private ArrayList<Weapon> weapons;
	private Weapon optimalWeapon;
	private DecisionNode parent;
	private ArrayList<Node> nodesToExpand;
	private boolean doneTree;

	//handles multiple attacking models, and cma with those models
	private boolean multAtkers;

	//useHeuristics is triggered if there is a large number of weapons or focus
	private boolean useHeuristics;

	private int MAX_DEPTH = 3;

	//stores and recognizes optimal focus expenditure
	private int[] optimalCombo;
	private ArrayList<AtkVar> curOptSit;
	static private int BUY = 0;
	static private int BOOST_ATK = 1;
	static private int BOOST_DAM = 2;
	static private int BOOST_BOTH = 3;

	//used to represent when an attack is illegal
	static private int ILLEGAL = 1000;

	//set up the initial values
    public TreeManager(AttackCalculator atkCalc, int optimization, AttackModel atk,
    	DefendModel def, ArrayList<AtkVar> situation) {

    	atkModel = atk;
    	defModel = def;
    	calc = atkCalc;
    	optMethod = optimization;
    	sit = new ArrayList<AtkVar>(situation);
    	weapons = atkModel.getWeapons();

    }//end constructor


    //function starts making the tree
    public ArrayList<Node> makeTree(int numFocus) throws NullPointerException{

		//finds out if there are multiple attackers, if it is a combined attack
		if (AtkVar.checkContainsName(sit, AtkVar.MULT_ATKERS))
			multAtkers = true;
		else
			multAtkers = false;

		//set initial attacks
		int[] usedWeapons = prepareWeapons();

		//see if we need heuristics, or if the number of attacks is small enough without it
		useHeuristics = checkNeedHeuristics(usedWeapons, numFocus);


    	System.out.println("Using heuristics: " + useHeuristics);


    	//set the optimal weapon for attacks after initial
    	findOptimalWeapon(sit, usedWeapons, numFocus);


		//add first attack, is removed after damage is recorded
    	sit.add(new AtkVar(AtkVar.FIRST_ATTACK));

		//sets the parent of the entire tree
		parent = new DecisionNode(Node.END, numFocus, new ArrayList<AtkVar>(sit), usedWeapons);

		//create the full tree of attack possibilities
		expandTree(parent, numFocus);

		//find the best path
		ArrayList<Node> newPath = new ArrayList<Node>();
		float expDam = parent.findBestPath(newPath, optMethod);

		//prints out the path of the optimal path
		printNodePath(newPath, expDam);


		//prints out the decisions and numbers
		printResults(newPath, numFocus);
		

		return newPath;

    }//end makeTree


    //checks each terminating node, and chooses 1 for each focus value to continue expanding
    private ArrayList<Node> pruneTree(ArrayList<Node> nodes, int numFocus){

		//table to store the nodes and scores of the current best
    	Node[] table = new Node[numFocus+1];
    	float[] scoreTable = new float[numFocus+1];

    	Arrays.fill(table, null);

		//check each node, if its score is better then the saved best for that focus level, save it
    	for (Node n : nodes){

    		float score = n.getScore(n, 0);

    		if (score > scoreTable[n.getFocus()] && n.isHitChain()){

    			table[n.getFocus()] = n;
    			scoreTable[n.getFocus()] = score;
    		}

    	}

    	ArrayList<Node> newExpanded = new ArrayList<Node>();

    	for (int i = 0; i < table.length; i++)
    		if (table[i] != null)
    			newExpanded.add(table[i]);

		return newExpanded;

    }//end pruneTree


    //creates and expands the tree, also handles pruning if the tree gets too large
	private void expandTree(Node parent, int numFocus){

		doneTree = false;
		int currentDepth = 0;

		ArrayList<Node> newPath;
		nodesToExpand = new ArrayList<Node>();
		nodesToExpand.add(parent);

		//loop through tree creation until finished
		while (!doneTree){

			doneTree = true;

			ArrayList<Node> copy = new ArrayList<Node>(nodesToExpand);

			//create a new list for the storing of the new nodes
			nodesToExpand = new ArrayList<Node>();

			//expand each node that is ready for it
			//goes till it is done or hits max depth
			for (Node n : copy)
				makeBuyNode(n.getFocus(), n, Arrays.copyOf(n.getWeaponCount(), weapons.size()), false, -1, n.getSit(), currentDepth+1);

			//if any nodes hit the depth, prune the array containing the nodes for the best ones, then continue expanding
			if (!doneTree){
				System.out.println("Pruning");

				//first precompute each subtree, so that the nodes don't have to figure out the other paths
				for (int i = 0; i < copy.size(); i++){

					newPath = new ArrayList<Node>();
					copy.get(i).findBestPath(newPath, optMethod);

				}

				//checks each node for the best one at each focus level
				nodesToExpand = pruneTree(nodesToExpand, numFocus);

				//record depth for next nodes to be made
				currentDepth += MAX_DEPTH;

				System.out.println("Done Pruning");
			}


		}


	}//end expandTree

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

	    		DecisionNode newNode = addDecisionNode(Node.BUY, prevNode, false, weaponToUse, weaponToUse, modSit);

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
    private boolean checkAttackNormal(int weaponToUse, boolean doneInits, int curFocus, ArrayList<AtkVar> modSit){

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

    //handles creating all attacks after the initials based on optimal weapon and optimal order
    private void resolveRest(int curFocus, Node prevNode, int[] initWeapons, ArrayList<AtkVar> modSit){

		//records whether or not to boost hit and damage
		boolean[] boostArr = new boolean[2];

		//determine focus used and boosts used
		int focusUsed = getResolveBoosts(modSit, initWeapons, curFocus, boostArr);
		boolean boostHit = boostArr[0];
		boolean boostDam = boostArr[1];


    	//if there is a weapon left to attack with, continue attacking
    	if (optimalWeapon != null)
    		addResolveNodes(curFocus, prevNode, initWeapons, modSit, boostHit, boostDam, focusUsed);


    }//end resolveRest


	//adds the nodes for the resolve attack, then if there is focus left passes on the variables to do it again
    private void addResolveNodes(int curFocus, Node prevNode, int[] initWeapons, ArrayList<AtkVar> modSit,
    							boolean boostHit, boolean boostDam, int focusUsed){

    	int index = weapons.indexOf(optimalWeapon);

	    //record attack
		int[] copyWeapon = addAttackRecord(initWeapons, index);


		//add nodes
		DecisionNode buyNode = addDecisionNode(Node.BUY, prevNode, false, index, index, modSit);

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





	//returns a string with the data of the specific attacks and the overall picture
    public String[] printResults(ArrayList<Node> optimalPath, int curFocus){
    	
    	String[] results = new String[2];


		//total expected damage, total expected dam if all hit, total expected hit, total expected crit
		float[] dataForAll = new float[4];


		results[0] = "";
		
		for (int i = 1; i < optimalPath.size(); i += 3)
			results[0] += printSpecificResult(optimalPath, i, dataForAll);
		
		results[1] = "";


		results[1] += "Total Expected Damage: " + dataForAll[0] + nl;
		results[1] += "Total Expected Damage on All Hit: " + dataForAll[1] + nl;
		results[1] += "Total Expected Number of Hits: " + dataForAll[2] + nl;
		results[1] += "Total Expected Number of Crits: " + dataForAll[3] + nl;
		
		return results;


    }//end printResults


	//goes through a single attack and calculates the values, then prints results, and stores overall values
    private String printSpecificResult(ArrayList<Node> optimalPath, int index, float[] data){

    	Node buyNode = optimalPath.get(index);
    	int weaponIndex = (int)buyNode.getValue();
    	
    	String result = "";

		if (weapons.get(weaponIndex).getHasCA())
			result = printCAAttacks(optimalPath, index, data);
		else{

			//set nodes to null if they may not be initialized
	    	Node hitNode = null;
	    	Node damNode = optimalPath.get(index+2);

			ArrayList<Node> hitNodes = optimalPath.get(index+1).getChildren();

			//find hitNode and critNode
	    	for (Node n : hitNodes)
	    		if (((ResultNode)n).getHitType() == ResultNode.HIT)
	    			hitNode = n;



			//get the values for a normal attack
			float hitChance = hitNode.getValue();
			float hitDamage = damNode.getChild(0).getValue();


			//figure out the crit values, and if there are crits
			float[] critValues = new float[2];

	    	boolean hasCrit = getCritValues(weaponIndex, hitNode, damNode, critValues);

	    	float critChance = critValues[0];
	    	float critDamage = critValues[1];


	    	//if the hitNode is from a resolve path, it hasn't had the hit chance of the crit deducted, so do that her
	    	if (hitNode.getParent().getValue() == 1 && weapons.get(weaponIndex).getCrit())
				hitChance -= critChance;




			//determind damage output
	    	float expDam = hitChance*hitDamage + critChance*critDamage;

	    	float expDamHit = hitDamage*hitChance/(hitChance+critChance)+critDamage*critChance/(hitChance+critChance);



		    result += "Expected damage with weapon: " + weapons.get((int)buyNode.getValue()).getName() + "\n" + nl;
		    result += "Expected Damage: " + expDam + nl;
		    result += "Expected Damage if hit: " + expDamHit + nl;
		    result += "Chance to Hit: " + (hitChance+critChance) + nl;

		   	if (hasCrit)
		   		result += "Chance to Crit: " + critChance + nl;

		   	if (multAtkers){

		   		result += "Expected number of hits: " + (hitChance+critChance)*atkModel.getNumAttackers() + nl;

				if (hasCrit)
					result += "Expected number of crits: " + (critChance)*atkModel.getNumAttackers() + nl;

		   	}



			data[0] += expDam;
			data[1] += expDamHit;
		   	data[2] += hitChance*atkModel.getNumAttackers();
		   	data[3] += critChance*atkModel.getNumAttackers();

		   	result += "\n";

		}
		
		return result;

    }//end printSpecificResult

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




    private String printCAAttacks(ArrayList<Node> optimalPath, int index, float[] data){
    	
    	String result = "";

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

		result += "\nExpected damage with weapon: " + weapons.get((int)buyNode.getValue()).getName() + "\n" + nl;

		result += "Ideal Combined is " + main + " attacks of " + comboNum + nl;

		if (hasLeft)
			result += "Attack of " + atkModel.getNumAttackers()%comboNum + " left over" + nl;

		result += "Expected Damage: " + (damMain*hitChanceMain[0]+damLeft*hitChanceLeft[0]) + nl;
		result += "Expected Damage if hit: " + (damMain+damLeft) + nl;
		result += "Expected number of hits: " + (hitChanceMain[0]*main+hitChanceLeft[0]) + nl;

	    data[0] += damMain*hitChanceMain[0]+damLeft*hitChanceLeft[0];
		data[1] += damMain+damLeft;
	    data[2] += hitChanceMain[0]*main+hitChanceLeft[0];
	    data[3] += 0;


	    return result; 
	    
    }//end printCAAttacks


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

	//properly sets the values for the weapon array that stores number of attacks for each weapon
	private int[] prepareWeapons(){

		//record what initials have been used
    	int[] usedWeapons = new int[weapons.size()];

    	removeIllegalAttacks(usedWeapons);

    	return usedWeapons;

	}//end prepareWeapons


	//turn on heuristics if there is a large number of possibilities
	//check to see if a lot of attacks can be generated
	private boolean checkNeedHeuristics(int[] usedWeapons, int numFocus){

		if (!(checkNumAttacks(usedWeapons)%ILLEGAL > 3)){

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

	//just prints out the path and how it is boosted, redundant
	private void printNodePath(ArrayList<Node> newPath, float expDam){


		System.out.println("Tree Depth: " + (newPath.size()-1)/3);
		System.out.println("Expected damage: " + expDam);

		for (int i = 0; i < newPath.size(); i++){

			if (newPath.get(i).getType() == Node.BUY){
				System.out.println("Buying attack with weapon " + newPath.get(i).getValue());
			}else
				System.out.println(Node.TYPE_ARR[newPath.get(i).getType()-1] + " isBoosted: " + ((DecisionNode)newPath.get(i)).getBoost());

		}

	}//end printNodePath
	
	//just prints out the path and how it is boosted, redundant
		static public String printNodePath(ArrayList<Node> newPath){
			
			System.out.println("Starting to print results");
			
			String result = "";
			result = result + ("Tree Depth: " + (newPath.size()-1)/3);

			for (int i = 0; i < newPath.size(); i++){

				if (newPath.get(i).getType() == Node.BUY){
					result = result + ("Buying attack with weapon " + newPath.get(i).getValue());
				}else
					result = result + (Node.TYPE_ARR[newPath.get(i).getType()-1] + " isBoosted: " + ((DecisionNode)newPath.get(i)).getBoost());

			}
			
			System.out.println("Finished to print results");
			
			return result;

		}//end printNodePath




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