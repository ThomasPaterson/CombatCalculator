package com.tree.combatcalculator.nodes;

import java.util.List;

import com.tree.combatcalculator.StaticAttackData;
import com.tree.combatcalculator.DynamicAttackData;

public class EndNode extends DecisionNode {


	
	public EndNode(DynamicAttackData tempData) {
		super();
		nodeType = Node.Type.END;
		this.tempData = tempData;
	}


	@Override
	public List<Node> createChildren(StaticAttackData permData) {	
		return BuyNode.createBuyNodes(this, permData);
	}


	@Override
	public float calculateValue(StaticAttackData permData) {
		return 0;
	}



}
