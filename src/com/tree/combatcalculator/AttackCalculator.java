package com.tree.combatcalculator;


/**
 * @(#)AttackCalculator.java
 *
 *
 *returns either expected damage on hit, expected damage on crit, or the various chances to hit in an array
 *
 * @author
 * @version 1.00 2012/10/1
 */

import java.util.ArrayList;

public class AttackCalculator {

	//different methods for calculating damage based on optimization methods
	static public int NORMAL = 0;
	static public int THRESH = 1;
	static public int CRIT = 2;

	//basic stats for the models
	private int baseAtk;
	private int basePow;
	private int baseDef;
	private int baseArm;
	private int baseHealth;

	//modified stats for the models
	private int modAtk;
	private int modPow;
	private int modDef;
	private int modArm;

	//affects number of dice to roll, how to roll them
	private boolean boostedAtk;
	private boolean boostedDam;
	private int addAtk;
	private int addDam;
	private int disAtk;
	private int disDam;
	private boolean rerollAtk;
	private boolean rerollDam;
	private boolean autoHit;
	private boolean autoCrit;
	private boolean critAtk;
	private boolean doubleDamage;

	private DiceHolder dice;


	//type of attack
	private boolean isMelee;

	//hit chance array, format is
	//[chance to hit, chance to miss, chance to crit]
	private double[] hitChance;

	//initializes the dice
    public AttackCalculator(DiceHolder holder) {

    	dice = holder;

    }


	//takes the attacking model, defending model, and the situational variables and returns the expected
	//damage
    public float getExpectedDamage(AttackModel a, DefendModel d, Weapon w, ArrayList<AtkVar> sit){
    	
		float ex = 0;


		initVariables(a, d, w, sit);


		//if it is an invalid attack, return 0 damage
    	if (baseAtk == -1)
    		return 0;

		hitChance = setHitChance();

		//if the weapon can crit, remove the crit chance from the hit chance
		if (w.getCrit())
			hitChance[0] = hitChance[0]-hitChance[2];


		ex = calculateDamage(true);

		//if the weapon can crit, handle that possibility for damage calculation
		if (w.getCrit()){


			ArrayList<AtkVar> newSit = new ArrayList<AtkVar>(sit);
			newSit.add(new AtkVar(AtkVar.CRIT));

			parseVariables(a.getVariables(), d.getVariables(), sit, w, true);


			//determine extra damage for critting
			float critDam = calculateDamage(false);

			ex += critDam*hitChance[2];

			if (critAtk)
				ex += (float) getShredDamage(a, d, w, newSit);

		}//end crit


		System.out.println(ex);

		return ex;



    }//end getExpectedDamage


    //takes the attacking model, defending model, and the situational variables and returns the expected
	//damage, gives the option for using hit chance or not, and optimization methods
    public float getExpectedDamage(AttackModel a, DefendModel d, Weapon w, ArrayList<AtkVar> sit,
    	boolean useHitChance, int optMethod){

		float ex = 0;

		//set up the variables if it won't be doing that later
		if (!(useHitChance && optMethod == NORMAL)){

			initVariables(a, d, w, sit);

			//if it is an invalid attack, return 0 damage
	    	if (baseAtk == -1)
	    		return 0;

		}


		if (useHitChance)
			hitChance = setHitChance();

    	//calculate normal expected damage
    	if (optMethod == NORMAL){

    		if (!useHitChance)
				ex = calculateDamage(useHitChance);
			else
				ex = getExpectedDamage(a, d, w, sit);

			if (critAtk)
				ex += (float) getShredDamage(a, d, w, sit);

		//calculate based on chance to exceed threshold
    	}else if (optMethod == THRESH){

    		ex = calculateExceedThresh(useHitChance);



    	//if just looking for crits, return 1 if it is a crit, or the chance to crit if it is making a full attack
    	}else if (optMethod == CRIT){

    		if (useHitChance)
    			ex = (float)hitChance[2];
    		else if (AtkVar.checkContainsName(sit, AtkVar.CRIT))
    			ex = 1;


    	}

		return ex;


    }//end getExpectedDamage



    //takes the attacking model, defending model, and the situational variables and returns the expected
	//damage, gives the option for using hit chance or not
    public float getExpectedDamageWithCrit(AttackModel a, DefendModel d, Weapon w, ArrayList<AtkVar> sit){

		float ex = getExpectedDamage(a, d, w, sit);

		ex += ex*hitChance[1];

		return ex;


    }//end getExpectedDamage


    //calculating damage for a model with crit shred
    //only go a few attacks deep
    public double getShredDamage(AttackModel a, DefendModel d, Weapon w, ArrayList<AtkVar> sit){

		float ex = 0;

		ArrayList<AtkVar> newSit = AtkVar.removeFocusBoost(sit);

		initVariables(a, d, w, newSit);

		//if it is an invalid attack, return 0 damage
    	if (baseAtk == -1)
    		return 0;

		hitChance = setHitChance();

		ex = calculateDamage(false);

		hitChance[0] = hitChance[0]-hitChance[2];

		return hitChance[2]*(ex*hitChance[0] + ex*hitChance[2] + hitChance[2]*(ex*hitChance[0] + ex*hitChance[2]));


    }//end getExpectedDamage



	//returns the probability of hitting as an array
	//format = [hit chance, miss chance, crit chance]
    public double[] getHitChance(AttackModel a, DefendModel d, Weapon w, ArrayList<AtkVar> sit){

    	initVariables(a, d, w, sit);

    	//if it is an invalid attack, return 0% chance to hit
    	if (baseAtk == -1){

    		double[] temp = {0, 1, 0};
    		return temp;

    	}

    	return setHitChance();

    }//end getHitChance


    //sets the various hit chances based on def, atk, and the autohit variables
    public double[] setHitChance(){


		//handle if it is auto hitting
    	if (autoHit){


    		double[] tempHitChance = {1, 0, 0};

    		return tempHitChance;

    	}else if (autoCrit){

    		double[] tempHitChance = {1, 0, 1};

    		return tempHitChance;

		//otherwise roll
    	}else{

			int numToRoll = getNumAtkRoll();

    		double[] temp = dice.rollToHit(modAtk, modDef, numToRoll - disAtk, disAtk);

			//if reroll, the hit chance is 1 - the chance of missing twice
			if (rerollAtk)
				temp[0] = 1-((1-temp[0]) * (1-temp[0]));


    		double[] tempHitChance = {temp[0], 1-temp[0], temp[1]};

    		return tempHitChance;

    	}



    }//end setHitChance

    //gets the number of dice to roll for the attack
    private int getNumAtkRoll(){

    	int num = 2 + addAtk;

    	if (boostedAtk)
    		num++;

		return num;

    }//end getNumToRoll

   	//gets the number of dice to roll for the attack
    private int getNumDamRoll(){

    	int num = 2 + addDam;

    	if (boostedDam)
    		num++;

		return num;

    }//end getNumToRoll


	//calculates the damage given the pow and arm and dice to roll
	//this does not worry about crits
    private float calculateDamage(boolean useHitChance){

    	int numDice = getNumDamRoll();

		//roll for damage
    	float damage = dice.rollToDamage(modPow, modArm, numDice - disDam, disDam);

		//double damage if requested
		if (doubleDamage)
			damage *= 2;

		//if we aren't assuming auto hit, then modify by chance to hit
		if (useHitChance)
			damage *= hitChance[0];


		return damage;

    }//end calculate damage


	//gives the percentage chance of killing a model with one attack
    private float calculateExceedThresh(boolean useHitChance){

		int numDice = getNumDamRoll();

		float toKill = dice.rollToExceed(modPow, modArm+baseHealth, numDice - disDam, disDam);

		if (useHitChance)
			toKill *= hitChance[0];

		if (toKill > 0.999999)
			toKill = 1;

		return toKill;

    }//end calculateExceedThresh




    //calculates the damage given the pow and arm and dice to roll
    private float calculateDamage(boolean useHitChance, boolean useCrit){

    	int numDice = getNumDamRoll();

		//roll for damage
    	float damage = dice.rollToDamage(modPow, modArm, numDice - disDam, disDam);

		//double damage if requested
		if (doubleDamage)
			damage *= 2;

		//if we have to roll to hit,
		if (useHitChance)
			damage *= hitChance[0];


		return damage;

    }//end calculate damage

    //initilizes the variables for calculations
    private void initVariables(AttackModel a, DefendModel d, Weapon w, ArrayList<AtkVar> sit){

		//set the base variables
    	boostedAtk = false;
		boostedDam = false;
		rerollAtk = false;
		rerollDam = false;
		autoHit = false;
		doubleDamage = false;
		critAtk = false;
		addAtk = 0;
		addDam = 0;
		disAtk = 0;
		disDam = 0;
		hitChance = new double[3];


		ArrayList<AtkVar> attackVars = a.getVariables();
    	ArrayList<AtkVar> defendVars = d.getVariables();

    	baseAtk = setBaseAttack(w, sit, a);




		//get other basic variables
		basePow = w.getPow();
		baseDef = d.getDef();
		baseArm = d.getArm();
		baseHealth = d.getHealth();

		//set the mod variables to the base, before modification
		modAtk = baseAtk;
		modPow = basePow;
		modDef = baseDef;
		modArm = baseArm;

		//add in the variable effects
		parseVariables(attackVars, defendVars, sit, w, false);


    }//end initVariables


    //goes through all the variables and handles testing conditions, adding values
    private void parseVariables(ArrayList<AtkVar> atk, ArrayList<AtkVar> def, ArrayList<AtkVar> sit, Weapon w, boolean checkCrit){


		ArrayList<AtkVar> weapon = w.getVariables();

    	for (AtkVar a : atk)
    		handleVariable(a, atk, def, sit, w, checkCrit);

    	for (AtkVar d : def)
    		handleVariable(d, atk, def, sit, w, checkCrit);

    	for (AtkVar s : sit)
    		handleVariable(s, atk, def, sit, w, checkCrit);

    	for (AtkVar we : weapon)
    		handleVariable(we, atk, def, sit, w, checkCrit);


    }//end parseVariables




    //handles specific variables
    private void handleVariable(AtkVar n, ArrayList<AtkVar> atk, ArrayList<AtkVar> def, ArrayList<AtkVar> sit, Weapon w, boolean checkCrit){

		int val = n.getValue();

		boolean meetsCond = n.meetsConditions(sit);


		//if it meets all conditions (normally none) continue handling
		if ((meetsCond && !checkCrit) || (meetsCond && checkCrit && n.getConditions().indexOf(AtkVar.GET_CRIT) > -1)){

			if (checkCrit)
				System.out.println(n.getName());
			//boosting attack rolls
			if (n.getType().equals(AtkVar.BOOST)){

				if (n.getTarget().equals(AtkVar.ATK_ROLL)){

					boostedAtk = true;

				//boosting damage rolls, if it is from a charge, only do so if the weapon is melee
				}else if (n.getTarget().equals(AtkVar.DAM_ROLL)){

					if (n.getName().equals(AtkVar.CHARGE_DAMAGE)){

						if (!w.getRanged())
							boostedDam = true;


					}else{
						boostedDam = true;
					}

				}


			//add additional dice
			}else if (n.getType().equals(AtkVar.ADD)){

				if (n.getTarget().equals(AtkVar.ATK_ROLL))
					addAtk += val;
				else if (n.getTarget().equals(AtkVar.DAM_ROLL))
					addDam += val;

			//modify a stat
			}else if (n.getType().equals(AtkVar.MOD)){

				if (n.getTarget().equals(AtkVar.ATK))
					modAtk += val;
				else if (n.getTarget().equals(AtkVar.POW))
					modPow += val;
				else if (n.getTarget().equals(AtkVar.DEF))
					modDef += val;
				else if (n.getTarget().equals(AtkVar.ARM))
					modArm += val;

			//reroll an attack or damage roll
			}else if (n.getType().equals(AtkVar.REROLL)){

				if (n.getTarget().equals(AtkVar.ATK_ROLL))
					rerollAtk = true;
				else if (n.getTarget().equals(AtkVar.DAM_ROLL))
					rerollDam = true;

			//discard a number of additional dice
			}else if (n.getType().equals(AtkVar.DISCARD)){

				if (n.getTarget().equals(AtkVar.ATK_ROLL))
					disAtk += val;
				else if (n.getTarget().equals(AtkVar.DAM_ROLL))
					disDam += val;


			//handle special cases
			}else if (n.getType().equals(AtkVar.SPECIAL)){


				if (n.getName().equals(AtkVar.ARM_PIERCING))
					modArm -= Math.ceil((float)baseArm/2);
				else if (n.getName().equals(AtkVar.DOUBLE_DAMAGE))
					doubleDamage = true;
				else if (n.getName().equals(AtkVar.AUTO_HIT))
					autoHit = true;
				else if (n.getName().equals(AtkVar.AUTO_CRIT))
					autoCrit = true;
				else if (n.getName().equals(AtkVar.CRIT_ATK))
					critAtk = true;
				else if (n.getName().equals(AtkVar.SUSTAINED_ATK) && AtkVar.checkContainsName(w.getVariables(), AtkVar.SUSTAINED_ATK_AB))
						autoHit = true;
				else if (n.getName().equals(AtkVar.POWERFUL_ATK)){


					boostedAtk = true;
					boostedDam = true;

				}else if (n.getName().equals(AtkVar.KNOCKDOWN)){

					if (AtkVar.checkContainsName(sit, AtkVar.RANGED))
						modDef -= baseDef-5;
					else
						autoHit = true;

				}

			}



		}





    }//end parseVariables



    //handles setting the base attack based on if it is a ranged attack, or melee attack with guns, or normal
    //returns -1 if it is an invalid attack
    public int setBaseAttack(Weapon w, ArrayList<AtkVar> sit, AttackModel a){

    	ArrayList<AtkVar> attackVars = a.getVariables();

		//set if it is a ranged attack or not
		if (AtkVar.checkContainsName(sit, AtkVar.RANGED))
			isMelee = false;
		else
			isMelee = true;
		
		System.out.println("a Attack is melee: " + isMelee);

		//default value, if not set to another value attack is invalid
    	int curAttack = -1;


    	//check to see if the ranged weapon can attack
    	if (w.getRanged()){

    		//if it is a ranged attack, use rat
    		if (!isMelee){
    			curAttack = a.getRat();

    		//if it is in melee and gunfither, use rat
    		}else if (AtkVar.checkContainsName(attackVars, AtkVar.GUNFIGHTER)){
    			curAttack = a.getRat();

    		//if it is in melee and point blank, use mat
    		}else if (AtkVar.checkContainsName(attackVars, AtkVar.POINT_BLANK)){
    			curAttack = a.getMat();
    		}


		//if it is a melee weapon
    	}else{

    		//if it is not ranged, use mat
    		if (isMelee)
    			curAttack = a.getMat();

    	}
    	
    	System.out.println("a Attack is from a melee weapon: " + !w.getRanged());
    	
    	System.out.println("a Attack is : " + curAttack);

		return curAttack;

	}//end setBaseAttack

	//determines if a weapon/model in a specific situation is a legal attack
	static public boolean legalAttack(Weapon w, ArrayList<AtkVar> sit, AttackModel a){


		ArrayList<AtkVar> attackVars = a.getVariables();
		boolean melee;

		//default value, if not set to another value attack is invalid
		boolean legal = false;

		//set if it is a ranged attack or not
		melee = !AtkVar.checkContainsName(sit, AtkVar.RANGED);






    	//check to see if the ranged weapon can attack
    	if (w.getRanged()){

    		//if it is a ranged attack, use rat
    		if (!melee){
    			legal = true;

    		//if it is in melee and gunfither, use rat
    		}else if (AtkVar.checkContainsName(attackVars, AtkVar.GUNFIGHTER)){
    			legal = true;

    		//if it is in melee and point blank, use mat
    		}else if (AtkVar.checkContainsName(attackVars, AtkVar.POINT_BLANK)){
    			legal = true;
    		}


		//if it is a melee weapon
    	}else{

    		//if it is not ranged, use mat
    		if (melee)
    			legal = true;

    	}

    	System.out.println("legal attack: " + legal);

		return legal;



	}//end legalAttack


    }


