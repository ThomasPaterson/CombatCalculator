package com.tree.combatcalculator.nodes;

import java.util.List;
import java.util.Map;

import com.example.combatcalculator.AttackHolder;
import com.tree.combatcalculator.AtkVarCopy;
import com.tree.combatcalculator.PermanentTreeData;
import com.tree.combatcalculator.AtkVarCopy.Id;
import com.tree.combatcalculator.WeaponCountHolder;

public class EndNode extends DecisionNode {

	public EndNode(int eND, int numFocus, Map<Id, AtkVarCopy> tempState,
			List<WeaponCountHolder> weaponCountHolder) {
		
		super(eND, numFocus, tempState, weaponCountHolder);
		nodeType = Node.Type.END;
		
	}
	
	@Override
	public List<Node> createChildren(PermanentTreeData permData) {
		
		return children;
	}

}
