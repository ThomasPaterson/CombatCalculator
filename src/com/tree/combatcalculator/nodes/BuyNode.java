package com.tree.combatcalculator.nodes;

import java.util.ArrayList;
import java.util.List;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.AtkVarCopy;
import com.tree.combatcalculator.PermanentTreeData;
import com.tree.combatcalculator.Weapon;
import com.tree.combatcalculator.WeaponCountHolder;

public class BuyNode extends DecisionNode {

	public BuyNode(Node parent) {
		
		super(parent);
		nodeType = Node.Type.BUY;

	}
	
	public static List<Node> createBuyNodes(Node parent, PermanentTreeData permData) {
		
		List<Node> buyNodes = new ArrayList<Node>();
		List<WeaponCountHolder> holders = parent.getTempData().weaponHolders;
		
		for (WeaponCountHolder holder :  holders){
			
			if (holder.hasInit()){
				
				buyNodes.addAll(makeInitial(parent, holders.indexOf(holder), permData));
				
			}else if (canBuyAttacks(holder, parent, holders)){
				//TODO: add in optimal weapon
				Node buyNode = buyAttack(parent, holders.indexOf(holder));
				buyNodes.add(buyNode);
			}
			
		}
		
		return buyNodes;	
	}

	private static boolean canBuyAttacks(WeaponCountHolder holder,
			Node parent, List<WeaponCountHolder> holders) {
		
		if (holder.hasAttackToBuy())
			if (parent.getTempData().focus > 0)
				if (WeaponCountHolder.outOfInits(holders))
					return true;
		
		return false;
	}

	private static Node buyAttack(Node parent, int index) {
		
		Node buyNode = new BuyNode(parent);
		buyNode.getTempData().focus--;
		WeaponCountHolder.buyAttack(index, buyNode.getTempData().weaponHolders);
		buyNode.weaponIndex = index;
		
		return buyNode;
	
	}

	private static List<Node> makeInitial(Node parent, int index, PermanentTreeData permData) {
		
		List<Node> buyNodes = new ArrayList<Node>();
		List<WeaponCountHolder> holders = parent.getTempData().weaponHolders;
		
		//if there is a star attack, make it a seperate attack
		if (Weapon.checkWeaponVariables(AtkVarCopy.GroupFlag.STAR_ATTACK, permData) && 
				WeaponCountHolder.hasAllInitials(holders)){

			Node starNode = new BuyNode(parent);
			
			WeaponCountHolder.useAllInitials(starNode.getTempData().weaponHolders);
			starNode.getTempData().variables.put(AtkVarCopy.Id.STAR_ATTACK, 
					AtkVarCopy.createAtkVar(AtkVarCopy.Id.STAR_ATTACK));
			
			buyNodes.add(starNode);
		}
		
		Node buyNode = new BuyNode(parent);
		WeaponCountHolder.makeAttack(index, buyNode.getTempData().weaponHolders);
		buyNodes.add(buyNode);
		
		return buyNodes;
	}




	@Override
	public List<Node> createChildren(PermanentTreeData permData) {
		return AttackDecNode.createAttackDecNodes(this, permData);
	}

	@Override
	public float calculateValue(PermanentTreeData permData) {
		// TODO Auto-generated method stub
		return 0;
	}




}
