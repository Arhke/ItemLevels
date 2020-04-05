package me.crz.minecraft.itemsystem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.effect.ExplodeEffect;
import de.slikey.effectlib.effect.TraceEffect;
import de.slikey.effectlib.effect.VortexEffect;
import de.tr7zw.itemnbtapi.NBTItem;
import me.crz.minecraft.itemsystem.utils.Base;
import me.crz.minecraft.itemsystem.utils.ItemLore;
import me.crz.minecraft.itemsystem.utils.ItemLore.ItemType;
import me.crz.minecraft.itemsystem.utils.ItemUtil;
import me.crz.minecraft.itemsystem.utils.Ray;
import me.crz.minecraft.itemsystem.utils.TreeCutter;
import me.crz.minecraft.itemsystem.utils.VeinBreakBlock;

@SuppressWarnings("unused")
public class ActiveItem extends Base {

	public enum active {
		// AXE
		TIMBER(10, 10), TOSS(15), BOLT(20), SHUFFLE(30), // timber done in blockbreak
		// PICKAXE
		RESERVE(5, 10), CRUSH(15, 10), VEINMINE(30), // veinmine done in blockbreak, reserve done in blockbreak
		// SHOVEL
		EXCAVATION(15, 30), TREASURE(30, 45), // treasure done in blockbreak
		// SWORD
		SLAY(10, 10), SLAM(20, 3), RAGE(25, 15), INSIGHT(30, 10),
		// HOE
		HARVEST(5), GROW(10), PLANT(15, 15), REAPER(30, 10),
		// BOW
		MULTISHOT(10, 30), EXPLOSIVE(15, 15), FIREWORK(20, 30), POISON_ARROW(25, 5), AUTO_FIRE(30, 10), NONE(-1);

		int startLevel;
		int durationSeconds;
		active(int startLevel) {
			this(startLevel, -1);
		}
		active(int startLevel, int durationSeconds) {
			this.startLevel = startLevel;
			this.durationSeconds = durationSeconds;
		}

		public int getStartLevel() {
			return this.startLevel;
		}

		public int getDurationSeconds() {
			return this.durationSeconds;
		}

		public boolean isSelected(CustomItem item) {
			return item.getActiveAbility().equals(this);
		}

		public boolean canSelect(CustomItem item) {
			if (item.getAbilityCooldowns().containsKey(this.name())) {
				if (item.getAbilityCooldowns().get(this.name()) < System.currentTimeMillis())
					return true;
				return false;
			}
			return true;
		}

		public boolean isActive(CustomItem item) {
			return item.getActivatedAbility().equals(this);
		}

		// activeAbility
		public void select(CustomItem item) {
			item.setActiveAbility(this);
		}

		public void activate(CustomItem item) {
			item.setActivatedAbility(this);
		}

		public static active getNext(CustomItem item) {
			List<active> a = getAbilities(item);
			if (a.indexOf(item.getActiveAbility()) == a.size() - 1)
				return a.get(0);
			return a.get(a.indexOf(item.getActiveAbility()) + 1);
		}

		public static List<active> getAbilities(CustomItem item) {
			return getAbilities0(item).stream().filter((a) -> a.startLevel <= item.getLevel())
					.collect(Collectors.toList());
		}

		public static List<active> getAbilities0(CustomItem item) {
			ItemType type = ItemType.fromItem(item.getItem());
			if (type.equals(ItemType.ARMOR))
				return Arrays.asList(NONE);
			if (type.equals(ItemType.AXE))
				return Arrays.asList(NONE, TIMBER, TOSS, BOLT, SHUFFLE);
			if (type.equals(ItemType.PICK))
				return Arrays.asList(NONE, RESERVE, CRUSH, VEINMINE);
			if (type.equals(ItemType.SHOVEL))
				return Arrays.asList(NONE, EXCAVATION, TREASURE);
			if (type.equals(ItemType.HOE))
				return Arrays.asList(NONE, HARVEST, GROW, PLANT, REAPER);
			if (type.equals(ItemType.SWORD))
				return Arrays.asList(NONE, SLAY, SLAM, RAGE, INSIGHT);
			if (type.equals(ItemType.BOW))
				return Arrays.asList(NONE, MULTISHOT, EXPLOSIVE, FIREWORK, POISON_ARROW, AUTO_FIRE);
			return Arrays.asList(NONE);
		}

	}

	public static ItemStack setCoolDown(String s, ItemStack is) {
		NBTItem nbti = new NBTItem(is);
		nbti.setLong(s + "cd",
				System.currentTimeMillis() + (ItemPlugin.fileconfig.getInt("actives." + s + ".activetime")
						+ ItemPlugin.fileconfig.getInt("actives." + s + ".cooldown")) * 1000);
		if (s.equals(active.POISON_ARROW.name().toLowerCase())||s.equals(active.BOLT.name().toLowerCase())) {
			nbti.setLong(s + "cd",
					System.currentTimeMillis() + (ItemPlugin.fileconfig.getInt("actives." + s + ".cooldown")) * 1000);
		}
		return nbti.getItem();
	}

	public static ItemStack setActive(String s, ItemStack is) {
		NBTItem nbti = new NBTItem(is);
		nbti.setLong(s + "a",
				System.currentTimeMillis() + ItemPlugin.fileconfig.getInt("actives." + s + ".activetime") * 1000);
		return nbti.getItem();
	}

	public static ItemStack setCoolDown30(String s, ItemStack is) {
		NBTItem nbti = new NBTItem(is);
		nbti.setLong(s + "cd",
				System.currentTimeMillis() + (ItemPlugin.fileconfig.getInt("actives." + s + ".activetime30")
						+ ItemPlugin.fileconfig.getInt("actives." + s + ".cooldown30")) * 1000);
		if (s.equals(active.POISON_ARROW.name().toLowerCase())||s.equals(active.BOLT.name().toLowerCase())) {
			nbti.setLong(s + "cd",
					System.currentTimeMillis() + (ItemPlugin.fileconfig.getInt("actives." + s + ".cooldown30")) * 1000);
		}
		return nbti.getItem();
	}

	public static ItemStack setActive30(String s, ItemStack is) {
		NBTItem nbti = new NBTItem(is);
		nbti.setLong(s + "a",
				System.currentTimeMillis() + ItemPlugin.fileconfig.getInt("actives." + s + ".activetime30") * 1000);
		return nbti.getItem();
	}

	public static int checkCoolDown(String s, ItemStack is) {
		NBTItem nbti = new NBTItem(is);
		if (nbti.hasKey(s + "cd") && nbti.getLong(s + "cd") > System.currentTimeMillis()) {
			return (int) ((nbti.getLong(s + "cd") - System.currentTimeMillis()) / 1000);
		} else {
			return -1;
		}
	}

	public static int checkActives(String s, ItemStack is) {
		NBTItem nbti = new NBTItem(is);
		if (nbti.hasKey(s + "a") && nbti.getLong(s + "a") > System.currentTimeMillis()) {
			return (int) ((nbti.getLong(s + "a") - System.currentTimeMillis()) / 1000);
		} else {
			return -1;
		}
	}

	public static Map<Item, Player> axeset = new HashMap<>();

	public static ItemStack[] shuffleArray(ItemStack[] ar) {
		Random rnd = ThreadLocalRandom.current();
		for (int i = ar.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			ItemStack a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
		return ar;
	}

	public static Vector spawnThor(Player p, Location loc) {

		ItemStack is = new ItemStack(p.getItemInHand().getType());
		Item i = p.getLocation().getWorld().dropItem(p.getEyeLocation(), is);
		i.setTicksLived(50);
		i.setFallDistance(0);
		i.setPickupDelay(100);
		Vector v;
		i.setVelocity(
				v = new Vector(1.5 * -Math.cos(Math.toRadians(loc.getPitch())) * Math.sin(Math.toRadians(loc.getYaw())),
						-1.5 * Math.sin(Math.toRadians(loc.getPitch())),
						1.5 * Math.cos(Math.toRadians(loc.getPitch())) * Math.cos(Math.toRadians(loc.getYaw()))));
		BukkitTask bt = new BukkitRunnable() {
			Vector v = i.getVelocity();

			@Override
			public void run() {
				i.setVelocity(v);
				// i.teleport(i.getLocation().add(0, 0.5, 0));
				if (i.isOnGround()) {
					this.cancel();
				}
			}
		}.runTaskTimer(ItemPlugin.getInstance(), 0L, 1L);
		new BukkitRunnable() {
			@Override
			public void run() {
				i.remove();
				axeset.remove(i);
				bt.cancel();
			}
		}.runTaskLater(ItemPlugin.getInstance(), 50);

		for (LivingEntity le : new Ray(i.getLocation(), i.getLocation().add(i.getVelocity().getX() * 50,
				i.getVelocity().getY() * 50, i.getVelocity().getZ() * 50)).removeAllNonColliding()) {
			if (!le.equals(p))
				le.damage(10, p);
		}
		TraceEffect effect = new TraceEffect(ItemPlugin.EM);
		effect.setLocation(p.getEyeLocation());
		effect.setEntity(i);
		effect.color = Color.AQUA;
		effect.start();
		axeset.put(i, p);
		return v;

	}

	public static Map<UUID, Set<ItemStack>> deathmap = new HashMap<>();

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (e.getKeepInventory()) {
			return;
		}
		if (e.getEntity() instanceof Player
				&& ItemPlugin.getInstance().worldname.contains(e.getEntity().getWorld().getName())) {
			Set<ItemStack> sis = new HashSet<>();
			Iterator<ItemStack> dropiterator = e.getDrops().iterator();
			while(dropiterator.hasNext()) {
				ItemStack is = dropiterator.next();
				Optional<CustomItem> oci = ItemUtil.getItem(is);
				if (oci.isPresent() && oci.get().countSoulBound() > 0) {
					oci.get().setSoulBound(oci.get().countSoulBound() - 1);
					ItemStack itemstack = oci.get().getItem();
					ItemMeta im = itemstack.getItemMeta();
					im.setLore(new ItemLore(oci.get()).getLore());
					itemstack.setItemMeta(im);
					sis.add(itemstack);
					dropiterator.remove();
				}
			}
			for (ItemStack is : ((Player) e.getEntity()).getInventory().getArmorContents()) {
				Optional<CustomItem> oci = ItemUtil.getItem(is);
				if (oci.isPresent() && oci.get().countSoulBound() > 0) {
					oci.get().setSoulBound(oci.get().countSoulBound() - 1);
					sis.add(is);
				}
			}
			deathmap.put(((Player) e.getEntity()).getUniqueId(), sis);

		} else if (e.getEntity() instanceof LivingEntity) {
			Player p = e.getEntity().getKiller();
			if (p != null && ItemType.fromItem(p.getItemInHand()) != null) {
				CustomItem item = ItemUtil.getItem(p.getItemInHand()).get();
				active a = item.getActiveAbility();
				if (a != null && a.equals(active.INSIGHT)) {

					e.setDroppedExp(e.getDroppedExp() * 3);
				}
			}
		}
	}

	@EventHandler
	public void respawn(PlayerRespawnEvent e) {
		Set<ItemStack> sis = deathmap.get(e.getPlayer().getUniqueId());
		if (sis != null) {
			for (ItemStack is : sis) {
				e.getPlayer().getInventory().addItem(is);
			}
			deathmap.remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onProjectile(ProjectileHitEvent e) {
		if (e.getEntity() instanceof Arrow) {
			String s = PoisonExplosiveIndex.get(e.getEntity().getUniqueId());
			if (s != null && s.equals("explosive")) {
				PoisonExplosiveIndex.remove(e.getEntity().getUniqueId());
				Effect effect = new ExplodeEffect(ItemPlugin.EM);
				effect.setLocation(e.getEntity().getLocation());
				effect.iterations = 20;
				effect.start();
				for (Entity ent : e.getEntity().getNearbyEntities(5d, 5d, 5d)) {
					if (ent instanceof LivingEntity) {
						((LivingEntity) ent).damage(8d);
						((LivingEntity) ent).setVelocity(new Vector(0, 1, 0));
					}
				}
			} else if (s != null && s.equals("firework")) {
				PoisonExplosiveIndex.remove(e.getEntity().getUniqueId());
				Arrow arrow = (Arrow) e.getEntity();
				Firework fw = (Firework) arrow.getWorld().spawnEntity(arrow.getLocation(), EntityType.FIREWORK);
				FireworkMeta fwm = fw.getFireworkMeta();
				Random r = new Random();

				// Get the type
				int rt = r.nextInt(5) + 1;
				Type type = Type.BALL;
				if (rt == 1)
					type = Type.BALL;
				if (rt == 2)
					type = Type.BALL_LARGE;
				if (rt == 3)
					type = Type.BURST;
				if (rt == 4)
					type = Type.CREEPER;
				if (rt == 5)
					type = Type.STAR;
				Color c1 = getColor(r.nextInt(17) + 1);
				Color c2 = getColor(r.nextInt(17) + 1);

				// Create our effect with this
				FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2)
						.with(type).trail(r.nextBoolean()).build();

				// Then apply the effect to the meta
				fwm.addEffect(effect);
				// Generate some random power and set it
				int rp = r.nextInt(2) + 1;
				fwm.setPower(rp);

				// Then apply this to our rocket
				fw.setFireworkMeta(fwm);
				new BukkitRunnable() {
					@Override
					public void run() {
						fw.detonate();
					}
				}.runTaskLater(ItemPlugin.getInstance(), 1L);

				for (Entity ent : arrow.getNearbyEntities(5d, 5d, 5d)) {
					if (ent instanceof LivingEntity) {
						if (((LivingEntity) ent).getHealth() <= 1) {
							((LivingEntity) ent).setHealth(0);
						} else {
							((LivingEntity) ent).setHealth(((LivingEntity) ent).getHealth() - 1d);
							((LivingEntity) ent).damage(0.01d);
						}

					}
				}
				FireworkIndex.remove((Arrow) e.getEntity());
			}
		}
	}

	@EventHandler
	public void ondamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof LivingEntity) {
			if (e.getDamager() instanceof Player) {
				Player p = (Player) e.getDamager();
				if (ItemType.fromItem(p.getItemInHand()) != null) {
					CustomItem item = ItemUtil.getItem(p.getItemInHand()).get();
					active a = item.getActiveAbility();
					if (e.getEntity() instanceof Player && a != null && a.equals(active.SHUFFLE)) {

						int cooldown;
						if ((cooldown = checkCoolDown(a.name().toLowerCase(), p.getItemInHand())) != -1) {
							p.sendMessage(
									ChatColor.RED + a.name() + " Still in CoolDown, " + cooldown + " seconds left!");
							return;
						}
						if (item.getLevel() < 30) {
							p.setItemInHand(setCoolDown(a.name().toLowerCase(), p.getItemInHand()));
						} else if (item.getLevel() >= 30) {

							p.setItemInHand(setCoolDown(a.name().toLowerCase(), p.getItemInHand()));
						}
						Player damaged = (Player) e.getEntity();
						ItemStack[] is = Arrays.copyOfRange(damaged.getInventory().getContents(), 0, 9);
						ItemStack[] ar = shuffleArray(is);
						ItemStack[] contents = damaged.getInventory().getContents();
						for (int i = 0 ; i < ar.length;i++) {
							contents[i] = ar[i];
						}
						damaged.getInventory().setContents(contents);
						damaged.sendMessage(ChatColor.YELLOW+"HotBar Has Been Shuffled!");
					} else if (a != null && a.equals(active.SLAY)) {
						int activetime;
						if ((activetime = checkActives(a.name().toLowerCase(), p.getItemInHand())) != -1) {
						} else {
							int cooldown;
							if ((cooldown = checkCoolDown(a.name().toLowerCase(), p.getItemInHand())) != -1) {
								p.sendMessage(ChatColor.RED + a.name() + " Still in CoolDown, " + cooldown
										+ " seconds left!");
								return;
							}
							if (item.getLevel() < 30) {
								p.setItemInHand(setActive(a.name().toLowerCase(), p.getItemInHand()));
								p.setItemInHand(setCoolDown(a.name().toLowerCase(), p.getItemInHand()));
							} else if (item.getLevel() >= 30) {
								p.setItemInHand(setActive30(a.name().toLowerCase(), p.getItemInHand()));
								p.setItemInHand(setCoolDown30(a.name().toLowerCase(), p.getItemInHand()));
							}
						}
						e.setDamage(e.getDamage() * 3);
					} else if (a != null && a.equals(active.SLAM)) {
						int activetime;
						if ((activetime = checkActives(a.name().toLowerCase(), p.getItemInHand())) != -1) {
						} else {
							int cooldown;
							if ((cooldown = checkCoolDown(a.name().toLowerCase(), p.getItemInHand())) != -1) {
								p.sendMessage(ChatColor.RED + a.name() + " Still in CoolDown, " + cooldown
										+ " seconds left!");
								return;
							}
							if (item.getLevel() < 30) {
								p.setItemInHand(setActive(a.name().toLowerCase(), p.getItemInHand()));
								p.setItemInHand(setCoolDown(a.name().toLowerCase(), p.getItemInHand()));
							} else if (item.getLevel() >= 30) {
								p.setItemInHand(setActive30(a.name().toLowerCase(), p.getItemInHand()));
								p.setItemInHand(setCoolDown30(a.name().toLowerCase(), p.getItemInHand()));
							}
						}
						for (Entity ent : e.getEntity().getNearbyEntities(5d, 5d, 5d)) {
							if (ent instanceof LivingEntity && !ent.equals(e.getDamager())) {
								((LivingEntity) ent).damage(e.getDamage());
								Location loc = ((LivingEntity) ent).getLocation()
										.subtract(e.getDamager().getLocation());
								((LivingEntity) ent).setVelocity(new Vector(loc.getX(), 1, loc.getZ()));
								((LivingEntity) ent)
										.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 1, true));
								((LivingEntity) ent)
										.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 1, true));

							}
						}
						Effect effect = new VortexEffect(ItemPlugin.EM);
						effect.setLocation(p.getLocation().add(0, 3, 0));
						effect.getLocation().setPitch(-90);
						effect.getLocation().setYaw(0);
						effect.speed = 10;
						effect.iterations = 1 * 20;
						effect.start();
					} else if (a != null && a.equals(active.RAGE)) {
						int activetime;
						if ((activetime = checkActives(a.name().toLowerCase(), p.getItemInHand())) != -1) {
							p.sendMessage(ChatColor.DARK_PURPLE + a.name() + " Already Active, " + activetime
									+ " seconds left!");
						} else {
							int cooldown;
							if ((cooldown = checkCoolDown(a.name().toLowerCase(), p.getItemInHand())) != -1) {
								p.sendMessage(ChatColor.RED + a.name() + " Still in CoolDown, " + cooldown
										+ " seconds left!");
								return;
							}
							if (item.getLevel() < 30) {
								p.setItemInHand(setActive(a.name().toLowerCase(), p.getItemInHand()));
								p.setItemInHand(setCoolDown(a.name().toLowerCase(), p.getItemInHand()));
								p.addPotionEffect(
										new PotionEffect(PotionEffectType.INCREASE_DAMAGE,
												20 * ItemPlugin.fileconfig
														.getInt("actives." + a.name().toLowerCase() + ".activetime"),
												2, true));
								p.addPotionEffect(
										new PotionEffect(PotionEffectType.SPEED,
												20 * ItemPlugin.fileconfig
														.getInt("actives." + a.name().toLowerCase() + ".activetime"),
												3, true));
							} else if (item.getLevel() >= 30) {
								p.setItemInHand(setActive30(a.name().toLowerCase(), p.getItemInHand()));
								p.setItemInHand(setCoolDown30(a.name().toLowerCase(), p.getItemInHand()));
								p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,
										20 * ItemPlugin.fileconfig
												.getInt("actives." + a.name().toLowerCase() + ".activetime30"),
										2, true));
								p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,
										20 * ItemPlugin.fileconfig
												.getInt("actives." + a.name().toLowerCase() + ".activetime30"),
										3, true));
							}
						}

					}
				}

			} else if (e.getDamager() instanceof Arrow) {
				String s = getArrowAbility(((Arrow) e.getDamager()).getUniqueId());
				if (s != null && s.equals("poison")) {
					if (((LivingEntity) e.getEntity()) instanceof Zombie
							|| ((LivingEntity) e.getEntity()) instanceof Skeleton
							|| ((LivingEntity) e.getEntity()) instanceof PigZombie) {
						((LivingEntity) e.getEntity())
								.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 2, true));
					} else {
						((LivingEntity) e.getEntity())
								.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1, true));
					}
				} else if (s != null && s.equals("explosive")) {
					e.getEntity().getLocation().getWorld().createExplosion(e.getEntity().getLocation(), 5f, true);
					for (Entity ent : e.getEntity().getNearbyEntities(5d, 5d, 5d)) {
						if (ent instanceof LivingEntity) {
							((LivingEntity) ent).damage(6d);
						}
					}
				} else if (s != null && s.equals("firework")) {
					PoisonExplosiveIndex.remove(e.getEntity().getUniqueId());
					Arrow arrow = (Arrow) e.getEntity();
					Firework fw = (Firework) arrow.getWorld().spawnEntity(arrow.getLocation(), EntityType.FIREWORK);
					FireworkMeta fwm = fw.getFireworkMeta();
					Random r = new Random();

					// Get the type
					int rt = r.nextInt(5) + 1;
					Type type = Type.BALL;
					if (rt == 1)
						type = Type.BALL;
					if (rt == 2)
						type = Type.BALL_LARGE;
					if (rt == 3)
						type = Type.BURST;
					if (rt == 4)
						type = Type.CREEPER;
					if (rt == 5)
						type = Type.STAR;
					Color c1 = getColor(r.nextInt(17) + 1);
					Color c2 = getColor(r.nextInt(17) + 1);

					// Create our effect with this
					FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2)
							.with(type).trail(r.nextBoolean()).build();

					// Then apply the effect to the meta
					fwm.addEffect(effect);

					// Generate some random power and set it
					int rp = r.nextInt(2) + 1;
					fwm.setPower(rp);

					// Then apply this to our rocket

					fw.setFireworkMeta(fwm);
					new BukkitRunnable() {
						@Override
						public void run() {
							fw.detonate();
						}
					}.runTaskLater(ItemPlugin.getInstance(), 1L);
					for (Entity ent : arrow.getNearbyEntities(5d, 5d, 5d)) {
						if (ent instanceof LivingEntity && arrow.getShooter().equals((ProjectileSource) ent)) {
							((LivingEntity) ent).setHealth(((LivingEntity) ent).getHealth() - 1d);
							((LivingEntity) e).damage(0.01d);
						}
					}
					FireworkIndex.remove((Arrow) e.getEntity());
				}
			} else if (e.getDamager() instanceof Firework) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void on(PlayerInteractEvent e) {

		if (e.getPlayer().isSneaking()
				&& (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {

			if (ItemType.fromItem(e.getPlayer().getItemInHand()) != null) {
				e.setCancelled(true);

				Optional<CustomItem> opitem = ItemUtil.getItem(e.getPlayer().getItemInHand());
				if (opitem.isPresent()) {
					CustomItem item = opitem.get();
					active next = active.getNext(item);
					item.setActiveAbility(next);
					e.getPlayer().sendMessage(ChatColor.GREEN+"You Have Switched the ability of this item to "+ next.toString());
					e.getPlayer().setItemInHand(item.getItem());
				} 
				return;

			}

		} else if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

			if (ItemType.fromItem(e.getPlayer().getItemInHand()) != null) {
				CustomItem item = ItemUtil.getItem(e.getPlayer().getItemInHand()).get();
				active a = item.getActiveAbility();
				if (a != null && a.equals(active.TOSS)) {
					Player p = e.getPlayer();
					int activetime;
					if ((activetime = checkActives(a.name().toLowerCase(), p.getItemInHand())) != -1) {

					} else {
						int cooldown;
						if ((cooldown = checkCoolDown(a.name().toLowerCase(), p.getItemInHand())) != -1) {
							p.sendMessage(
									ChatColor.RED + a.name() + " Still in CoolDown, " + cooldown + " seconds left!");
							return;
						}
						if (item.getLevel() < 30) {
							p.setItemInHand(setActive(a.name().toLowerCase(),
									setCoolDown(a.name().toLowerCase(), p.getItemInHand())));
						} else if (item.getLevel() >= 30) {
							p.setItemInHand(setActive30(a.name().toLowerCase(),
									setCoolDown30(a.name().toLowerCase(), p.getItemInHand())));
						}

					}
					spawnThor(e.getPlayer(), e.getPlayer().getEyeLocation());
					ItemStack is = p.getItemInHand();
					e.getPlayer().getInventory().remove(e.getPlayer().getItemInHand());
					new BukkitRunnable() {
						int itemslot = p.getInventory().getHeldItemSlot();
						ItemStack itemstack = is;

						@Override
						public void run() {
							if (p.getInventory().getItem(itemslot) != null) {
								p.getLocation().getWorld().dropItem(p.getEyeLocation(),
										p.getInventory().getItem(itemslot));
							}
							e.getPlayer().getInventory().setItem(itemslot, itemstack);
						}
					}.runTaskLater(ItemPlugin.getInstance(), 50L);
					e.getPlayer().getInventory().remove(e.getPlayer().getItemInHand());
				} else if (a != null && a.equals(active.BOLT)) {
					Player p = e.getPlayer();
					int cooldown;
					if ((cooldown = checkCoolDown(a.name().toLowerCase(), p.getItemInHand())) != -1) {
						p.sendMessage(ChatColor.RED + a.name() + " Still in CoolDown, " + cooldown + " seconds left!");
						return;
					}
					Short s = BoltCount.putIfAbsent(p.getUniqueId(), (short) 1);
					if (s != null) {
						if (s.shortValue() >= ItemPlugin.fileconfig
								.getInt("actives." + a.name().toLowerCase() + ".activetime") - 1) {
							if (item.getLevel() < 30) {
								p.setItemInHand(setCoolDown(a.name().toLowerCase(), p.getItemInHand()));
							} else if (item.getLevel() >= 30) {
								p.setItemInHand(setCoolDown30(a.name().toLowerCase(), p.getItemInHand()));
							}
							p.sendMessage(ChatColor.DARK_PURPLE + "Last Smite");
							BoltCount.put(p.getUniqueId(), null);
						} else {
							BoltCount.put(p.getUniqueId(), (short) (s + 1));
							p.sendMessage(ChatColor.DARK_PURPLE + "Thor Smite " + (s + 1) + "");
						}

					} else {
						p.sendMessage(ChatColor.DARK_PURPLE + "First Smite");
					}
					for (Entity ent : e.getPlayer().getLocation().getWorld()
							.getNearbyEntities(e.getPlayer().getEyeLocation(), 10d, 10d, 10d)) {
						if (ent instanceof LivingEntity && !ent.getUniqueId().equals(e.getPlayer().getUniqueId())) {
							ent.getLocation().getWorld().strikeLightningEffect(ent.getLocation());
							((LivingEntity) ent).damage(7, e.getPlayer());
						}
					}
				}
			}
		} else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (ItemType.fromItem(e.getPlayer().getItemInHand()) != null) {
				CustomItem item = ItemUtil.getItem(e.getPlayer().getItemInHand()).get();
				active a = item.getActiveAbility();
				if (a != null && a.equals(active.CRUSH)) {
					Player p = e.getPlayer();
					int activetime;
					if ((activetime = checkActives(a.name().toLowerCase(), p.getItemInHand())) != -1) {
					} else {
						int cooldown;
						if ((cooldown = checkCoolDown(a.name().toLowerCase(), p.getItemInHand())) != -1) {
							p.sendMessage(
									ChatColor.RED + a.name() + " Still in CoolDown, " + cooldown + " seconds left!");
							return;
						}
						if (item.getLevel() < 30) {
							p.setItemInHand(setActive(a.name().toLowerCase(), p.getItemInHand()));
							p.setItemInHand(setCoolDown(a.name().toLowerCase(), p.getItemInHand()));
						} else if (item.getLevel() >= 30) {
							p.setItemInHand(setActive30(a.name().toLowerCase(), p.getItemInHand()));
							p.setItemInHand(setCoolDown30(a.name().toLowerCase(), p.getItemInHand()));
						}
					}
					if (ItemUtil.isOre(e.getClickedBlock()) || e.getClickedBlock().getType() == Material.STONE) {
						for (ItemStack is : e.getClickedBlock().getDrops()) {
							e.getClickedBlock().getWorld().dropItemNaturally(e.getClickedBlock().getLocation(), is);
						}
						e.getClickedBlock().breakNaturally();
					}
				}

			}
		}
	}

	public static Map<UUID, Short> PoisonCount = new HashMap<>();
	public static Map<UUID, String> PoisonExplosiveIndex = new HashMap<>();
	public static Set<Arrow> FireworkIndex = new HashSet<>();
	public static Map<UUID, Short> BoltCount = new HashMap<>();

	@EventHandler
	public void onShoot(EntityShootBowEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (ItemType.fromItem(p.getItemInHand()) != null) {
				CustomItem item = ItemUtil.getItem(p.getItemInHand()).get();
				active a = item.getActiveAbility();
				if (a != null && a.equals(active.MULTISHOT) && e.getProjectile().getType() == EntityType.ARROW) {
					int activetime;
					if ((activetime = checkActives(a.name().toLowerCase(), p.getItemInHand())) != -1) {
					} else {
						int cooldown;
						if ((cooldown = checkCoolDown(a.name().toLowerCase(), p.getItemInHand())) != -1) {
							p.sendMessage(
									ChatColor.RED + a.name() + " Still in CoolDown, " + cooldown + " seconds left!");
							return;
						}
						if (item.getLevel() < 30) {
							p.setItemInHand(setActive(a.name().toLowerCase(), p.getItemInHand()));
							p.setItemInHand(setCoolDown(a.name().toLowerCase(), p.getItemInHand()));
						} else if (item.getLevel() >= 30) {
							p.setItemInHand(setActive30(a.name().toLowerCase(), p.getItemInHand()));
							p.setItemInHand(setCoolDown30(a.name().toLowerCase(), p.getItemInHand()));
						}
					}
					Vector v = e.getProjectile().getVelocity();
					Location loc = p.getEyeLocation().add(p.getEyeLocation().getDirection().normalize().multiply(1));
					double speed = Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY() + v.getZ() * v.getZ()) * 0.95;
					new BukkitRunnable() {
						int count = (int) (Math.random() * 3 + 3);

						@Override
						public void run() {
							count--;
							Arrow arrow = e.getEntity().getLocation().getWorld().spawnArrow(loc, v, (float) speed, 0f);
							arrow.setBounce(false);
							arrow.setKnockbackStrength(2);
							arrow.setShooter(e.getEntity());
							if (count <= 0) {
								this.cancel();
							}
						}
					}.runTaskTimer(ItemPlugin.getInstance(), 0, 2L);
					e.setCancelled(true);
				} else if (a != null && a.equals(active.AUTO_FIRE) && e.getProjectile().getType() == EntityType.ARROW) {
					int activetime;
					if ((activetime = checkActives(a.name().toLowerCase(), p.getItemInHand())) != -1) {
						p.sendMessage(ChatColor.DARK_PURPLE + a.name() + "Already Active, " + activetime + "s left!");
					} else {
						int cooldown;
						if ((cooldown = checkCoolDown(a.name().toLowerCase(), p.getItemInHand())) != -1) {
							p.sendMessage(
									ChatColor.RED + a.name() + " Still in CoolDown, " + cooldown + " seconds left!");
							return;
						}
						if (item.getLevel() < 30) {
							p.setItemInHand(setActive(a.name().toLowerCase(), p.getItemInHand()));
							p.setItemInHand(setCoolDown(a.name().toLowerCase(), p.getItemInHand()));
						} else if (item.getLevel() >= 30) {
							p.setItemInHand(setActive30(a.name().toLowerCase(), p.getItemInHand()));
							p.setItemInHand(setCoolDown30(a.name().toLowerCase(), p.getItemInHand()));
						}
					}
					Vector v = e.getProjectile().getVelocity();
					double speed = Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY() + v.getZ() * v.getZ()) * 0.92;
					BukkitTask bt = new BukkitRunnable() {
						@Override
						public void run() {
							Arrow arrow = e.getEntity().getLocation().getWorld().spawnArrow(
									p.getEyeLocation().add(p.getEyeLocation().getDirection().normalize().multiply(1)),
									p.getEyeLocation().getDirection(), (float) speed, 0f);
							arrow.setShooter(e.getEntity());
						}
					}.runTaskTimer(ItemPlugin.getInstance(), 0, 5L);
					new BukkitRunnable() {
						@Override
						public void run() {
							bt.cancel();
						}
					}.runTaskLater(ItemPlugin.getInstance(),
							20 * ItemPlugin.fileconfig.getInt("actives.auto_fire.activetime"));
					e.setCancelled(true);
				} else if (a != null && a.equals(active.POISON_ARROW)
						&& e.getProjectile().getType() == EntityType.ARROW) {
					int cooldown;
					if ((cooldown = checkCoolDown(a.name().toLowerCase(), p.getItemInHand())) != -1) {
						p.sendMessage(ChatColor.RED + a.name() + " Still in CoolDown, " + cooldown + " seconds left!");
						return;
					}
					Short s = PoisonCount.putIfAbsent(p.getUniqueId(), (short) 1);
					if (s != null) {
						if (s.shortValue() >= ItemPlugin.fileconfig
								.getInt("actives." + a.name().toLowerCase() + ".activetime") - 1) {
							if (item.getLevel() < 30) {
								p.setItemInHand(setCoolDown(a.name().toLowerCase(), p.getItemInHand()));
							} else if (item.getLevel() >= 30) {
								p.setItemInHand(setCoolDown30(a.name().toLowerCase(), p.getItemInHand()));
							}
							p.sendMessage(ChatColor.DARK_PURPLE + "Last Arrow");
							PoisonCount.put(p.getUniqueId(), null);
						} else {
							PoisonCount.put(p.getUniqueId(), (short) (s + 1));
							p.sendMessage(ChatColor.DARK_PURPLE + "Arrow Numba " + (s + 1) + "");
						}

					} else {
						p.sendMessage(ChatColor.DARK_PURPLE + "First Arrow");
					}

					PoisonExplosiveIndex.put(e.getProjectile().getUniqueId(), "poison");
				} else if (a != null && a.equals(active.EXPLOSIVE) && e.getProjectile().getType() == EntityType.ARROW) {
					int activetime;
					if ((activetime = checkActives(a.name().toLowerCase(), p.getItemInHand())) != -1) {
					} else {
						int cooldown;
						if ((cooldown = checkCoolDown(a.name().toLowerCase(), p.getItemInHand())) != -1) {
							p.sendMessage(
									ChatColor.RED + a.name() + " Still in CoolDown, " + cooldown + " seconds left!");
							return;
						}
						if (item.getLevel() < 30) {
							p.setItemInHand(setActive(a.name().toLowerCase(), p.getItemInHand()));
							p.setItemInHand(setCoolDown(a.name().toLowerCase(), p.getItemInHand()));
						} else if (item.getLevel() >= 30) {
							p.setItemInHand(setActive30(a.name().toLowerCase(), p.getItemInHand()));
							p.setItemInHand(setCoolDown30(a.name().toLowerCase(), p.getItemInHand()));
						}
					}
					PoisonExplosiveIndex.put(e.getProjectile().getUniqueId(), "explosive");
				} else if (a != null && a.equals(active.FIREWORK)) {
					int activetime;
					if ((activetime = checkActives(a.name().toLowerCase(), p.getItemInHand())) != -1) {
					} else {
						int cooldown;
						if ((cooldown = checkCoolDown(a.name().toLowerCase(), p.getItemInHand())) != -1) {
							p.sendMessage(
									ChatColor.RED + a.name() + " Still in CoolDown, " + cooldown + " seconds left!");
							return;
						}
						if (item.getLevel() < 30) {
							p.setItemInHand(setActive(a.name().toLowerCase(), p.getItemInHand()));
							p.setItemInHand(setCoolDown(a.name().toLowerCase(), p.getItemInHand()));
						} else if (item.getLevel() >= 30) {
							p.setItemInHand(setActive30(a.name().toLowerCase(), p.getItemInHand()));
							p.setItemInHand(setCoolDown30(a.name().toLowerCase(), p.getItemInHand()));
						}
					}
					FireworkIndex.add((Arrow) e.getProjectile());
					PoisonExplosiveIndex.put(e.getProjectile().getUniqueId(), "firework");
				}
			}
		}
	}

	public static String getArrowAbility(UUID id) {
		return PoisonExplosiveIndex.remove(id);
	}

	private Color getColor(int i) {
		Color c = null;
		if (i == 1) {
			c = Color.AQUA;
		}
		if (i == 2) {
			c = Color.BLACK;
		}
		if (i == 3) {
			c = Color.BLUE;
		}
		if (i == 4) {
			c = Color.FUCHSIA;
		}
		if (i == 5) {
			c = Color.GRAY;
		}
		if (i == 6) {
			c = Color.GREEN;
		}
		if (i == 7) {
			c = Color.LIME;
		}
		if (i == 8) {
			c = Color.MAROON;
		}
		if (i == 9) {
			c = Color.NAVY;
		}
		if (i == 10) {
			c = Color.OLIVE;
		}
		if (i == 11) {
			c = Color.ORANGE;
		}
		if (i == 12) {
			c = Color.PURPLE;
		}
		if (i == 13) {
			c = Color.RED;
		}
		if (i == 14) {
			c = Color.SILVER;
		}
		if (i == 15) {
			c = Color.TEAL;
		}
		if (i == 16) {
			c = Color.WHITE;
		}
		if (i == 17) {
			c = Color.YELLOW;
		}

		return c;
	}

	@EventHandler
	public void on(BlockBreakEvent e) {
		if (ItemType.fromItem(e.getPlayer().getItemInHand()) != null) {
			CustomItem item = ItemUtil.getItem(e.getPlayer().getItemInHand()).get();
			// check cooldown
			active a = item.getActiveAbility();
			if (a != null && a.equals(active.TIMBER)) {
				Player p = e.getPlayer();
				int activetime;
				if ((activetime = checkActives(a.name().toLowerCase(), p.getItemInHand())) != -1) {
				} else {
					int cooldown;
					if ((cooldown = checkCoolDown(a.name().toLowerCase(), p.getItemInHand())) != -1) {
						p.sendMessage(ChatColor.RED + a.name() + " Still in CoolDown, " + cooldown + " seconds left!");
						return;
					}
					if (item.getLevel() < 30) {
						p.setItemInHand(setActive(a.name().toLowerCase(), p.getItemInHand()));
						p.setItemInHand(setCoolDown(a.name().toLowerCase(), p.getItemInHand()));
					} else if (item.getLevel() >= 30) {
						p.setItemInHand(setActive30(a.name().toLowerCase(), p.getItemInHand()));
						p.setItemInHand(setCoolDown30(a.name().toLowerCase(), p.getItemInHand()));
					}
				}
				if (e.getBlock().getType().equals(Material.LOG) || e.getBlock().getType().equals(Material.LOG_2)) {
					new TreeCutter(e.getPlayer(), e.getBlock(), e.getPlayer().getItemInHand())
							.runTaskTimer(ItemPlugin.getInstance(), 0, 1L);

				}
			} else if (a != null && a.equals(active.TREASURE)
					&& (e.getBlock().getType() == Material.DIRT || e.getBlock().getType() == Material.SAND)) {
				Player p = e.getPlayer();
				int activetime;
				if ((activetime = checkActives(a.name().toLowerCase(), p.getItemInHand())) != -1) {
				} else {
					int cooldown;
					if ((cooldown = checkCoolDown(a.name().toLowerCase(), p.getItemInHand())) != -1) {
						p.sendMessage(ChatColor.RED + a.name() + " Still in CoolDown, " + cooldown + " seconds left!");
						return;
					}
					if (item.getLevel() < 30) {
						p.setItemInHand(setActive(a.name().toLowerCase(), p.getItemInHand()));
						p.setItemInHand(setCoolDown(a.name().toLowerCase(), p.getItemInHand()));
					} else if (item.getLevel() >= 30) {
						p.setItemInHand(setActive30(a.name().toLowerCase(), p.getItemInHand()));
						p.setItemInHand(setCoolDown30(a.name().toLowerCase(), p.getItemInHand()));
					}
				}
				Random rd = new Random();
				if (rd.nextInt(99) < 15) {
					String s = ItemPlugin.fileconfig.getString("actives.treasure.command");
					int i = s.indexOf("%player%");
					s.replaceAll("%player%", e.getPlayer().getName());
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
							s);
				}
			} else if (a != null && a.equals(active.VEINMINE)) {
				if (ItemUtil.isOre(e.getBlock())) {
					Player p = e.getPlayer();
					int cooldown;
					if ((cooldown = checkCoolDown(a.name().toLowerCase(), p.getItemInHand())) != -1) {
						p.sendMessage(ChatColor.RED + a.name() + " Still in CoolDown, " + cooldown + " seconds left!");
						return;
					}
					if (item.getLevel() < 30) {
						p.setItemInHand(setCoolDown(a.name().toLowerCase(), p.getItemInHand()));
					} else if (item.getLevel() >= 30) {
						p.setItemInHand(setCoolDown30(a.name().toLowerCase(), p.getItemInHand()));
					}
					new VeinBreakBlock(e.getBlock(), e.getPlayer().getItemInHand())
							.runTaskTimer(ItemPlugin.getInstance(), 0, 1L);
				}
			} else if (a != null && a.equals(active.RESERVE)) {
				if (ItemUtil.isOre(e.getBlock())) {
					Player p = e.getPlayer();
					int activetime;
					if ((activetime = checkActives(a.name().toLowerCase(), p.getItemInHand())) != -1) {
					} else {
						int cooldown;
						if ((cooldown = checkCoolDown(a.name().toLowerCase(), p.getItemInHand())) != -1) {
							p.sendMessage(
									ChatColor.RED + a.name() + " Still in CoolDown, " + cooldown + " seconds left!");
							return;
						}
						if (item.getLevel() < 30) {
							p.setItemInHand(setActive(a.name().toLowerCase(), p.getItemInHand()));
							p.setItemInHand(setCoolDown(a.name().toLowerCase(), p.getItemInHand()));
						} else if (item.getLevel() >= 30) {
							p.setItemInHand(setActive30(a.name().toLowerCase(), p.getItemInHand()));
							p.setItemInHand(setCoolDown30(a.name().toLowerCase(), p.getItemInHand()));
						}
					}
					Material m = e.getBlock().getType();
					Bukkit.getServer().getScheduler().runTaskLater(ItemPlugin.getInstance(), new Runnable() {
						@Override
						public void run() {
							e.getBlock().setType(m);
						}
					}, 3L);

				}
			} else if (a != null && a.equals(active.EXCAVATION)) {
				Player p = e.getPlayer();
				int activetime;
				if ((activetime = checkActives(a.name().toLowerCase(), p.getItemInHand())) != -1) {
					p.sendMessage(
							ChatColor.DARK_PURPLE + a.name() + " Already Active, " + activetime + " seconds left!");
					return;
				} else {
					int cooldown;
					if ((cooldown = checkCoolDown(a.name().toLowerCase(), p.getItemInHand())) != -1) {
						p.sendMessage(ChatColor.RED + a.name() + " Still in CoolDown, " + cooldown + " seconds left!");
						return;
					}
					if (item.getLevel() < 30) {
						p.setItemInHand(setActive(a.name().toLowerCase(), p.getItemInHand()));
						p.setItemInHand(setCoolDown(a.name().toLowerCase(), p.getItemInHand()));
						p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,
								ItemPlugin.fileconfig.getInt("actives.excavation.activetime") * 20, 3));
					} else if (item.getLevel() >= 30) {
						p.setItemInHand(setActive30(a.name().toLowerCase(), p.getItemInHand()));
						p.setItemInHand(setCoolDown30(a.name().toLowerCase(), p.getItemInHand()));
						p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,
								ItemPlugin.fileconfig.getInt("actives.excavation.activetime30") * 20, 3));
					}
				}

			}
		}
	}
}
