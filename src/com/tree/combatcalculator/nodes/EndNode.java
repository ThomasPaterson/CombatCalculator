package com.tree.combatcalculator.nodes;

import java.util.List;
import java.util.Map;

import com.example.combatcalculator.AttackHolder;
import com.tree.combatcalculator.AtkVarCopy;
import com.tree.combatcalculator.PermanentTreeData;
import com.tree.combatcalculator.AtkVarCopy.Id;
import com.tree.combatcalculator.WeaponCountHolder;

public class EndNode extends DecisionNode {

	public EndNode() {
	
		super();
		nodeType = Node.Type.END;
		
	}
	
	@Override
	public List<Node> createChildren(PermanentTreeData permData) {	
		return BuyNode.createBuyNodes(this, permData);
	}

	@Override
	public void calculateValue() {
		// TODO Auto-generated method stub
		
	}

}
