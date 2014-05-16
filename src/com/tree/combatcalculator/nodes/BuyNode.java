package com.tree.combatcalculator.nodes;

import java.util.ArrayList;
import java.util.List;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.DynamicAttackData;
import com.tree.combatcalculator.StaticAttackData;
import com.tree.combatcalculator.WeaponCountHolder;

public class BuyNode extends DecisionNode {

	public BuyNode(Node parent, DynamicAttackData tempData) {

		super(parent);
		tempData = new DynamicAttackData(tempData);
		nodeType = Node.Type.BUY;

	}

	public static List<Node> createBuyNodes(Node parent, StaticAttackData permData) {

		List<Node> buyNodes = new ArrayList<Node>();
		DynamicAttackData tempData = new DynamicAttackData(parent.getTempData());
		tempData.clearTempValues();
		List<WeaponCountHolder> holders = tempData.getWeaponHolders();


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
			if (parent.getTempData().getFocus() > 0)
				if (WeaponCountHolder.outOfInits(holders))
					return true;

		return false;
	}

	private static Node buyAttack(Node parent, int index, DynamicAttackData tempData) {

		Node buyNode = new BuyNode(parent, tempData);
		buyNode.getTempData().decrementFocus();
		WeaponCountHolder.buyAttack(index, buyNode.getTempData().getWeaponHolders());
		buyNode.weaponIndex = index;

		return buyNode;

	}

	private static List<Node> makeInitial(Node parent, int index, StaticAttackData permData,
			DynamicAttackData tempData) {

		List<Node> buyNodes = new ArrayList<Node>();
		List<WeaponCountHolder> holders = parent.getTempData().getWeaponHolders();

		Node starNode = addStarAttacks(parent, index, permData, holders, tempData);
		if (starNode != null)
			buyNodes.add(starNode);


		Node buyNode = new BuyNode(parent, tempData);

		if (isCharging(parent, permData)){
			buyNode.getTempData().getVariables().put(AtkVar.Id.CHARGE,
					AtkVar.createAtkVar(AtkVar.Id.CHARGE));
		}

		WeaponCountHolder.makeAttack(index, buyNode.getTempData().getWeaponHolders());
		buyNodes.add(buyNode);

		return buyNodes;
	}




	private static boolean isCharging(Node parent, StaticAttackData permData) {

		Boolean hasCharge = permData.checkContains(AtkVar.Id.CHARGE);
		Boolean firstAttack = WeaponCountHolder.hasAllInitials(parent.getTempData().getWeaponHolders());

		return (hasCharge && firstAttack);
	}



	private static Node addStarAttacks(Node parent, int index, StaticAttackData permData,
			List<WeaponCountHolder> holders, DynamicAttackData tempData) {

		AtkVar starAttack = permData.findStarAttack(index);

		//if there is a star attack, make it a seperate attack
		if (starAttack != null &&
				WeaponCountHolder.hasAllInitials(holders)){

			Node starNode = new BuyNode(parent, tempData);
			WeaponCountHolder.useAllInitials(starNode.getTempData().getWeaponHolders());
			starNode.getTempData().getVariables().put(starAttack.getId(),
					new AtkVar(starAttack));

			return starNode;
		}
		return null;
	}

	@Override
	public List<Node> createChildren(StaticAttackData permData) {
		return AttackDecNode.createAttackDecNodes(this, permData);
	}

	@Override
	public float calculateValue(StaticAttackData permData) {
		return 0;
	}




}
