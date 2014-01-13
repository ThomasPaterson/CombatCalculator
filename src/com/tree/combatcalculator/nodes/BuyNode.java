package com.tree.combatcalculator.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tree.combatcalculator.AtkVarCopy;
import com.tree.combatcalculator.AtkVarCopy.Id;
import com.tree.combatcalculator.PermanentTreeData;
import com.tree.combatcalculator.Weapon;
import com.tree.combatcalculator.WeaponCountHolder;

public class BuyNode extends DecisionNode {

	public BuyNode(int end, int numFocus, Map<Id, AtkVarCopy> tempState,
			List<WeaponCountHolder> weaponsState) {
		
		super(end, numFocus, tempState,weaponsState);
		nodeType = Node.Type.BUY;

	}
	
	@Override
	public List<Node> createChildren(PermanentTreeData permData) {
		
		children = new ArrayList<Node>();
		
		
		for (WeaponCountHolder w : weaponCount){
			
			if (w.hasInit())
				children.addAll(makeInitial());
			else if (w.hasAttackToBuy() && focus > 0)
				children.addAll(buyAttack());
			
		}
		
		return children;
		
		
	}

	private List<Node> buyAttack() {
		return null;
		
	}

	private List<Node> makeInitial() {
		return null;
		
	}


}
