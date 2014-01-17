package com.tree.combatcalculator.nodes;

import java.util.ArrayList;
import java.util.List;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.AtkVarCopy;
import com.tree.combatcalculator.AttackCalcHelper;
import com.tree.combatcalculator.PermanentTreeData;
import com.tree.combatcalculator.Weapon;
import com.tree.combatcalculator.WeaponCountHolder;

public class AttackResNode extends ResultNode {
	
	public static boolean CRIT = true;
	public static boolean NORMAL = false;

	public AttackResNode(Node parent) {
		
		super(parent);
		nodeType = Node.Type.ATTACK_DEC;

	}

	
	public static List<Node> createAttackResNodes(Node parent, PermanentTreeData permData) {
		
		List<Node> attackResNodes = new ArrayList<Node>();
		
		
		
		if (hasCrit(parent, permData)){
			attackResNodes.add(makeAttackResult(parent, CRIT, permData));
		}
		
		attackResNodes.add(makeAttackResult(parent, NORMAL, permData));
		
		
		return attackResNodes;	
	}

	private static boolean hasCrit(Node parent, PermanentTreeData permData) {
		
		Weapon w = permData.attacker.getWeapons().get(parent.getWeaponIndex());
		
		return w.getCrit();
	}


	private static Node makeAttackResult(Node parent, boolean hasCritical, PermanentTreeData permData) {

		Node attackResNode = new AttackResNode(parent);
		
		if (hasCritical){
			attackResNode.getTempData().variables.put(
					AtkVarCopy.Id.CRIT, new AtkVarCopy(AtkVarCopy.Id.CRIT));
			attackResNode.getTempData().variables.put(
					AtkVarCopy.Id.CRIT_PATH, new AtkVarCopy(AtkVarCopy.Id.CRIT_PATH));
		}
		
		attackResNode.setValue(AttackCalcHelper.calcHitProbability(attackResNode, permData));
		
		
		return attackResNode;
	}




	@Override
	public void calculateValue() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public List<Node> createChildren(PermanentTreeData permData) {
		// TODO Auto-generated method stub
		return DamageDecNode.createDamageDecNodes(this, permData);
	}

	




}
