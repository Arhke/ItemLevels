package me.crz.minecraft.itemsystem.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class CItem extends Base {

	private ItemStack item;
	private ItemMeta im;

	public CItem(ItemStack item) {
		this.item = item.clone();
		this.im = item.getItemMeta();
	}

	public CItem(Material material) {
		ItemStack item = new ItemStack(material);
		this.item = item;
		this.im = item.getItemMeta();
	}

	@SuppressWarnings("deprecation")
	public CItem(Material material, byte data) {
		ItemStack item = new ItemStack(material, 1, (short) 0, data);
		this.item = item;
		this.im = item.getItemMeta();
	}

	public CItem() {
		this.item = new ItemStack(Material.STICK);
		this.im = item.getItemMeta();
		im.setDisplayName("Error Null Itemstack");
	}

	public CItem setName(String name) {
		im.setDisplayName(colour(name));
		return this;
	}

	public CItem setLore(List<String> lore) {
		List<String> temp = new ArrayList<>();
		for(String s : lore)
			temp.add(colour(s));
		im.setLore(temp);
		return this;
	}

	public CItem addEnchantment(Enchantment ench, int level) {
		im.addEnchant(ench, level, true);
		return this;
	}

	public boolean isNull() {
		return im.hasDisplayName() ? im.getDisplayName().equalsIgnoreCase("Error Null Itemstack") : false;
	}

	public CItem glow(boolean b) {
		if (b) {
			addEnchantment(Enchantment.DAMAGE_ALL, 1);
			this.im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		return this;
	}

	@SuppressWarnings("deprecation")
	public CItem setColor(DyeColor color) {
		if (this.item.getType() == Material.WOOL || this.item.getType() == Material.CARPET || this.item.getType() == Material.STAINED_GLASS || this.item.getType() == Material.STAINED_GLASS_PANE)
			this.item = new ItemStack(this.item.getType(), 1, color.getData());
		return this;
	}

	public CItem setColor(Color colour) {
		if (colour != null && item.getType().name().startsWith("LEATHER_")) {
			LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) im;
			leatherArmorMeta.setColor(colour);
			im = (leatherArmorMeta);
		}
		return this;
	}

	public CItem setAmount(int amount) {
		this.item.setAmount(amount);
		return this;
	}

	public ItemStack build(Integer amount) {
		this.item.setAmount(amount);
		return build();
	}

	public ItemStack build() {
		this.item.setItemMeta(im);
		return this.item;
	}
}
