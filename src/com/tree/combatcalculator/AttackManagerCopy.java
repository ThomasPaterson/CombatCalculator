package com.tree.combatcalculator;

import java.util.ArrayList;
import java.util.List;

import com.tree.combatcalculator.nodes.Node;

public class AttackManagerCopy {
	
	private static PermanentTreeData permData;
	private static List<Node> bottomRow;

	
	
	public static List<Node> addAttack(Node n, PermanentTreeData permanentData) {
		
		permData = permanentData;
		bottomRow = new ArrayList<Node>();
		
		addChildNodes(n);
		
		computeScores();
		
		return bottomRow;
		
	}



	private static void computeScores() {
		
		for (Node n : bottomRow){
			n.calculateValue();
		}
	}



	private static void addChildNodes(Node n) {
		
		List<Node> children = n.createChildren(permData);
		
		if (n.getNodeType().getNext() != null){
			
			for (Node child : children){
				addChildNodes(child);
			}
		}else{
			bottomRow.add(n);
		}
		
	}

}
