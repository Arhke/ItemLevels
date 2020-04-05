package me.crz.minecraft.itemsystem.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.itemnbtapi.NBTItem;
import me.crz.minecraft.itemsystem.CustomItem;
import me.crz.minecraft.itemsystem.ItemPlugin;
import net.md_5.bungee.api.ChatColor;

public class Commands extends Base implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("itemlevel") && sender.isOp()) {
			if (args != null && args.length >= 1) {
				if (args[0].equalsIgnoreCase("soulbound")) {
					if (args.length >= 2) {
						Optional<Player> op;
						if ((op = getPlayer(args[1])).isPresent()) {
							ItemStack is = new ItemStack(Material.PRISMARINE_SHARD, 1);
							ItemMeta im = is.getItemMeta();
							im.setDisplayName(colour("&5&lSoul Shard"));
							List<String> lore = new ArrayList<>();

							lore.add(colour("&f&lThis magic soul infused shard will bind any one item to your soul."));
							lore.add(colour(
									"&7For up to three deaths, item will not be dropped and will return to your inventory."));
							im.setLore(lore);
							is.setItemMeta(im);
							NBTItem nbti = new NBTItem(is);
							nbti.setBoolean("soulbound", true);
							is = nbti.getItem();
							if (op.get().getInventory().firstEmpty() == -1) {
								op.get().getWorld().dropItem(op.get().getLocation(), is);
							} else {
								op.get().getInventory().addItem(is);
							}
							op.get().sendMessage(ChatColor.DARK_AQUA + "You Have Been Given A SoulShard!");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Invalid Usage! /itemlevel souldbound <playernamme>");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("setlevel")) {
					ItemStack is = ((Player) sender).getItemInHand();
					Optional<CustomItem> oci = ItemUtil.getItem(is);
					if (oci.isPresent()) {
						try {
							if (args.length >= 2 && Integer.valueOf(args[1]) >= 1 && Integer.valueOf(args[1]) <= 30) {
								CustomItem ci = oci.get();
								ci.setLevel(Integer.valueOf(args[1]));
								ItemStack itemstack = ci.getItem();
								ItemMeta im = itemstack.getItemMeta();
								im.setLore(new ItemLore(ci).getLore());
								itemstack.setItemMeta(im);
								((Player) sender).setItemInHand(itemstack);
								sender.sendMessage(
										ChatColor.DARK_AQUA + "ItemInHand set to level " + Integer.valueOf(args[1]));
							} else {
								sender.sendMessage(ChatColor.RED + "Please Enter Valid Number!");
							}
						} catch (Exception e) {
							sender.sendMessage(
									ChatColor.RED + "ARE YOU TRYING TO BREAK THE PLUGIN BOI PUT IN A LEVEL NUMBER");
							return true;
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Item Cannot be setlevel");
					}
				} else if (args[0].equalsIgnoreCase("addworld")) {
					if (args.length >= 2) {
						ItemPlugin.getInstance().worldname.add(args[1]);
						sender.sendMessage(ChatColor.DARK_AQUA + "WorldAdded");
					} else {
						sender.sendMessage(ChatColor.RED + "Please Input a World Name");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("remworld")) {
					if (args.length >= 2) {
						ItemPlugin.getInstance().worldname.remove(args[1]);
						sender.sendMessage(ChatColor.DARK_AQUA + "WorldAdded");
					} else {
						sender.sendMessage(ChatColor.RED + "Please Input a World Name");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("listworld")) {
					sender.sendMessage(ChatColor.DARK_AQUA + ItemPlugin.getInstance().worldname.toString());
				} else if (args[0].equalsIgnoreCase("reload")) {
					sender.sendMessage(ChatColor.DARK_AQUA + "Reloading...");
					ItemPlugin.getInstance().saveFolder();
					ItemPlugin.getInstance().reloadConfig();
					ItemPlugin.getInstance().loadConfig();
					ItemPlugin.getInstance().loadFolder();
					ConfigData.loadConfigData(ItemPlugin.fileconfig);
					sender.sendMessage(ChatColor.DARK_AQUA + "Reloaded");
				} else if (args[0].equalsIgnoreCase("setsoulbound")) {
					try {
						System.out.println(args[1]);
						if (args.length >= 2 && Integer.valueOf(args[1]).intValue() >= 0 && Integer.valueOf(args[1]).intValue() <= 3) {
							Optional<CustomItem> opc = ItemUtil.getItem(((Player) sender).getItemInHand());
							if (opc.isPresent()) {
								CustomItem ci1 = opc.get();
								ci1.setSoulBound(Integer.valueOf(args[1]));
								ItemStack is1 = ci1.getItem();
								ItemMeta im = is1.getItemMeta();
								im.setLore(new ItemLore(ci1).getLore());
								is1.setItemMeta(im);
								((Player) sender).setItemInHand(is1);
								sender.sendMessage(ChatColor.DARK_AQUA+"SoulBound For Object In Hand Set to "+ Integer.valueOf(args[1]));
							} else {
								sender.sendMessage(ChatColor.RED + "Not A Valid Object To Set SoulBound");
							}

						}else{
							sender.sendMessage(ChatColor.RED+"WRONG ARGUMENTS use /itemlevel setsoulbound <level number(between 0,3>");
						}
					} catch (Exception e) {
						sender.sendMessage(
								ChatColor.RED + "ARE YOU TRYING TO BREAK THE PLUGIN BOI PUT IN A VALID NUMBER");
						return true;
					}
				}else {
					sender.sendMessage(ChatColor.RED + "Unrecognized Command type /itemlevel for help");
					return true;
				}
			} else {
				sender.sendMessage(ChatColor.GREEN + "/itemlevel soulbound <playername>");
				sender.sendMessage(ChatColor.GREEN + "/itemlevel setlevel <level>");
				sender.sendMessage(ChatColor.GREEN + "/itemlevel addworld <WorldName>");
				sender.sendMessage(ChatColor.GREEN + "/itemlevel remworld <WorldName>");
				sender.sendMessage(ChatColor.GREEN + "/itemlevel listworld");
				sender.sendMessage(ChatColor.GREEN + "/itemlevel reload");
				sender.sendMessage(ChatColor.GREEN + "/itemlevel setsouldbound <SoulBound Level>");
			}

		}
		return true;
	}
}
