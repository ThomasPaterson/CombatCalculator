package com.tree.combatcalculator.Dice;

/**
 * @(#)DiceHolder.java
 *
 *
 *holds the dice, handles converting the number of dice/additional dice and stats into
 *either threshold or expected damage checks for the correct dice number
 *
 *
 * @author
 * @version 1.00 2012/9/11
 */


public class DiceHolder {

	DiceCalculator[][] diceBag;

	public DiceHolder() {

		diceBag = new DiceCalculator[7][3];
	}


	//adds in new dice to the dicebag
	public void addDice(int numDice, int numAdd, DiceCalculator newDice){

		diceBag[numDice][numAdd] = newDice;
	}//end addDice

	//returns the toHit chance and toCrit chance
	//numAdd is the number of dice to roll extra and discard
	public HitRoll rollToHit(int attack, int defense, int numDice, int numAdd, boolean reroll){

		HitRoll hitRoll = diceBag[numDice-1][numAdd].getHitChance(defense-attack, reroll);

		return hitRoll;


	}//end rollToHit

	//returns the chance to exceed a target roll
	public float rollToExceed(int power, int armor, int numDice, int numAdd){

		float result = diceBag[numDice-1][numAdd].getExceedChance(armor-power);

		return result;


	}//end rollToHit

	//returns the toHit chance and toCrit chance
	public float rollToDamage(int power, int armor, int numDice, int numAdd, boolean reroll){

		float result = diceBag[numDice-1][numAdd].getDamage(armor-power, reroll);


		return result;


	}//end rollToHit


}