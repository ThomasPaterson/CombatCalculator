package com.tree.combatcalculator;

import java.util.ArrayList;
import java.util.List;

import com.tree.combatcalculator.nodes.Node;

public class AttackManagerCopy {
	
	private static StaticAttackData permData;
	private static List<Node> bottomRow;

	
	
	public static List<Node> addAttack(Node n, StaticAttackData permanentData) {
		
		permData = permanentData;
		bottomRow = new ArrayList<Node>();
		
		addChildNodes(n);
		
		computeScores();
		
		return bottomRow;
		
	}





	private static void computeScores() {
		
		for (Node n : bottomRow){
			n.calculateValue(permData);
		}
	}



	private static void addChildNodes(Node n) {
		
		List<Node> children = n.createChildren(permData);
		
		if (!finishedAttack(children)){
			
			for (Node child : children){
				addChildNodes(child);
			}
		}else{
			bottomRow.add(n);
		}
		
	}





	private static boolean finishedAttack(List<Node> children) {
		
		if (children == null)
			return true;
		else if (children.size() == 0)
			return true;
		else if (children.get(0).getNodeType().equals(Node.TERMINUS_TYPE))
			return true;
		
		return false;
	}

}
