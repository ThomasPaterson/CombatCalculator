package com.tree.combatcalculator.Dice;

/**
 * @(#)DiceCalculator.java
 *
 *calculates the value for the dice it holds for binary conditions
 *and exceeding value conditions
 *
 * @author
 * @version 1.00 2012/9/10
 */

import java.util.ArrayList;

public class DiceCalculator {

	private ArrayList<Double> dice ;
	private ArrayList<Double> crits;
	private int numDice;


	public DiceCalculator() {


	}

	//sets the number of dice, stores the dice and crit arrays
	public void setValues(ArrayList<Double> newDice, ArrayList<Double> newCrits){

		dice = newDice;
		crits = newCrits;

		int checkSize = 6;

		//loop through to find the correct number of dice
		for (int i = 1; i < 8; i++){

			if (dice.size() == checkSize){
				numDice = i;
				break;

			}else
				checkSize += 5;


		}


	}//end setList

	//returns the expected damage given the number to exceed, for the dice amount this uses
	public float getDamage(int numToExceed, boolean reroll){

		float expectedDamage = 0;

		//don't bother checking if it can't damage
		if (numToExceed - numDice < dice.size())
			return 0;


		//convert numToExceed to array location
		int startLoc = numToExceed - numDice;

		//if all rolls are damage, just start at 0
		int bottomValue = (startLoc < 0 ? 0 : startLoc + 1);

		for (int i = bottomValue; i < dice.size(); i++)
			expectedDamage += (i-startLoc) * dice.get(i);
		//otherwise start when damage starts


		//if you reroll, has to calculate again, knowing what the average is
		if (reroll)
			expectedDamage = rerollDamage(numToExceed, startLoc, expectedDamage);


		return expectedDamage;
	}//end getDamage


	private float rerollDamage(int numToExceed, int startLoc, float expectedDamage) {

		float overAverageDamage = 0;
		float chanceForBelowAverage = 0;

		int bottomValue = (startLoc < 0 ? 0 : startLoc + 1);

		for (int i = bottomValue; i < dice.size(); i++){
			//if the damage exceeds the average, calculate it
			if (i-startLoc >= expectedDamage)
				overAverageDamage += (i-startLoc) * dice.get(i);
			//otherwise, count the possibility of getting below the average
			else
				chanceForBelowAverage += dice.get(i);


		}

		return chanceForBelowAverage * expectedDamage + overAverageDamage;
	}

	//returns the hit chance and crit chance based on the difference between the atk and def values
	//all 6's always hit, all 1's always misses
	public HitRoll getHitChance(int numToExceed, boolean reroll){

		//struct of array = [toHit, toCrit]
		HitRoll hitRoll = new HitRoll();

		//convert numToExceed to array location
		int startLoc = numToExceed - numDice;


		//don't bother checking if it can't hit
		if (startLoc <= dice.size()){

			//if all rolls hit, make all 1s still miss
			if (startLoc <= 0)
				startLoc = 1;

			for (int i = startLoc; i < dice.size()-1; i++){
				hitRoll.hitChance += dice.get(i);
				hitRoll.critChance += crits.get(i);

			}



		}

		//cover the chance of all 6s
		hitRoll.hitChance += dice.get(dice.size()-1);
		hitRoll.critChance += crits.get(dice.size()-1);


		if (reroll){
			hitRoll.critChance += (1-hitRoll.hitChance)*hitRoll.critChance;
			hitRoll.hitChance += (1-hitRoll.hitChance)*hitRoll.hitChance;
		}

		return hitRoll;

	}//end getHitChance


	//returns the chance for an attack to exceed a target's health + arm
	public float getExceedChance(int numToExceed){

		//struct of array = [toHit, toCrit]
		float hitChance = 0;

		//convert numToExceed to array location
		int startLoc = numToExceed - numDice;


		//don't bother checking if it can't hit
		if (startLoc <= dice.size()){

			//if all rolls hit
			if (startLoc <= 0){

				//skip first location, so that all 1s always misses
				for (int i = 0; i < dice.size(); i++)
					hitChance += dice.get(i);




				//otherwise start when damage starts
			}else{

				for (int i = startLoc; i < dice.size()-1; i++)
					hitChance += dice.get(i);


			}

		}


		return hitChance;

	}//end getHitChance

	//for bug testing
	public void printList(){

		System.out.println("Number of dice: " + numDice + "\n");

		for (int i = 0; i < dice.size(); i++)
			System.out.println(dice.get(i));

	}//end printList


}