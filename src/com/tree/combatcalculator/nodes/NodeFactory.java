package com.tree.combatcalculator.nodes;

import com.tree.combatcalculator.nodes.Node;

import android.os.Parcel;

public class NodeFactory {

	public static Node createNode(Parcel in, Node.Type type) {
		

		switch(type) {
		
			case END: 
				return (Node) new EndNode(in);
				
			case BUY: 
				return (Node) new BuyNode(in);
				
			case ATTACK_DEC: 
				return (Node) new AttackDecNode(in);
				
			case ATTACK_RES: 
				return (Node) new AttackResNode(in);
				
			case DAMAGE_DEC: 
				return (Node) new DamageDecNode(in);
				
			case DAMAGE_RES: 
				return (Node) new DamageResNode(in);
				
			case FINAL_RES: 
				return (Node) new FinalResNode(in);
		
			default:
				throw new IllegalArgumentException();
		
		}
		
	}
	
	
	
	

}
