package me.crz.minecraft.itemsystem.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class VeinBreakBlock extends BukkitRunnable{
	private Block startBlock;
	private ItemStack is;
	private int indexed = 0;
	private List<Block> bList = new ArrayList<Block>();
	public VeinBreakBlock(Block startblock, ItemStack is) {
		this.startBlock = startblock;
		bList.add(startBlock);
		this.is = is;

	}

	@Override
	public void run() {
		if (!bList.isEmpty()) {
			for (int i = 0; i < bList.size(); i++) {
				Block block = bList.get(i);
				if (ItemUtil.isOre(block)) {
					for (ItemStack item : block.getDrops()) {
						block.getWorld().dropItemNaturally(block.getLocation(), item);
					}
					indexed++;
					block.setType(Material.AIR);
				}
				for (BlockFace face : BlockFace.values()) {
					if (ItemUtil.isOre(block.getRelative(face))) {
						bList.add(block.getRelative(face));
					}
				}
				bList.remove(block);
			}
		}	
		else {
			is.setDurability((short)(is.getDurability()+indexed));
			this.cancel();
		}
	}
	

}
