package com.example.combatcalculator;

import java.util.ArrayList;

import com.tree.combatcalculator.AtkVar;
import com.tree.combatcalculator.AttackCalculator;
import com.tree.combatcalculator.AttackModel;
import com.tree.combatcalculator.DefendModel;


//Class to act as a holder for all the data needed to be passed to an asynchronious task
public class AttackHolder {
    public final AttackModel attacker;
    public final DefendModel defender;
    public final ArrayList<AtkVar> situation;
    public final int focus;
    public final AttackCalculator atkCalc;
    public final int optimization;

    public AttackHolder
    (AttackModel attacker,  DefendModel defender, ArrayList<AtkVar> situation, 
    		int focus, AttackCalculator atkCalc, int optimization) {
        this.attacker = attacker;
        this.defender = defender;
        this.situation = situation;
        this.focus = focus;
        this.atkCalc = atkCalc;
        this.optimization = optimization;
    }

}