package me.crz.minecraft.itemsystem;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import de.tr7zw.itemnbtapi.NBTItem;
import me.crz.minecraft.itemsystem.ActiveItem.active;
import me.crz.minecraft.itemsystem.utils.ConfigData;

public class CustomItem {

	private int xp, level, enchantSlots;
	private active activeAbility, activatedAbility;
	private int soulBound;
	private Map<String, Long> abilityCooldown;
	private Map<String, Object> abilityData;
	private ItemStack item;

	public int levelxp;

	boolean changed = false;

	@SuppressWarnings("unchecked")
	public CustomItem(JSONObject obj, ItemStack item) {
		this.xp = (int) Integer.valueOf(obj.get("xp") + "");
		this.level = (int) Integer.valueOf(obj.get("level") + "");
		if(item.getType().name().contains("SWORD")) {
			this.enchantSlots = ConfigData.swordenchantslots[level-1];
		} else if(item.getType().name().contains("AXE")) {
			this.enchantSlots = ConfigData.axeenchantslots[level-1];
		} else if(item.getType().name().contains("PICKAXE")) {
			this.enchantSlots = ConfigData.pickaxeenchantslots[level-1];
		} else if(item.getType().name().contains("SHOVEL")) {
			this.enchantSlots = ConfigData.shovelenchantslots[level-1];
		} else if(item.getType().name().contains("BOW")) {
			this.enchantSlots = ConfigData.bowenchantslots[level-1];
		} else if(item.getType().name().contains("ARMOR")) {
			this.enchantSlots = ConfigData.armorenchantslots[level-1];
		}else {
			this.enchantSlots = 1;
		}
		this.levelxp = ConfigData.levelxp[this.level-1];
		this.soulBound = (int) Integer.valueOf(obj.get("soulBound")+"");
		this.activeAbility = active.valueOf(obj.get("activeAbility") + "");
		this.activatedAbility = active.valueOf(obj.get("activatedAbility") + "");
		this.abilityCooldown = new HashMap<>();
		JSONObject o = (JSONObject) obj.get("abilityCooldown");
		((Set<Map.Entry<String, Long>>) o.entrySet()).forEach((e) -> abilityCooldown.put(e.getKey(), e.getValue()));

		this.abilityData = new HashMap<>();
		JSONObject oo = (JSONObject) obj.get("abilityData");
		if (oo != null && !oo.isEmpty())
			for (Map.Entry<String, Object> e : (Set<Map.Entry<String, Object>>) oo.entrySet()) {
				this.abilityData.put(e.getKey(), e.getValue());
			}
		this.item = item;
		update();
	}

	public int getLevel() {
		return this.level;
	}

	public int getEnchantSlots() {
		return this.enchantSlots;
	}

	public int countSoulBound() {
		return this.soulBound;
	}

	public int getXP() {
		return this.xp;
	}

	public active getActiveAbility() {
		return this.activeAbility;
	}

	public active getActivatedAbility() {
		return this.activatedAbility;
	}

	public Map<String, Long> getAbilityCooldowns() {
		return this.abilityCooldown;
	}

	public Map<String, Object> getAbilityData() {
		return this.abilityData;
	}

	public void setAbilityData(active a, Object o) {
		this.abilityData.put(a.name(), o);
	}

	public void setLevel(int level) {
		this.level = level;
		if (level < 30)
			xp = 0;
		else
			xp = -1;
		changed = true;
		update();
	}

	public void setEnchantSlots(int enchantSlots) {
		this.enchantSlots = enchantSlots;
		this.changed = true;
	}

	public void setSoulBound(int soulBound) {
		this.soulBound = soulBound;
		this.changed = true;
	}

	public void setXP(int xp) {
		this.xp = xp;
		changed = true;
		update();
	}

	public void setActiveAbility(active active) {
		this.activeAbility = active;
		changed = true;
	}

	public void setActivatedAbility(active active) {
		this.activatedAbility = active;
		changed = true;
	}

	public void setAbilityCooldown(active ability, Long cooldown) {
		this.abilityCooldown.put(ability.name(), cooldown);
		changed = true;
	}

	public void update() {
		double maxLevelXp = this.levelxp;
		if (xp >= maxLevelXp) {
			level++;
			if(item.getType().name().contains("SWORD")) {
				this.enchantSlots = ConfigData.swordenchantslots[level-1];
			} else if(item.getType().name().contains("AXE")) {
				this.enchantSlots = ConfigData.axeenchantslots[level-1];
			} else if(item.getType().name().contains("PICKAXE")) {
				this.enchantSlots = ConfigData.pickaxeenchantslots[level-1];
			} else if(item.getType().name().contains("SPADE")) {
				this.enchantSlots = ConfigData.shovelenchantslots[level-1];
			} else if(item.getType().name().contains("BOW")) {
				this.enchantSlots = ConfigData.bowenchantslots[level-1];
			} else if(item.getType().name().contains("ARMOR")) {
				this.enchantSlots = ConfigData.armorenchantslots[level-1];
			}
			
			if (level < 30)
				xp = (int) Math.floor(xp - maxLevelXp);
			else
				xp = -1;
			changed = true;
		}else {
			if(item.getType().name().contains("SWORD")) {
				this.enchantSlots = ConfigData.swordenchantslots[level-1];
			} else if(item.getType().name().contains("AXE")) {
				this.enchantSlots = ConfigData.axeenchantslots[level-1];
			} else if(item.getType().name().contains("PICKAXE")) {
				this.enchantSlots = ConfigData.pickaxeenchantslots[level-1];
			} else if(item.getType().name().contains("SPADE")) {
				this.enchantSlots = ConfigData.shovelenchantslots[level-1];
			} else if(item.getType().name().contains("BOW")) {
				this.enchantSlots = ConfigData.bowenchantslots[level-1];
			} else if(item.getType().name().contains("ARMOR")) {
				this.enchantSlots = ConfigData.armorenchantslots[level-1];
			}
		}
	}

	@SuppressWarnings("unchecked")
	public ItemStack getItem() {
		update();
		if (changed) {
			JSONObject obj = new JSONObject();
			obj.put("xp", xp);
			obj.put("level", level);
			obj.put("enchantSlots", enchantSlots);
			obj.put("soulBound", soulBound);
			obj.put("activeAbility", activeAbility.name());
			obj.put("activatedAbility", activatedAbility.name());
			JSONObject abilityCooldownObject = new JSONObject();
			abilityCooldown.entrySet().forEach((e) -> {
				abilityCooldownObject.put(e.getKey() + "", e.getValue());
			});
			obj.put("abilityCooldown", abilityCooldownObject);
			JSONObject abilityDataObject = new JSONObject();
			abilityData.entrySet().forEach((e) -> {
				abilityDataObject.put(e.getKey() + "", e.getValue());
			});
			obj.put("abilityData", abilityDataObject);
			NBTItem nbti = new NBTItem(item);
			nbti.setString("itemSystem", obj.toJSONString());
			return nbti.getItem();
		}
		
		return this.item;
	}

}
