package me.crz.minecraft.itemsystem.utils;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.tr7zw.itemnbtapi.NBTItem;
import me.crz.minecraft.itemsystem.ActiveItem.active;
import me.crz.minecraft.itemsystem.CustomItem;

public class ItemUtil {

	// -1 = doesn't have xp
	@SuppressWarnings("unchecked")
	public static Optional<CustomItem> getItem(ItemStack item) {
		if (item == null)
			return Optional.empty();
		if (!doesItemHaveXP(item)) {
			return Optional.empty();
		}
		NBTItem nbti = new NBTItem(item);
		
		if (nbti.hasNBTData() && nbti.hasKey("itemSystem")) {
		} 
		else {
			JSONObject obj = new JSONObject();
			obj.put("xp", 0);
			obj.put("level", 1);
			obj.put("enchantSlots", 1);
			obj.put("soulBound", 0);
			obj.put("activeAbility", active.NONE.name());
			obj.put("activatedAbility", active.NONE.name());
			obj.put("abilityCooldown", new JSONObject());
			obj.put("abilityData", new JSONObject());
			nbti.setString("itemSystem", obj.toJSONString());
			item = nbti.getItem();
		}		

		try {

			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(nbti.getString("itemSystem"));
			if (!obj.containsKey("xp") || !obj.containsKey("level") || !obj.containsKey("abilityData"))
				return Optional.empty();
			return Optional.ofNullable(new CustomItem(obj, item));

		} catch (ParseException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

	public static boolean isOre(Block b) {
		Material m = b.getType();
		return m == Material.COAL_ORE || m == Material.DIAMOND_ORE || m == Material.EMERALD_ORE
				|| m == Material.GLOWING_REDSTONE_ORE || m == Material.GOLD_ORE || m == Material.IRON_ORE
				|| m == Material.LAPIS_ORE || m == Material.QUARTZ_ORE || m == Material.REDSTONE_ORE;
	}

	public static boolean doesItemHaveXP(ItemStack item) {
		return (item.getType().name().contains("axe".toUpperCase())
				|| item.getType().name().contains("spade".toUpperCase())
				|| item.getType().name().contains("sword".toUpperCase())
				|| item.getType().name().contains("helmet".toUpperCase())
				|| item.getType().name().contains("boots".toUpperCase())
				|| item.getType().name().contains("chestplate".toUpperCase())
				|| item.getType().name().contains("legg".toUpperCase())
				|| item.getType().name().contains("hoe".toUpperCase()) || item.getType().equals(Material.BOW));
	}

}
