package com.tree.combatcalculator;

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
    public float getDamage(int numToExceed){

		float expectedDamage = 0;

		//don't bother checking if it can't damage
		if (numToExceed - numDice < dice.size()){

			//convert numToExceed to array location
			int startLoc = numToExceed - numDice;

			//if all rolls are damage, just start at 0
			if (startLoc < 0){

				for (int i = 0; i < dice.size(); i++)

					expectedDamage += (i-startLoc) * dice.get(i);
			//otherwise start when damage starts
			}else{

				for (int i = startLoc+1; i < dice.size(); i++)
					expectedDamage += (i-startLoc) * dice.get(i);
			}

		}


		return expectedDamage;
    }//end getDamage


	//returns the hit chance and crit chance based on the difference between the atk and def values
	//all 6's always hit, all 1's always misses
    public double[] getHitChance(int numToExceed){

    	//struct of array = [toHit, toCrit]
    	double[] hitChance = {0, 0};

    	//convert numToExceed to array location
    	int startLoc = numToExceed - numDice;


		//don't bother checking if it can't hit
		if (startLoc <= dice.size()){

			//if all rolls hit
			if (startLoc <= 0){

				//skip first location, so that all 1s always misses
				for (int i = 1; i < dice.size()-1; i++){
					hitChance[0] = hitChance[0] + dice.get(i);
					hitChance[1] = hitChance[1] + crits.get(i);

				}


			//otherwise start when damage starts
			}else{

				for (int i = startLoc; i < dice.size()-1; i++){
					hitChance[0] = hitChance[0] + dice.get(i);
					hitChance[1] = hitChance[1] + crits.get(i);

				}

			}

		}

		//cover the chance of all 6s
		hitChance[0] = hitChance[0] + dice.get(dice.size()-1);
		hitChance[1] = hitChance[1] + crits.get(dice.size()-1);


		return hitChance;

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