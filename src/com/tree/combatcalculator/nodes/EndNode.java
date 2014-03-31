package com.tree.combatcalculator.nodes;

import java.util.List;

import com.tree.combatcalculator.PermanentTreeData;
import com.tree.combatcalculator.TemporaryTreeData;

public class EndNode extends DecisionNode {


	
	public EndNode(TemporaryTreeData tempData) {
		super();
		nodeType = Node.Type.END;
		this.tempData = tempData;
	}


	@Override
	public List<Node> createChildren(PermanentTreeData permData) {	
		return BuyNode.createBuyNodes(this, permData);
	}


	@Override
	public float calculateValue(PermanentTreeData permData) {
		return 0;
	}



}
