package me.crz.minecraft.itemsystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import de.slikey.effectlib.EffectManager;
import me.crz.minecraft.itemsystem.utils.Config;
import me.crz.minecraft.itemsystem.utils.ConfigData;

public class ItemPlugin extends JavaPlugin {

	private static ItemPlugin instance;
	public Config config;
	public static EffectManager EM;
	public static FileConfiguration fileconfig = new YamlConfiguration();
	public Set<String> worldname = new HashSet<>();

	@Override
	public void onEnable() {
		instance = this;
		EM = new EffectManager(instance);
		loadFolder();
		loadConfig();

		ConfigData.loadConfigData(fileconfig);
		getServer().getPluginManager().registerEvents(new XPListener(), this);
		getServer().getPluginManager().registerEvents(new PassiveSkillListener(), this);
		getServer().getPluginManager().registerEvents(new ActiveItem(), this);
		Bukkit.getPluginCommand("itemlevel").setExecutor(new me.crz.minecraft.itemsystem.utils.Commands());
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!ActiveItem.FireworkIndex.isEmpty()) {
					for (Arrow arrow : ActiveItem.FireworkIndex) {
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
						FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1)
								.withFade(c2).with(type).trail(r.nextBoolean()).build();

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
						for (Entity e : arrow.getNearbyEntities(5d, 5d, 5d)) {
							if (e instanceof LivingEntity && arrow.getShooter().equals((ProjectileSource) e)) {
								((LivingEntity) e).setHealth(((LivingEntity) e).getHealth() - 1d);
								((LivingEntity) e).damage(0.01d);
							}
						}
						/*
						 * Type type = Type.BURST; Color c1 = Color.WHITE; Color c2 = Color.AQUA; Random
						 * r = new Random(); //Create our effect with this FireworkEffect effect =
						 * FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).
						 * with(type).trail(r.nextBoolean()).build();
						 * 
						 * //Then apply the effect to the meta fwm.addEffect(effect);
						 * 
						 * //Generate some random power and set it int rp = r.nextInt(2) + 1;
						 * fwm.setPower(rp);
						 * 
						 * //Then apply this to our rocket fw.setFireworkMeta(fwm);
						 */
					}
				}
			}
		}.runTaskTimer(this, 10L, 60L);
	}

	public static boolean between(double i, double value1, double value2) {
		if (i >= value1 && i <= value2)
			return true;
		else
			return false;
	}

	public void onDisable() {
		saveFolder();
		EM.dispose();
	}

	public void loadConfig() {
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		File f = new File(this.getDataFolder(), "config.yml");

		try {
			fileconfig.load(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void loadFolder() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
			File file = new File(getDataFolder(), "storage.db");
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			File file = new File(getDataFolder(), "storage.db");
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					ObjectInputStream oos = new ObjectInputStream(new FileInputStream(file));
					this.worldname = (Set<String>) oos.readObject();
					oos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	public void saveFolder() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
			File file = new File(getDataFolder(), "storage.db");
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			File file = new File(getDataFolder(), "storage.db");
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			ObjectOutputStream ois = new ObjectOutputStream(
					new FileOutputStream(new File(getDataFolder(), "storage.db")));
			ois.writeObject(this.worldname);
			ois.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// private void createCustomConfig() {
	// customConfigFile = new File(getDataFolder(), "custom.yml");
	// if (!customConfigFile.exists()) {
	// customConfigFile.getParentFile().mkdirs();
	// saveResource("custom.yml", false);
	// }
	//
	// customConfig= new YamlConfiguration();
	// try {
	// customConfig.load(customConfigFile);
	// } catch (IOException | InvalidConfigurationException e) {
	// Bukkit.getPluginManager().disablePlugin(this);
	// System.err.println("User has killed program please fix your config boi");
	// e.printStackTrace();
	// }
	// }

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

	public static ItemPlugin getInstance() {
		return instance;
	}

}
