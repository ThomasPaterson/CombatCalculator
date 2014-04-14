package com.tree.combatcalculator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class WeaponCountHolder {

	
	public int initials;
	public int maxInitials;
	public int boughtAttacks;
	public int maxROF;
	public boolean disabled;
	public Weapon weapon;
	
	
	public WeaponCountHolder(){
		
	}
	
	public WeaponCountHolder(int intitials, int maxInitials, int boughtAttacks,
			int maxROF, boolean disabled, Weapon weapon){
		
		this.initials = initials;
		this.maxInitials = maxInitials;
		this.boughtAttacks = boughtAttacks;
		this.maxROF = maxROF;
		this.disabled = disabled;
		this.weapon = weapon;
		
	}
	
	
	
	
	public static List<WeaponCountHolder >createWeaponCountHolders(List<Weapon> weapons, 
			StaticAttackData permData){
		
		List<WeaponCountHolder> holders = new ArrayList<WeaponCountHolder>();
		
		for (Weapon w : weapons){
			
			WeaponCountHolder holder = new WeaponCountHolder();
			
			holder.initials = 0;
			holder.maxInitials = w.getNumAtks();
			holder.boughtAttacks = 0;
			holder.maxROF = w.getROF();
			
			if (!AtkVar.checkContains(AtkVar.RANGED)){
				
				if (!w.getRanged())
					holder.disabled = false;
				else if ((AtkVar.checkContainsName(w.getVariables(), AtkVar.GUNFIGHTER) ||
						AtkVar.checkContainsName(w.getVariables(), AtkVar.POINT_BLANK)))
					holder.disabled = false;
			
			}else if (AtkVar.checkContains(AtkVar.RANGED) && w.getRanged()){
				
				holder.disabled = false;
			}else{
				holder.disabled = true;
			}	
			
			holders.add(holder);
		}
		
		return holders;
	}
	
	
	public static List<WeaponCountHolder> copy(List<WeaponCountHolder> holders){
		
		return new ArrayList<WeaponCountHolder>(holders);
		
	}


	public static boolean hasInits(List<WeaponCountHolder> holders) {
		
		for (WeaponCountHolder holder : holders){
			
			if (holder.hasInit())
				return true;
		}

		return false;
	}
	
	public boolean hasInit() {

		return !disabled && initials <= maxInitials;
	}
	
	public static boolean hasAttacksToBuy(List<WeaponCountHolder> holders) {
		
		for (WeaponCountHolder holder : holders){
			
			if (holder.hasAttackToBuy())
				return true;
		}

		return false;
	}
	
	
	public boolean hasAttackToBuy() {

		return !disabled && boughtAttacks + maxInitials <= maxROF;

	}


	public static boolean hasAttacks(List<WeaponCountHolder> holders) {
		
		return hasInits(holders) || hasAttacksToBuy(holders);
	}
	
	public boolean hasAttack() {
		
		return hasInit() || hasAttackToBuy();
	}

	public static void buyAttack(int index,
			List<WeaponCountHolder> holders) {
		
		WeaponCountHolder holder = holders.get(index);
		holder.boughtAttacks++;
	}
	
	public static void makeAttack(int index, List<WeaponCountHolder> holders) {
		
		WeaponCountHolder holder = holders.get(index);
		holder.initials++;	
	}
	
	public static boolean hasAllInitials(List<WeaponCountHolder> holders){
		
		int numAttacksMade = 0;
		
		for (WeaponCountHolder holder : holders)
			numAttacksMade += holder.initials;
		
		return (numAttacksMade == 0);
		
	}
	
public static void useAllInitials(List<WeaponCountHolder> holders){
		
		for (WeaponCountHolder holder : holders)
			holder.initials = holder.maxInitials;
		
	}

public static boolean outOfInits(List<WeaponCountHolder> holders) {

	
	for (WeaponCountHolder holder : holders)
		if (holder.initials < holder.maxInitials)
			return true;
	
	return false;
}





}
