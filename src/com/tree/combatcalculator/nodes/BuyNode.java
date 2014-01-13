package com.tree.combatcalculator.nodes;

import java.util.ArrayList;
import java.util.List;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.AtkVarCopy;
import com.tree.combatcalculator.PermanentTreeData;
import com.tree.combatcalculator.WeaponCountHolder;

public class BuyNode extends DecisionNode {

	public BuyNode(Node parent) {
		
		super(parent);
		nodeType = Node.Type.BUY;

	}
	
	public static List<Node> createBuyNodes(Node parent, PermanentTreeData permData) {
		
		List<Node> buyNodes = new ArrayList<Node>();
		List<WeaponCountHolder> holders = parent.getTempData().weaponHolders;
		
		for (WeaponCountHolder holder :  parent.getTempData().weaponHolders){
			
			if (holder.hasInit()){
				
				buyNodes.addAll(makeInitial(parent, holders.indexOf(holder), permData));
				
			}else if (holder.hasAttackToBuy() && parent.getTempData().focus > 0){
				
				Node buyNode = buyAttack(parent, holders.indexOf(holder));
				buyNodes.add(buyNode);
			}
			
		}
		
		return buyNodes;
		
		
	}

	private static Node buyAttack(Node parent, int index) {
		
		Node buyNode = new BuyNode(parent);
		buyNode.getTempData().focus--;
		WeaponCountHolder.buyAttack(index, buyNode.getTempData().weaponHolders);
		
		return buyNode;
	
	}

	private static List<Node> makeInitial(Node parent, int index, PermanentTreeData permData) {
		
		List<Node> buyNodes = new ArrayList<Node>();
		List<WeaponCountHolder> holders = parent.getTempData().weaponHolders;
		
		//if there is a star attack, make it a seperate attack
		if (AtkVarCopy.contains(AtkVarCopy.Id.STAR_ATTACK, permData) && 
				WeaponCountHolder.hasAllInitials(holders)){

			Node starNode = new BuyNode(parent);
			
			WeaponCountHolder.useAllInitials(starNode.getTempData().weaponHolders);
			starNode.getTempData().variables.put(AtkVarCopy.Id.STAR_ATTACK, 
					new AtkVarCopy(AtkVarCopy.Id.STAR_ATTACK));
			
			buyNodes.add(starNode);
		}
		
		Node buyNode = new BuyNode(parent);
		WeaponCountHolder.makeAttack(index, buyNode.getTempData().weaponHolders);
		buyNodes.add(buyNode);
		
		return buyNodes;
	}

	@Override
	public void calculateValue() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public List<Node> createChildren(PermanentTreeData permData) {
		// TODO Auto-generated method stub
		return null;
	}




}
