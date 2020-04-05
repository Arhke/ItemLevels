package me.crz.minecraft.itemsystem.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import me.crz.minecraft.itemsystem.ItemPlugin;

public class Base implements Listener {

	public ItemPlugin getPlugin() {
		return ItemPlugin.getInstance();
	}

	public void registerListener(Listener listener) {
		getPluginManager().registerEvents(listener, getPlugin());
	}

	public PluginManager getPluginManager() {
		return getPlugin().getServer().getPluginManager();
	}

	public Optional<Player> getPlayer(String s) {
		return getOnlinePlayers().stream().filter(p -> p.getName().equalsIgnoreCase(s)).findFirst();
	}

	public List<Player> getOnlinePlayers() {
		return new ArrayList<>(Bukkit.getOnlinePlayers());
	}

	public String colour(String in) {
		return ChatColor.translateAlternateColorCodes('&', in);
	}

	public static List<Location> getSphere(Location center, int radius, boolean hollow, Material mat) {
		List<Location> blocks = new ArrayList<>();
		int bX = center.getBlockX();
		int bY = center.getBlockY();
		int bZ = center.getBlockZ();

		for (int x = bX - radius; x <= bX + radius; x++) {
			for (int y = bY - radius; y <= bY + radius; y++) {
				for (int z = bZ - radius; z <= bZ + radius; z++) {
					double distance = ((bX - x) * (bX - x) + ((bZ - z) * (bZ - z)) + ((bY - y) * (bY - y)));

					if ((distance < (radius * radius)) && !(hollow && distance < ((radius - 1) * (radius - 1)))) {
						Location loc = new Location(center.getWorld(), x, y, z);
						if (loc.getBlock().getType().equals(mat))
							blocks.add(loc);
					}
				}
			}

		}
		return blocks;
	}

	public String replaceAndStrip(String in) {
		return ChatColor.stripColor(colour(in));
	}

	public List<String> replaceInList(List<String> list, String what, String with) {
		List<String> temp = new ArrayList<>();
		list.forEach((s) -> temp.add(s.replace(what, with)));
		return temp;
	}
	Random rand = new Random();
	public <E> List<E> pickNRandomElements(List<E> list, int n) {
		List<E> temp = new ArrayList<>();
		list.forEach((e) -> temp.add(e));
		if (temp.size() < n)
			return temp;
		List<E> r = new ArrayList<>();
		Collections.shuffle(list);

		for (int i = 0; i < n; i++) {
			int randomIndex = rand.nextInt(temp.size());
			r.add(temp.remove(randomIndex));
		}
		return list.subList(list.size() - n, list.size());
	}

	public boolean isSame(Location one, Location two) {
		return (one.getBlockX() == two.getBlockX() && one.getBlockY() == two.getBlockY()
				&& one.getBlockZ() == two.getBlockZ());
	}
	
	public String capitaliseFirstLetter(String s) {
		if(s.isEmpty())
			return s;
		if(s.length() == 1)
			return s.toUpperCase();
		return s.split("")[0].toUpperCase() + s.substring(1);
	}

}
