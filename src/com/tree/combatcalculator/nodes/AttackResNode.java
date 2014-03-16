package com.tree.combatcalculator.nodes;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.AtkVarCopy;
import com.tree.combatcalculator.AttackCalcHelper;
import com.tree.combatcalculator.PermanentTreeData;
import com.tree.combatcalculator.Weapon;
import com.tree.combatcalculator.WeaponCountHolder;

public class AttackResNode extends ResultNode {

	public AttackResNode(Node parent) {
		
		super(parent);
		nodeType = Node.Type.ATTACK_RES;

	}

	
	public AttackResNode(Parcel in) {
		super(in);
		nodeType = Node.Type.ATTACK_RES;
	}


	public static List<Node> createAttackResNodes(Node parent, PermanentTreeData permData) {
		
		List<Node> attackResNodes = new ArrayList<Node>();
		
		attackResNodes.add(makeAttackResult(parent, permData));
		
		
		return attackResNodes;	
	}

	private static boolean hasCrit(Node parent, PermanentTreeData permData) {
		
		Weapon w = permData.attacker.getWeapons().get(parent.getWeaponIndex());
		
		return w.getCrit();
	}


	private static Node makeAttackResult(Node parent, PermanentTreeData permData) {

		Node attackResNode = new AttackResNode(parent);
		
		
		attackResNode.getTempData().putCrits(attackResNode, permData);
			
		
		return attackResNode;
	}




	public float calculateValue() {

		return value;
		
	}


	@Override
	public List<Node> createChildren(PermanentTreeData permData) {
		// TODO Auto-generated method stub
		return DamageDecNode.createDamageDecNodes(this, permData);
	}



	@Override
	public float calculateValue(PermanentTreeData permData) {
		// TODO Auto-generated method stub
		return 0;
	}





	




}
