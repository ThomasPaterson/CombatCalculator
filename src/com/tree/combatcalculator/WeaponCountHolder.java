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
			PermanentTreeData permData){
		
		List<WeaponCountHolder> holders = new ArrayList<WeaponCountHolder>();
		
		for (Weapon w : weapons){
			
			WeaponCountHolder holder = new WeaponCountHolder();
			
			holder.initials = w.getNumAtks();
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




}
