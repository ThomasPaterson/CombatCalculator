package com.tree.combatcalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tree.combatcalculator.nodes.DecisionNodeCopy;
import com.tree.combatcalculator.nodes.EndNode;
import com.tree.combatcalculator.nodes.Node;

/**
 * @(#)TreeManager.java
 *
 *Creates the tree, then manages it, getting the results requested, then returning the information gathered
 * @author
 * @version 1.00 2012/10/15
 */


public class TreeManagerCopy {
	
private PermanentTreeData permData;
private Node parent;
private List<Node> optimalNodes;
	

	//set up the initial values
    public TreeManagerCopy(AttackCalculator atkCalc, int optimization, AttackModel attacker,
    	DefendModel defender, List< Map<String, AtkVar> > permState) {
    	
    	permData = new PermanentTreeData();

    	permData.atkCalc = atkCalc;
    	permData.optimization = optimization;
    	permData.attacker = attacker;
    	permData.defender = defender;
    	permData.permState = permState;
    	permData.optimalWeapons = permData.getOptimalWeapons();

    }//end constructor


    //function starts making the tree
    public boolean makeTree(int numFocus) throws NullPointerException{

    	permData.useHeuristics = DecisionManager.checkNeedHeuristics(permData.permState,numFocus);
    	WeaponCountHolder usedWeapons = new WeaponCountHolder(permData.attacker.getWeapons(), permData);
    	
    	Map<AtkVarCopy.Id, AtkVarCopy> tempState = AtkVarCopy.setupTempState(permData.permState);
    	
    	parent = new EndNode(Node.END, numFocus, tempState, usedWeapons);
    	
    	optimalNodes = new ArrayList<Node>();
    	optimalNodes.add(parent);
    	
    	expandTree();
    	
    	return true;
    }//end makeTree
    
    
    
    //creates and expands the tree, also handles pruning if the tree gets too large
	private void expandTree(){
		
		boolean done = true;
		
		for (Node n : optimalNodes){
			
			if (n.getFocus() != 0 || n.getWeaponCount().hasAttacks()){
				AttackManagerCopy.addAttack(n, permData);
				done = false;
			}		
		}
		
		pruneTree();

		if (!done)
			expandTree();

	}//end expandTree



	//checks each terminating node, and chooses 1 for each state
    private void pruneTree(){
    	
    	List<Node> replacements = new ArrayList<Node>();
    	
    	Map<Integer, List<Node>> stateBucket = createStateBucket();
    	
    	Set<Integer> keys = stateBucket.keySet();
    	
    	for (Integer key : keys) {
    		List<Node> bucket = stateBucket.get(key);
    		replacements.add(Collections.max(bucket));
    	}
		
    	optimalNodes = replacements;

    }//end pruneTree
    
    
    private Map<Integer, List<Node>> createStateBucket(){
    	
    	Map<Integer, List<Node>> stateBucket = new HashMap<Integer, List<Node>>();
    	
    	//split the nodes up into a number of buckets, for each possible state
    	for (Node n : optimalNodes){
    		
    		Object[] holder = {n.getFocus(), n.getWeaponCount(), n.getTempState()};
    		int nodeHash = holder.hashCode();
    		
    		List<Node> bucket = stateBucket.get(nodeHash);
    		
    		if (bucket == null){
    			bucket = new ArrayList<Node>();
    			stateBucket.put(nodeHash, bucket);
    		}
    		
    		bucket.add(n);
    	}
    	
    	return stateBucket;
    	
    }

    


	



	

	

    


	


    

	


    





    

	






    



    







    

    

		
	
    


	


	


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