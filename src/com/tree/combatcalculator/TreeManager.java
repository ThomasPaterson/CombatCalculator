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
import java.util.Collections;
import java.util.List;

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
	
	static final private int FIRST_ATTACK = 1;

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


	

	

    


	


    

	


    





    

	






    



    







    

    

		
	
    


	


	


//
//    private String printCAAttacks(ArrayList<Node> optimalPath, int index, float[] data){
//    	
//    	String result = "";
//
//    	double[] hitChanceLeft;
//    	float damLeft;
//
//    	Node buyNode = optimalPath.get(index);
//    	int weaponIndex = (int)buyNode.getValue();
//
//    	int comboNum = ((DecisionNode)buyNode).getComboNum();
//
//    	boolean hasLeft = false;
//
//    	if (atkModel.getNumAttackers()%comboNum != 0)
//    		hasLeft = true;
//
//    	Node damNode = optimalPath.get(index+2);
//
//
//    	ArrayList<AtkVar> mainSit = new ArrayList<AtkVar>(damNode.getChild(0).getSit());
//		ArrayList<AtkVar> leftSit = new ArrayList<AtkVar>(damNode.getChild(0).getSit());
//
//		addCABonuses(mainSit, leftSit, atkModel.getNumAttackers(), comboNum);
//
//		int main = (int)Math.floor((float)atkModel.getNumAttackers()/comboNum);
//
//		//get the values together for calculation
//    	double[] hitChanceMain = calc.getHitChance(atkModel, defModel, weapons.get(weaponIndex), mainSit);
//
//
//
//		//add the damage from the main attacks and left over
//		float damMain = calc.getExpectedDamage(atkModel, defModel, weapons.get(weaponIndex), mainSit, false, optMethod)*main;
//
//		if (hasLeft){
//
//			hitChanceLeft = calc.getHitChance(atkModel, defModel, weapons.get(weaponIndex), leftSit);
//			damLeft = calc.getExpectedDamage(atkModel, defModel, weapons.get(weaponIndex), leftSit, false, optMethod);
//
//		}else{
//			hitChanceLeft = new double[3];
//			damLeft = 0;
//		}
//
//		result += "\nExpected damage with weapon: " + weapons.get((int)buyNode.getValue()).getName() + "\n" + nl;
//
//		result += "Ideal Combined is " + main + " attacks of " + comboNum + nl;
//
//		if (hasLeft)
//			result += "Attack of " + atkModel.getNumAttackers()%comboNum + " left over" + nl;
//
//		result += "Expected Damage: " + (damMain*hitChanceMain[0]+damLeft*hitChanceLeft[0]) + nl;
//		result += "Expected Damage if hit: " + (damMain+damLeft) + nl;
//		result += "Expected number of hits: " + (hitChanceMain[0]*main+hitChanceLeft[0]) + nl;
//
//	    data[0] += damMain*hitChanceMain[0]+damLeft*hitChanceLeft[0];
//		data[1] += damMain+damLeft;
//	    data[2] += hitChanceMain[0]*main+hitChanceLeft[0];
//	    data[3] += 0;
//
//
//	    return result; 
//	    
//    }//end printCAAttacks




	





//	//just prints out the path and how it is boosted, redundant
//	private void printNodePath(ArrayList<Node> newPath, float expDam){
//
//
//		System.out.println("Tree Depth: " + (newPath.size()-1)/3);
//		System.out.println("Expected damage: " + expDam);
//
//		for (int i = 0; i < newPath.size(); i++){
//
//			if (newPath.get(i).getType() == Node.BUY){
//				System.out.println("Buying attack with weapon " + newPath.get(i).getValue());
//			}else
//				System.out.println(Node.TYPE_ARR[newPath.get(i).getType()-1] + " isBoosted: " + ((DecisionNode)newPath.get(i)).getBoost());
//
//		}
//
//	}//end printNodePath
	
//	//just prints out the path and how it is boosted, redundant
//		static public String printNodePath(ArrayList<Node> newPath){
//			
//			System.out.println("Starting to print results");
//			
//			String result = "";
//			result = result + ("Tree Depth: " + (newPath.size()-1)/3);
//
//			for (int i = 0; i < newPath.size(); i++){
//
//				if (newPath.get(i).getType() == Node.BUY){
//					result = result + ("Buying attack with weapon " + newPath.get(i).getValue());
//				}else
//					result = result + (Node.TYPE_ARR[newPath.get(i).getType()-1] + " isBoosted: " + ((DecisionNode)newPath.get(i)).getBoost());
//
//			}
//			
//			System.out.println("Finished to print results");
//			
//			return result;
//
//		}//end printNodePath




	





}