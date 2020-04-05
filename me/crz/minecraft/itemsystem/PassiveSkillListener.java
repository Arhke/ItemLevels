package me.crz.minecraft.itemsystem;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.crz.minecraft.itemsystem.utils.Base;
import me.crz.minecraft.itemsystem.utils.ConfigData;
import me.crz.minecraft.itemsystem.utils.ItemLore;
import me.crz.minecraft.itemsystem.utils.ItemUtil;
import me.crz.minecraft.itemsystem.utils.MetaValue;

public class PassiveSkillListener extends Base {

	Random random = new Random();

	@EventHandler
	public void on(BlockBreakEvent e) {
		Optional<CustomItem> oci = ItemUtil.getItem(e.getPlayer().getItemInHand());
		if (oci.isPresent()) {
			CustomItem ci = oci.get();

//			if (ItemLore.ItemType.fromItem(e.getPlayer().getItemInHand()).equals(ItemLore.ItemType.HOE)) {
//
//				if (Arrays.asList(Material.POTATO, Material.CARROT, Material.CROPS, Material.MELON_BLOCK,
//						Material.NETHER_WARTS, Material.PUMPKIN).contains(e.getBlock().getType())) {
//
//					if (e.getBlock().getData() == 0x7) {
//
//						if (random.nextInt(100) <= ItemLore.percentage0(0, 20, ci.getLevel())) {
//							for (ItemStack item : e.getBlock().getDrops(e.getPlayer().getItemInHand()))
//								e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), item);
//						} else {
//							if (random.nextInt(100) <= ItemLore.percentage0(0, 15, ci.getLevel())) {
//								for (ItemStack item : e.getBlock().getDrops(e.getPlayer().getItemInHand()))
//									e.getBlock().getWorld().dropItem(e.getBlock().getLocation(),
//											new CItem(item).build(item.getAmount() * 3));
//							} else {
//								if (random.nextInt(100) <= ItemLore.percentage0(0, 5, ci.getLevel())) {
//									for (ItemStack item : e.getBlock().getDrops(e.getPlayer().getItemInHand()))
//										e.getBlock().getWorld().dropItem(e.getBlock().getLocation(),
//												new CItem(item).build(item.getAmount() * 5));
//								}
//							}
//						}
//
//					}
//
//				}
//
//			}

			if (ItemLore.ItemType.fromItem(e.getPlayer().getItemInHand()).equals(ItemLore.ItemType.SHOVEL)) {

				if (Arrays.asList(Material.GRASS, Material.GRAVEL, Material.DIRT, Material.SAND, Material.SOUL_SAND)
						.contains(e.getBlock().getType())) {

					if (ci.getLevel() >= 5) {
						if (random.nextInt(100) <= (double)ConfigData.shoveldoubledrop[ci.getLevel()-1])
							e.getBlock().getDrops(e.getPlayer().getItemInHand()).stream().forEach(
									drop -> e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), drop));

						if (ci.getLevel() >= 20) {
							if (random.nextInt(100) <= (double)ConfigData.expdrop[ci.getLevel()-1])
								e.setExpToDrop(e.getExpToDrop() + (random.nextInt(ci.getLevel())));

						}
					}

				}

			}

			if (ItemLore.ItemType.fromItem(e.getPlayer().getItemInHand()).equals(ItemLore.ItemType.AXE)) {
				if (e.getBlock().getType().equals(Material.LOG) || e.getBlock().getType().equals(Material.LOG_2)) {
					if (ci.getLevel() >= 15) {

						if (random.nextInt(100) <= (double)ConfigData.axedoubledrop[ci.getLevel()-1]) {
							e.getBlock().getWorld().dropItem(e.getBlock().getLocation(),
									e.getBlock().getDrops(e.getPlayer().getItemInHand()).stream().findFirst().get());
						}

						if (ci.getLevel() >= 25) {
							if (random.nextInt(100) <= (double)ConfigData.goldenapple[ci.getLevel()-1]) {
								e.getPlayer().getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
							}
						}
					}
				}
			}

			if (ItemLore.ItemType.fromItem(e.getPlayer().getItemInHand()).equals(ItemLore.ItemType.PICK)) {

				if (random.nextInt(100) <= (double)ConfigData.ironchance[ci.getLevel()-1])
					e.getPlayer().getInventory().addItem(new ItemStack(Material.IRON_ORE));
				if (random.nextInt(100) <= (double)ConfigData.goldchance[ci.getLevel()-1])
					e.getPlayer().getInventory().addItem(new ItemStack(Material.GOLD_ORE));
				if (random.nextInt(100) <= (double)ConfigData.diamondchance[ci.getLevel()-1])
					e.getPlayer().getInventory().addItem(new ItemStack(Material.DIAMOND));
				if (random.nextInt(100) <= (double)ConfigData.emeraldchance[ci.getLevel()-1])
					e.getPlayer().getInventory().addItem(new ItemStack(Material.EMERALD));

			}
		}

	}

	@EventHandler
	public void onBow(EntityShootBowEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			Optional<CustomItem> oci = ItemUtil.getItem(e.getBow());
			if (oci.isPresent()) {
				CustomItem ci = oci.get();

				// doesn't use arrow
				if (ci.getLevel() >= 5) {
					if (!e.getBow().containsEnchantment(Enchantment.ARROW_INFINITE))
						if (random.nextInt(100) <= (double)ConfigData.infinitychance[ci.getLevel()-1])
							p.getInventory().addItem(new ItemStack(Material.ARROW));

					if (ci.getLevel() >= 10) {
						Arrow arrow = (Arrow) e.getProjectile();
						if (random.nextInt(100) <= (double)ConfigData.lightningchance[ci.getLevel()-1]) {
							arrow.setMetadata("itemLevels_Lightning", new MetaValue());
						}

						if (ci.getLevel() >= 15) {
							if (random.nextInt(100) <= (double)ConfigData.slownesschance[ci.getLevel()-1]) {
								arrow.setMetadata("itemLevels_Slow", new MetaValue());
							}
						}

					}

				}

			}
		}
	}

	@EventHandler
	public void onArmor(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			Player op = (Player) e.getEntity();

			Optional<CustomItem> ohelmet = ItemUtil.getItem(op.getInventory().getHelmet());
			Optional<CustomItem> ochestplate = ItemUtil.getItem(op.getInventory().getChestplate());
			Optional<CustomItem> oleggings = ItemUtil.getItem(op.getInventory().getLeggings());
			Optional<CustomItem> oboots = ItemUtil.getItem(op.getInventory().getBoots());

			CustomItem helmet = null, chestplate = null, leggings = null, boots = null;

			if (ohelmet.isPresent()) {
				helmet = ohelmet.get();
			}
			if (ochestplate.isPresent()) {
				chestplate = ochestplate.get();
			}
			if (oleggings.isPresent()) {
				leggings = oleggings.get();
			}
			if (oboots.isPresent()) {
				boots = oboots.get();
			}

			

			if ((helmet != null && shouldRun((double)ConfigData.dodgechance[helmet.getLevel()]) && helmet.getLevel()>=10)
					|| (chestplate != null && shouldRun((double)ConfigData.dodgechance[chestplate.getLevel()]) && chestplate.getLevel()>=10)
					|| (leggings != null && shouldRun((double)ConfigData.dodgechance[leggings.getLevel()]) && leggings.getLevel()>=10)
					|| (boots != null && shouldRun((double)ConfigData.dodgechance[boots.getLevel()]) && boots.getLevel()>=10)) {
				e.setDamage(0);
				op.sendMessage(colour("&7You avoided the attack!"));
			}
			if ((helmet != null && shouldRun((double)ConfigData.firechance[helmet.getLevel()]) && helmet.getLevel()>=15)
					|| (chestplate != null && shouldRun((double)ConfigData.firechance[chestplate.getLevel()]) && chestplate.getLevel()>=15)
					|| (leggings != null && shouldRun((double)ConfigData.firechance[leggings.getLevel()]) && leggings.getLevel()>=15)
					|| (boots != null && shouldRun((double)ConfigData.firechance[boots.getLevel()]) && boots.getLevel()>=15)) {
				p.setFireTicks(40);
				op.sendMessage(colour("&7You set the enemy on &cfire&7!"));
			}
			if ((helmet != null && shouldRun((double)ConfigData.blindchance[helmet.getLevel()]) && helmet.getLevel()>=30)
					|| (chestplate != null && shouldRun((double)ConfigData.blindchance[chestplate.getLevel()]) && chestplate.getLevel()>=30)
					|| (leggings != null && shouldRun((double)ConfigData.blindchance[leggings.getLevel()]) && leggings.getLevel()>=30)
					|| (boots != null && shouldRun((double)ConfigData.blindchance[boots.getLevel()]) && boots.getLevel()>=30)) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10*20, 10));
				op.sendMessage(colour("&7You blinded the enemy&7!"));
			}
		}
	}

	public boolean shouldRun(double percentage) {
		return random.nextInt(100) <= percentage;
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
			Player p = (Player) e.getDamager();

			Optional<CustomItem> oci = ItemUtil.getItem(p.getItemInHand());
			if (oci.isPresent()) {
				CustomItem ci = oci.get();
				if (ItemLore.ItemType.fromItem(p.getItemInHand()).equals(ItemLore.ItemType.SWORD)) {
					if (ci.getLevel() >= 5) {
						if (e.getEntity() instanceof Player) {
							if (random.nextInt(100) <= (double)ConfigData.playerbleed[ci.getLevel()-1]) {
								Bleed.BleedEffect((LivingEntity)e.getEntity(), 4);
							}
						} else {
							if (ci.getLevel() >= 15)
								if (random.nextInt(100) <= (double)ConfigData.mobbleed[ci.getLevel()-1]) {
									Bleed.BleedEffect((LivingEntity)e.getEntity(), 4);
								}
						}

					}
				}
			}
		}
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			Optional<CustomItem> oci = ItemUtil.getItem(p.getItemInHand());
			if (oci.isPresent()) {
				CustomItem ci = oci.get();
				if (ItemLore.ItemType.fromItem(p.getItemInHand()).equals(ItemLore.ItemType.AXE)) {
					if (ci.getLevel() >= 25) {
						if (random.nextInt(100) <= (double)ConfigData.doubledamage[ci.getLevel()-1]) {
							e.setDamage(e.getDamage() * 2);
						}
					}
				}
			}

			if (e.getDamager() instanceof Arrow) {
				p.sendMessage("e.getDamager() instanceof Arrow");
				
				Arrow arrow = (Arrow) e.getDamager();
				if (arrow.hasMetadata("itemLevels_Lightning")) {
					e.getEntity().getWorld().strikeLightning(e.getEntity().getLocation());
				}
				if (arrow.hasMetadata("itemLevels_Slow")) {
					if (e.getEntity() instanceof Player) {
						Player player = (Player) e.getEntity();
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 10, 3));
					}
					if (e.getEntity() instanceof Monster) {
						Monster monster = (Monster) e.getEntity();
						monster.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 10, 3));
					}
				}
			}
		}
	}

}
