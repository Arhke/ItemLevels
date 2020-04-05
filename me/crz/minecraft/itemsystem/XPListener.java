package me.crz.minecraft.itemsystem;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.itemnbtapi.NBTItem;
import me.crz.minecraft.itemsystem.utils.Base;
import me.crz.minecraft.itemsystem.utils.ItemLore;
import me.crz.minecraft.itemsystem.utils.ItemLoseDurabilityEvent;
import me.crz.minecraft.itemsystem.utils.ItemUtil;

public class XPListener extends Base {

	@EventHandler
	public void on(ItemLoseDurabilityEvent e) {

		if (e.getItem() == null)
			return;
		Optional<CustomItem> oci = ItemUtil.getItem(e.getItem());

		if (oci.isPresent()) {
			CustomItem ci = oci.get();
			if (ci.getLevel() < 30)
				ci.setXP(ci.getXP() + 1);
			if (e.getPlayer().getInventory().contains(e.getItem())
					|| Arrays.asList(e.getPlayer().getInventory().getArmorContents()).contains(e.getItem())) {
				ItemStack item = ci.getItem();
				e.getItem().setDurability((short) (item.getDurability() + 1));
				ItemMeta im = item.getItemMeta();
				im.setLore(new ItemLore(ci).getLore());

				item.setItemMeta(im);
				e.getItem().setItemMeta(im);
				e.change();
				// e.getPlayer().getInventory().setItem(index, item);
				if (ci.getLevel() < 30 && ci.getXP() > 0) {

				}
			}
		}
	}

	@EventHandler
	public void on(PlayerItemDamageEvent e) {
		ItemLoseDurabilityEvent event = new ItemLoseDurabilityEvent(e.getItem(), e.getPlayer());
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();

			if (p.getInventory().getHelmet() != null) {
				ItemStack helmet = p.getInventory().getHelmet();
				if (helmet.getType().getMaxDurability() > 0) {
					ItemLoseDurabilityEvent event = new ItemLoseDurabilityEvent(helmet, p);
					Bukkit.getServer().getPluginManager().callEvent(event);
					if (event.isChanged()) {
						p.getInventory().setHelmet(event.getItem());
					}
				}
			}

			if (p.getInventory().getChestplate() != null) {
				ItemStack chestplate = p.getInventory().getChestplate();

				if (chestplate.getType().getMaxDurability() > 0) {
					ItemLoseDurabilityEvent event = new ItemLoseDurabilityEvent(chestplate, p);
					Bukkit.getServer().getPluginManager().callEvent(event);
					if (event.isChanged())
						p.getInventory().setChestplate(event.getItem());
				}
			}

			if (p.getInventory().getLeggings() != null) {
				ItemStack leggings = p.getInventory().getLeggings();

				if (leggings.getType().getMaxDurability() > 0) {
					ItemLoseDurabilityEvent event = new ItemLoseDurabilityEvent(leggings, p);
					Bukkit.getServer().getPluginManager().callEvent(event);
					if (event.isChanged())
						p.getInventory().setLeggings(event.getItem());

				}
			}

			if (p.getInventory().getBoots() != null) {
				ItemStack boots = p.getInventory().getBoots();

				if (boots.getType().getMaxDurability() > 0) {
					ItemLoseDurabilityEvent event = new ItemLoseDurabilityEvent(boots, p);
					Bukkit.getServer().getPluginManager().callEvent(event);
					if (event.isChanged())
						p.getInventory().setBoots(event.getItem());
				}
			}
		}
	}

	/*
	 * @EventHandler public void on(EntityShootBowEvent e) { if (e.getEntity()
	 * instanceof Player) { Player p = (Player) e.getEntity(); Optional<CustomItem>
	 * oci = ItemUtil.getItem(e.getBow()); if (oci.isPresent()) { CustomItem ci =
	 * oci.get(); ci.setXP(ci.getXP() + (3000));
	 * 
	 * ItemStack item = ci.getItem(); item.setDurability((short)
	 * (item.getDurability() + 1)); ItemMeta im = item.getItemMeta(); im.setLore(new
	 * ItemLore(ci).getLore()); item.setItemMeta(im); p.setItemInHand(item);
	 * tb.withActionbar(colour("&7XP: &6" + ci.getXP() + "&7 / &6" + ((int)
	 * CustomItem.baseXp * (CustomItem.levelMultiplier * ci.getLevel())) +
	 * "   &7Level: &6" + ci.getLevel())); tb.withTimings(10, 20, 10); tb.send(p); }
	 * } }
	 */
	@EventHandler
	public void on(EnchantItemEvent e) {
		if (ItemUtil.doesItemHaveXP(e.getItem())) {
			Optional<CustomItem> oci = ItemUtil.getItem(e.getItem());
			if (oci.isPresent()) {
				CustomItem ci = oci.get();
				int totalEnchants = e.getItem().getEnchantments().size();

				if (totalEnchants + e.getEnchantsToAdd().size() > ci.getEnchantSlots()) {
					e.setCancelled(true);
					if (totalEnchants + 1 <= ci.getEnchantSlots()) {
						for (Entry<Enchantment, Integer> ench : e.getEnchantsToAdd().entrySet()) {
							if (totalEnchants > ci.getEnchantSlots())
								break;
							e.getItem().addEnchantment(ench.getKey(), ench.getValue());
							totalEnchants++;
						}
					}
				}

			}
		}
	}

	public static boolean isPickUp(InventoryAction ia) {
		return ia == InventoryAction.PICKUP_ALL || ia == InventoryAction.PICKUP_HALF || ia == InventoryAction.PICKUP_ONE
				|| ia == InventoryAction.PICKUP_SOME;
	}

	public static boolean isPlace(InventoryAction ia) {
		return ia == InventoryAction.PLACE_ALL || ia == InventoryAction.PLACE_ONE || ia == InventoryAction.PLACE_SOME;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClick(InventoryClickEvent e) {
		if (!e.isCancelled()) {
			HumanEntity ent = e.getWhoClicked();

			if (ent instanceof Player) {
				Inventory inv = e.getInventory();

				if (inv instanceof AnvilInventory) {
					AnvilInventory anvil = (AnvilInventory) inv;
					InventoryView view = e.getView();
					int rawSlot = e.getRawSlot();
					ItemStack[] items = anvil.getContents();
					if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && e.getRawSlot() != 0
							&& e.getRawSlot() != 1
							&& (anvil.getContents()[0] != null || anvil.getContents()[1] != null)) {
						if (anvil.getContents()[0] != null) {
							ItemStack is1 = anvil.getItem(0);
							ItemStack is2 = e.getCurrentItem();
							Optional<CustomItem> oci1 = ItemUtil.getItem(is1);
							Optional<CustomItem> oci2 = ItemUtil.getItem(is2);
							if (oci1.isPresent()) {
								NBTItem nbti = new NBTItem(is2);
								if (nbti.hasKey("soulbound") && nbti.getBoolean("soulbound")) {
									CustomItem ci1 = oci1.get();
									if (ci1.countSoulBound() < 3) {
										if (((Player) ent).getLevel() < ItemPlugin.fileconfig.getInt("souldbcost")) {
											e.getView().close();
											((Player) ent).sendMessage(ChatColor.RED + "Not Enough Exp");
										} else {
											((Player) ent).sendMessage(
													ChatColor.DARK_AQUA + "SoulShard Successfully combined with item");
											((Player) ent).giveExpLevels(-ItemPlugin.fileconfig.getInt("souldbcost"));
											ci1.setSoulBound(ci1.countSoulBound() + 1);
											ItemStack itemset = ci1.getItem();
											ItemMeta im = itemset.getItemMeta();
											im.setLore(new ItemLore(ci1).getLore());
											itemset.setItemMeta(im);
											anvil.setItem(0, itemset);
											if (is2.getAmount() <= 1) {
												
												e.setCurrentItem(null);
											} else {
												is2.setAmount(is2.getAmount() - 1);
												e.setCurrentItem(is2);
											}
										}
									} else {
										((Player) ent)
												.sendMessage(ChatColor.RED + "Too Many SoulBounds Already Queued");
									}
								}
							} else if (oci2.isPresent()) {
								NBTItem nbti = new NBTItem(is1);
								if (nbti.hasKey("soulbound") && nbti.getBoolean("soulbound")) {
									CustomItem ci2 = oci2.get();
									if (ci2.countSoulBound() < 3) {
										if (((Player) ent).getLevel() < ItemPlugin.fileconfig.getInt("souldbcost")) {
											e.getView().close();
											((Player) ent).sendMessage(ChatColor.RED + "Not Enough Exp");
										} else {
											((Player) ent).giveExpLevels(-ItemPlugin.fileconfig.getInt("souldbcost"));
											ci2.setSoulBound(ci2.countSoulBound() + 1);
											if (is1.getAmount() <= 1) {
												anvil.setItem(0, null);
											} else {
												is1.setAmount(is1.getAmount() - 1);
											}
											ItemStack itemset = ci2.getItem();
											ItemMeta im = itemset.getItemMeta();
											im.setLore(new ItemLore(ci2).getLore());
											itemset.setItemMeta(im);
											e.setCurrentItem(itemset);
											// e.setCurrentItem(null);
											e.setCursor(null);
											((Player) ent).sendMessage(
													ChatColor.DARK_AQUA + "SoulShard Successfully combined with item");

										}
									} else {
										((Player) ent)
												.sendMessage(ChatColor.RED + "Too Many SoulBounds Already Queued");
									}
								}
							}
						}else {
							ItemStack is1 = anvil.getItem(1);
							ItemStack is2 = e.getCurrentItem();
							Optional<CustomItem> oci1 = ItemUtil.getItem(is1);
							Optional<CustomItem> oci2 = ItemUtil.getItem(is2);
							if (oci1.isPresent()) {
								NBTItem nbti = new NBTItem(is2);
								if (nbti.hasKey("soulbound") && nbti.getBoolean("soulbound")) {
									CustomItem ci1 = oci1.get();
									if (ci1.countSoulBound() < 3) {
										if (((Player) ent).getLevel() < ItemPlugin.fileconfig.getInt("souldbcost")) {
											e.getView().close();
											((Player) ent).sendMessage(ChatColor.RED + "Not Enough Exp");
										} else {
											((Player) ent).sendMessage(
													ChatColor.DARK_AQUA + "SoulShard Successfully combined with item");
											((Player) ent).giveExpLevels(-ItemPlugin.fileconfig.getInt("souldbcost"));
											ci1.setSoulBound(ci1.countSoulBound() + 1);
											ItemStack itemset = ci1.getItem();
											ItemMeta im = itemset.getItemMeta();
											im.setLore(new ItemLore(ci1).getLore());
											itemset.setItemMeta(im);
											anvil.setItem(1, itemset);
											if (is2.getAmount() <= 1) {
												anvil.setItem(0, null);
												e.setCurrentItem(null);
											} else {
												is2.setAmount(is2.getAmount() - 1);
											}
										}
									} else {
										((Player) ent)
												.sendMessage(ChatColor.RED + "Too Many SoulBounds Already Queued");
									}
								}
							} else if (oci2.isPresent()) {
								NBTItem nbti = new NBTItem(is1);
								if (nbti.hasKey("soulbound") && nbti.getBoolean("soulbound")) {
									CustomItem ci2 = oci2.get();
									if (ci2.countSoulBound() < 3) {
										if (((Player) ent).getLevel() < ItemPlugin.fileconfig.getInt("souldbcost")) {
											e.getView().close();
											((Player) ent).sendMessage(ChatColor.RED + "Not Enough Exp");
										} else {
											((Player) ent).giveExpLevels(-ItemPlugin.fileconfig.getInt("souldbcost"));
											ci2.setSoulBound(ci2.countSoulBound() + 1);
											if (is1.getAmount() <= 1) {
												anvil.setItem(1, null);
											} else {
												is1.setAmount(is1.getAmount() - 1);
											}
											ItemStack itemset = ci2.getItem();
											ItemMeta im = itemset.getItemMeta();
											im.setLore(new ItemLore(ci2).getLore());
											itemset.setItemMeta(im);
											e.setCurrentItem(itemset);
											// e.setCurrentItem(null);
											((Player) ent).sendMessage(
													ChatColor.DARK_AQUA + "SoulShard Successfully combined with item");

										}
									} else {
										((Player) ent)
												.sendMessage(ChatColor.RED + "Too Many SoulBounds Already Queued");
									}
								}
							}
						}
					} else if (isPlace(e.getAction()) && (e.getRawSlot() == 0 || e.getRawSlot() == 1)
							&& (anvil.getContents()[0] != null || anvil.getContents()[1] != null)) {
						if (e.getRawSlot() == 0) {
							ItemStack is1 = anvil.getItem(1);
							ItemStack is2 = e.getCursor();
							Optional<CustomItem> oci1 = ItemUtil.getItem(is1);
							Optional<CustomItem> oci2 = ItemUtil.getItem(is2);
							if (oci1.isPresent()) {
								NBTItem nbti = new NBTItem(is2);
								if (nbti.hasKey("soulbound") && nbti.getBoolean("soulbound")) {
									CustomItem ci1 = oci1.get();
									if (ci1.countSoulBound() < 3) {
										if (((Player) ent).getLevel() < ItemPlugin.fileconfig.getInt("souldbcost")) {
											e.getView().close();
											((Player) ent).sendMessage(ChatColor.RED + "Not Enough Exp");
										} else {
											((Player) ent).sendMessage(
													ChatColor.DARK_AQUA + "SoulShard Successfully combined with item");
											((Player) ent).giveExpLevels(-ItemPlugin.fileconfig.getInt("souldbcost"));
											ci1.setSoulBound(ci1.countSoulBound() + 1);
											ItemStack itemset = ci1.getItem();
											ItemMeta im = itemset.getItemMeta();
											im.setLore(new ItemLore(ci1).getLore());
											itemset.setItemMeta(im);
											anvil.setItem(1, itemset);
											if (is2.getAmount() <= 1) {
												anvil.setItem(0, null);
												e.setCursor(null);
											} else {
												is2.setAmount(is1.getAmount() - 1);
											}
										}
									} else {
										((Player) ent)
												.sendMessage(ChatColor.RED + "Too Many SoulBounds Already Queued");
									}
								}
							} else if (oci2.isPresent()) {
								NBTItem nbti = new NBTItem(is1);
								if (nbti.hasKey("soulbound") && nbti.getBoolean("soulbound")) {
									CustomItem ci2 = oci2.get();
									if (ci2.countSoulBound() < 3) {
										if (((Player) ent).getLevel() < ItemPlugin.fileconfig.getInt("souldbcost")) {
											e.getView().close();
											((Player) ent).sendMessage(ChatColor.RED + "Not Enough Exp");
										} else {
											((Player) ent).giveExpLevels(-ItemPlugin.fileconfig.getInt("souldbcost"));
											ci2.setSoulBound(ci2.countSoulBound() + 1);
											if (is1.getAmount() <= 1) {
												anvil.setItem(1, null);
											} else {
												is1.setAmount(is1.getAmount() - 1);
											}
											ItemStack itemset = ci2.getItem();
											ItemMeta im = itemset.getItemMeta();
											im.setLore(new ItemLore(ci2).getLore());
											itemset.setItemMeta(im);
											anvil.setItem(0, itemset);
											// e.setCurrentItem(null);
											e.setCursor(null);
											((Player) ent).sendMessage(
													ChatColor.DARK_AQUA + "SoulShard Successfully combined with item");

										}
									} else {
										((Player) ent)
												.sendMessage(ChatColor.RED + "Too Many SoulBounds Already Queued");
									}
								}
							} else {

							}
						} else if (rawSlot == 1) {
							ItemStack is1 = anvil.getItem(0);
							ItemStack is2 = e.getCursor();
							Optional<CustomItem> oci1 = ItemUtil.getItem(is1);
							Optional<CustomItem> oci2 = ItemUtil.getItem(is2);
							if (oci1.isPresent()) {
								NBTItem nbti = new NBTItem(is2);
								if (nbti.hasKey("soulbound") && nbti.getBoolean("soulbound")) {
									CustomItem ci1 = oci1.get();
									if (ci1.countSoulBound() < 3) {
										if (((Player) ent).getLevel() < ItemPlugin.fileconfig.getInt("souldbcost")) {
											e.getView().close();
											((Player) ent).sendMessage(ChatColor.RED + "Not Enough Exp");
										} else {
											((Player) ent).sendMessage(
													ChatColor.DARK_AQUA + "SoulShard Successfully combined with item");
											((Player) ent).giveExpLevels(-ItemPlugin.fileconfig.getInt("souldbcost"));
											ci1.setSoulBound(ci1.countSoulBound() + 1);
											ItemStack itemset = ci1.getItem();
											ItemMeta im = itemset.getItemMeta();
											im.setLore(new ItemLore(ci1).getLore());
											itemset.setItemMeta(im);
											anvil.setItem(0, itemset);
											if (is2.getAmount() <= 1) {
												anvil.setItem(1, null);
												e.setCursor(null);
											} else {
												is2.setAmount(is2.getAmount() - 1);
											}
										}
									} else {
										((Player) ent)
												.sendMessage(ChatColor.RED + "Too Many SoulBounds Already Queued");
									}
								}
							} else if (oci2.isPresent()) {
								NBTItem nbti = new NBTItem(is1);
								if (nbti.hasKey("soulbound") && nbti.getBoolean("soulbound")) {
									CustomItem ci2 = oci2.get();
									if (ci2.countSoulBound() < 3) {
										if (((Player) ent).getLevel() < ItemPlugin.fileconfig.getInt("souldbcost")) {
											e.getView().close();
											((Player) ent).sendMessage(ChatColor.RED + "Not Enough Exp");
										} else {
											((Player) ent).giveExpLevels(-ItemPlugin.fileconfig.getInt("souldbcost"));
											ci2.setSoulBound(ci2.countSoulBound() + 1);
											if (is1.getAmount() <= 1) {
												anvil.setItem(0, null);
											} else {
												is1.setAmount(is1.getAmount() - 1);
											}
											ItemStack itemset = ci2.getItem();
											ItemMeta im = itemset.getItemMeta();
											im.setLore(new ItemLore(ci2).getLore());
											itemset.setItemMeta(im);
											anvil.setItem(1, itemset);
											// e.setCurrentItem(null);
											e.setCursor(null);
											((Player) ent).sendMessage(
													ChatColor.DARK_AQUA + "SoulShard Successfully combined with item");

										}
									} else {
										((Player) ent)
												.sendMessage(ChatColor.RED + "Too Many SoulBounds Already Queued");
									}
								}
							} else {

							}
						}
					}
					if (rawSlot == view.convertSlot(rawSlot)) {

						if (rawSlot == 2) {

							ItemStack item = e.getCurrentItem();

							if (item != null) {

								if (ItemUtil.doesItemHaveXP(item)) {
									Optional<CustomItem> oci = ItemUtil.getItem(item);
									if (oci.isPresent()) {
										if (items[0].getEnchantments().size() != item.getEnchantments().size()) {
											CustomItem ci = oci.get();

											if (item.getEnchantments().size() > ci.getEnchantSlots()) {
												e.setCancelled(true);
												ent.sendMessage(colour("&cYou have no enchantment slots available."));
												((Player) ent).closeInventory();
											} else {
												ItemMeta im = item.getItemMeta();
												im.setLore(new ItemLore(ci).getLore());
												item.setItemMeta(im);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

}
