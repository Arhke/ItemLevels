package me.crz.minecraft.itemsystem.utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigData {
	public static int[] levelxp = new int[30];
	
	public static int[] dodgechance = new int[30];
	public static int[] firechance = new int[30];
	public static int[] blindchance = new int[30];
	public static int[] armorenchantslots = new int[30];
	
	public static int[] playerbleed = new int[30];
	public static int[] mobbleed = new int[30];
	public static int[] swordenchantslots = new int[30];
	
	public static int[] axedoubledrop = new int[30];
	public static int[] goldenapple = new int[30];
	public static int[] doubledamage = new int[30];
	public static int[] axeenchantslots = new int[30];
	
	public static int[] ironchance = new int[30];
	public static int[] goldchance = new int[30];
	public static int[] diamondchance = new int[30];
	public static int[] emeraldchance = new int[30];
	public static int[] spreadchance = new int[30];
	public static int[] pickaxeenchantslots = new int[30];
	
	public static int[] shoveldoubledrop = new int[30];
	public static int[] expdrop = new int[30];
	public static int[] shovelenchantslots = new int[30];
	
	public static int[] infinitychance = new int[30];
	public static int[] lightningchance = new int[30];
	public static int[] slownesschance = new int[30];
	public static int[] bowenchantslots = new int[30];
	public static void loadConfigData(FileConfiguration fileconfig) {
		
//		String[] activeabilities = {"multishot", "explosive", "firework", "poison_arrow","auto_fire","slay","slam","rage","insight","timber","toss","bolt","shuffle","reserve","crush","veinminer","excavation","treasure"};
//		JSONObject jsono = new JSONObject();
//		for(String s:activeabilities) {
//			jsono.put("cooldown_"+s,fileconfig.getInt("actives."+s+".cooldown"));
//			jsono.put("activetime_"+s,fileconfig.getInt("actives."+s+".activetime"));
//			jsono.put("cooldown30_"+s,fileconfig.getInt("actives."+s+".cooldown30"));
//			jsono.put("activetime30_"+s,fileconfig.getInt("actives."+s+".activetime30"));
//		}
		
		for(int i = 1; i<=30;i++) {
			dodgechance[i-1] = fileconfig.getInt("passivechance.armor.level"+i+".dodge");
			firechance[i-1] = fileconfig.getInt("passivechance.armor.level"+i+".fire");
			blindchance[i-1] = fileconfig.getInt("passivechance.armor.level"+i+".blind");
			armorenchantslots[i-1] = fileconfig.getInt("passivechance.armor.level"+i+".enchantmentslots");
			
			mobbleed[i-1] = fileconfig.getInt("passivechances.sword.level"+i+".mobbleed");
			playerbleed[i-1] = fileconfig.getInt("passivechances.sword.level"+i+".playerbleed");
			swordenchantslots[i-1] = fileconfig.getInt("passivechances.sword.level"+i+".enchantmentslots");
			
			axedoubledrop[i-1] = fileconfig.getInt("passivechances.axe.level"+i+".doubledrop");
			goldenapple[i-1] = fileconfig.getInt("passivechances.axe.level"+i+".goldenapple");
			doubledamage[i-1] = fileconfig.getInt("passivechances.axe.level"+i+".doubledamage");
			axeenchantslots[i-1] = fileconfig.getInt("passivechances.axe.level"+i+".enchantmentslots");
			
			ironchance[i-1] = fileconfig.getInt("passivechances.pickaxe.level"+i+".irondrops");
			goldchance[i-1] = fileconfig.getInt("passivechances.pickaxe.level"+i+".golddrops");
			diamondchance[i-1] = fileconfig.getInt("passivechances.pickaxe.level"+i+".diamonddrops");
			emeraldchance[i-1] = fileconfig.getInt("passivechances.pickaxe.level"+i+".emeralddrops");
			spreadchance[i-1] = fileconfig.getInt("passivechances.pickaxe.level"+i+".spread");
			pickaxeenchantslots[i-1] = fileconfig.getInt("passivechances.pickaxe.level"+i+".enchantmentslots");
			
			shoveldoubledrop[i-1] = fileconfig.getInt("passivechances.shovel.level"+i+".doubledrop");
			expdrop[i-1] = fileconfig.getInt("passivechances.shovel.level"+i+".expdrop");
			shovelenchantslots[i-1] = fileconfig.getInt("passivechances.shovel.level"+i+".enchantmentslots");
			
		    infinitychance[i-1] = fileconfig.getInt("passivechances.bow.level"+i+".infinity");
			lightningchance[i-1] = fileconfig.getInt("passivechances.bow.level"+i+".lightning");
			slownesschance[i-1] = fileconfig.getInt("passivechances.bow.level"+i+".slowness");
			bowenchantslots[i-1] = fileconfig.getInt("passivechances.bow.level"+i+".enchantmentslots");
			
			levelxp[i-1] = fileconfig.getInt("xp.level"+i+"xp");
		}

	}

	public static void main(String[] args) {
		Random rnd = ThreadLocalRandom.current();
		int[] ar = {1,2,3,4,5,6,7,8,9,10,11,12,13,14};
		for (int i = ar.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			int a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
		for(int i:ar) {
			System.out.println(i);
		}
		
	}

}
