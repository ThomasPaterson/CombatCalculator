package com.tree.combatcalculator;

/**
 * @(#)PathFinder.java
 *
 * PathFinder application, currently holds the main file and the test values
 *
 * @author
 * @version 1.00 2011/11/21
 */

import java.util.ArrayList;


public class PathFinder {

	private static String configFile = "config2.xml";


    public static void main(String[] args) {

		DiceReader reader = new DiceReader();
		PathFinder gene = new PathFinder();
		AttackCalculator atkCalc = null;


		try {
      		//gener.startWriter(configFile);
    	} catch (Exception e) {
      		//e.printStackTrace();
    	}


    	try {
      		atkCalc = reader.readFile(configFile);
    	} catch (Exception e) {
      		e.printStackTrace();
    	}

		if (!atkCalc.equals(null))
			gene.testAttacks(atkCalc);
		else
			System.out.println("Error in attack calculator creation");

	}



	private void testAttacks(AttackCalculator atkCalc){

		ArrayList<AtkVar> weaponVars = new ArrayList<AtkVar>();
		//weaponVars.add(new AtkVar(AtkVar.DOUBLE_DAMAGE));
		//weaponVars.add(new AtkVar(AtkVar.REROLL_ATK));
		//weaponVars.add(new AtkVar(AtkVar.CRIT_DAM));
		//weaponVars.add(new AtkVar(AtkVar.CRIT_KNOCKDOWN));
		//weaponVars.add(new AtkVar(AtkVar.ADD_HIT, 1));
		//weaponVars.add(new AtkVar(AtkVar.DISCARD_ATK, 1));
		//weaponVars.add(new AtkVar(AtkVar.CRIT_ATK));
		//AtkVar newVar = new AtkVar(AtkVar.ARM_PIERCING);
		//newVar.addCondition(AtkVar.IS_FIRST_ATTACK);
		//weaponVars.add(new AtkVar(AtkVar.POWERFUL_ATK_AB);
		//weaponVars.add(new AtkVar(AtkVar.SUSTAINED_ATK_AB));
		//weaponVars.add(new AtkVar(AtkVar.BOOSTED_HIT));
		//weaponVars.add(new AtkVar(AtkVar.BOOSTED_DAM));
		//weaponVars.add(newVar);

		Weapon w = new Weapon(14, false, weaponVars, true, -1, 1);
		w.setName("Tail Strike");
		//w.setHasCA(true);

		ArrayList<AtkVar> weaponVars2 = new ArrayList<AtkVar>();
		//weaponVars2.add(new AtkVar(AtkVar.CRIT_DAM));
		//weaponVars2.add(new AtkVar(AtkVar.BOOSTED_DAM));
		//weaponVars2.add(new AtkVar(AtkVar.CRIT_KNOCKDOWN));
		Weapon wa = new Weapon(11, false, weaponVars2, false, -1, 1);
		wa.setName("Bite");

		ArrayList<AtkVar> weaponVars3 = new ArrayList<AtkVar>();
		//weaponVars2.add(new AtkVar(AtkVar.CRIT_DAM));
		//weaponVars2.add(new AtkVar(AtkVar.CRIT_KNOCKDOWN));
		Weapon w3 = new Weapon(20, false, weaponVars3, false, -1, 1);


		ArrayList<AtkVar> attackVars = new ArrayList<AtkVar>();
		//attackVars.add(new AtkVar(AtkVar.WEAPON_MASTER));
		//attackVars.add(new AtkVar(AtkVar.FREE_CHARGE));
		AttackModel a = new AttackModel(6, 6, attackVars, 1);
		a.setName("Angelius");
		a.addWeapon(w);
		//a.addWeapon(w);
		//a.addWeapon(wa);
		//a.addWeapon(w3);



		DefendModel d = new DefendModel(12, 19, 2);
		d.setName("Enemy Angel");

		ArrayList<AtkVar> sit = new ArrayList<AtkVar>();
		//sit.add(new AtkVar(AtkVar.RANGED));
		//sit.add(new AtkVar(AtkVar.CHARGING));
		//sit.add(new AtkVar(AtkVar.CHARGE_DAMAGE));
		//sit.add(new AtkVar(AtkVar.MULT_ATKERS));
		//sit.add(new AtkVar(AtkVar.BOOSTED_HIT));

		//float damage = atkCalc.getExpectedDamage(a, d, w, sit);

	//	System.out.println("expected damage of mat 6 pow 10 vs def 12 arm 16 = " + damage);

		//double[] hitChance = atkCalc.getHitChance(a, d, w, sit);

		//System.out.println("expected chance to hit of mat 6 pow 10 vs def 12 arm 16 = " + hitChance[0]);


		TreeManager tree = new TreeManager(atkCalc, 0, a, d, sit);
		tree.makeTree(12);


	}//end testAttacks

  		}







