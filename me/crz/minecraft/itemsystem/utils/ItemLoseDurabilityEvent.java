package me.crz.minecraft.itemsystem.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemLoseDurabilityEvent extends CEvent {

	private ItemStack item;
	private Player player;
	private boolean changed = false;
	
	public ItemLoseDurabilityEvent(ItemStack item, Player player) {
		this.item = item;
		this.player = player;
	}
	
	public ItemStack getItem() {
		return this.item;
	}
	
	public void change() {
		this.changed = !changed;
	}
	
	public boolean isChanged() {
		return this.changed;
	}
	
	public Player getPlayer() {
		return this.player;
	}

}
