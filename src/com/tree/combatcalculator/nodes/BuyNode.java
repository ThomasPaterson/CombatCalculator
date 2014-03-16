package com.tree.combatcalculator.nodes;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.AtkVarCopy;
import com.tree.combatcalculator.PermanentTreeData;
import com.tree.combatcalculator.TemporaryTreeData;
import com.tree.combatcalculator.Weapon;
import com.tree.combatcalculator.WeaponCountHolder;

public class BuyNode extends DecisionNode {

	public BuyNode(Node parent, TemporaryTreeData tempData) {
		
		super(parent);
		tempData = new TemporaryTreeData(tempData);
		nodeType = Node.Type.BUY;

	}
	
	public BuyNode(Parcel in) {
		super(in);
		nodeType = Node.Type.BUY;
	}

	public static List<Node> createBuyNodes(Node parent, PermanentTreeData permData) {
		
		List<Node> buyNodes = new ArrayList<Node>();
		TemporaryTreeData tempData = new TemporaryTreeData(parent.getTempData());
		tempData.clearTempValues();
		List<WeaponCountHolder> holders = tempData.weaponHolders;
		
		
		for (WeaponCountHolder holder :  holders){
			
			if (holder.hasInit()){
				
				buyNodes.addAll(makeInitial(parent, holders.indexOf(holder), permData, tempData));
				
			}else if (canBuyAttacks(holder, parent, holders)){
				//TODO: add in optimal weapon
				Node buyNode = buyAttack(parent, holders.indexOf(holder), tempData);
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

	private static Node buyAttack(Node parent, int index, TemporaryTreeData tempData) {
		
		Node buyNode = new BuyNode(parent, tempData);
		buyNode.getTempData().focus--;
		WeaponCountHolder.buyAttack(index, buyNode.getTempData().weaponHolders);
		buyNode.weaponIndex = index;
		
		return buyNode;
	
	}

	private static List<Node> makeInitial(Node parent, int index, PermanentTreeData permData, 
			TemporaryTreeData tempData) {
		
		List<Node> buyNodes = new ArrayList<Node>();
		List<WeaponCountHolder> holders = parent.getTempData().weaponHolders;
		
		Node starNode = addStarAttacks(parent, index, permData, holders, tempData);
		if (starNode != null)
			buyNodes.add(starNode);
		
		
		Node buyNode = new BuyNode(parent, tempData);
		
		if (isCharging(parent, permData)){
			buyNode.getTempData().variables.put(AtkVarCopy.Id.CHARGE, 
					AtkVarCopy.createAtkVar(AtkVarCopy.Id.CHARGE));
		}
		
		WeaponCountHolder.makeAttack(index, buyNode.getTempData().weaponHolders);
		buyNodes.add(buyNode);
		
		return buyNodes;
	}




	private static boolean isCharging(Node parent, PermanentTreeData permData) {

		Boolean hasCharge = AtkVarCopy.contains(permData.variables, 
				AtkVarCopy.Group.SITUATION, AtkVarCopy.Id.CHARGE);
		Boolean firstAttack = WeaponCountHolder.hasAllInitials(parent.getTempData().weaponHolders);
		
		return (hasCharge && firstAttack);
	}
	
	

	private static Node addStarAttacks(Node parent, int index, PermanentTreeData permData, 
			List<WeaponCountHolder> holders, TemporaryTreeData tempData) {
		
		//if there is a star attack, make it a seperate attack
		if (Weapon.checkWeaponVariables(AtkVarCopy.Modifier.STAR_ATTACK, permData) && 
				WeaponCountHolder.hasAllInitials(holders)){

			Node starNode = new BuyNode(parent, tempData);
			
			WeaponCountHolder.useAllInitials(starNode.getTempData().weaponHolders);
			starNode.getTempData().variables.put(AtkVarCopy.Id.STAR_ATTACK, 
					AtkVarCopy.createAtkVar(AtkVarCopy.Id.STAR_ATTACK));
			
			return starNode;
		}
		return null;
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
