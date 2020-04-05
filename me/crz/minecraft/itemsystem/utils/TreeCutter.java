package me.crz.minecraft.itemsystem.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class TreeCutter extends BukkitRunnable {

	@SuppressWarnings("unused")
	private Player player;
	@SuppressWarnings("unused")
	private Block startBlock;
	private List<Block> bList = new ArrayList<>();
	private short indexed = 0;
	private ItemStack is;

	public TreeCutter(Player cutter, Block startBlock, ItemStack is) {
		this.player = cutter;
		this.startBlock = startBlock;
		bList.add(startBlock);
		this.is = is;

	}

	@Override
	public void run() {
		if (!bList.isEmpty()) {
			for (int i = 0; i < bList.size(); i++) {
				Block block = bList.get(i);
				if (block.getType() == Material.LOG) {
					for (ItemStack item : block.getDrops()) {
						block.getWorld().dropItemNaturally(block.getLocation(), item);
					}
					indexed++;
					block.setType(Material.AIR);
				}
				for (BlockFace face : BlockFace.values()) {
					if (block.getRelative(face).getType() == Material.LOG) {
						bList.add(block.getRelative(face));
					}
				}
				bList.remove(block);

			}
		} else {
			is.setDurability((short) (is.getDurability() + indexed));
			this.cancel();
		}
	}

	public void breakTree(Block tree) {
		if (tree.getType() != Material.LOG && tree.getType() != Material.LEAVES)
			return;
		tree.breakNaturally();
		for (BlockFace face : BlockFace.values())
			breakTree(tree.getRelative(face));

	}

	// for (int x = -2; x <= 2; x++) {
	// for (int y = -2; y <= 2; y++) {
	// for (int z = -2; z <= 1; z++) {
	// if (x == 0 && y == 0 && z == 0)
	// continue;
	// Block b2 = b1.getRelative(x, y, z);
	// String s = b2.getX() + ":" + b2.getY() + ":" + b2.getZ();
	//
	// if ((b2.getType() == Material.LEAVES || b2.getType() == Material.LEAVES_2) &&
	// !comparisonBlockArrayLeaves.contains(s))
	// comparisonBlockArrayLeaves.add(s);
	// if (b2.getType() != Material.LOG && b2.getType() != Material.LOG_2)
	// continue;
	// int search square
	// int searchSquareSize = 25;
	// if (b2.getX() > x1 + searchSquareSize || b2.getX() < x1 - searchSquareSize ||
	// b2.getZ() > z1 + searchSquareSize || b2.getZ() < z1 - searchSquareSize)
	// break;
	// if (!comparisonBlockArray.contains(s)) {
	// comparisonBlockArray.add(s);
	// blocks.add(b2);
	// this.runLoop(b2, x1, z1);
	// }
	// }
	// }
	// }

//	private boolean isTree() {
//		return (comparisonBlockArrayLeaves.size() * 1D) / (blocks.size() * 1D) > 0.3;
//	}
//
//	public List<Block> getBlocks() {
//		return blocks;
//	}
//
//	public void cutDownTree() {
//		if ((player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR)
//				&& !updateItemInHand()) {
//			return;
//		}
//
//		blocks = blocks.stream().sorted((b, b2) -> b2.getY() - b.getY()).collect(Collectors.toList());
//		long speed = 4;
//
//		new BukkitRunnable() {
//			int blocksCut = 0;
//
//			@Override
//			public void run() {
//
//				// In case player disconnect
//				if (!player.isOnline()) {
//					this.cancel();
//					return;
//				}
//
//				ItemStack item = player.getItemInHand();
//
//				if (blocks.size() < indexed - 2) {
//					this.cancel();
//					return;
//				}
//
//				Block block = blocks.get(indexed++);
//
//				// Fire events
//				Event animationEvent = new PlayerAnimationEvent(player);
//				Bukkit.getPluginManager().callEvent(animationEvent);
//				BlockBreakEvent event = new BlockBreakEvent(block, player);
//				Bukkit.getPluginManager().callEvent(event);
//
//				if (!event.isCancelled()) {
//					Location center = block.getLocation().add(0.5, 0.5, 0.5);
//					startBlock.getWorld().playEffect(center, Effect.STEP_SOUND, block.getType());
//
//					for (ItemStack drop : block.getDrops()) {
//						startBlock.getWorld().dropItem(center, drop);
//					}
//
//					item.setDurability((short) (item.getDurability() + 1));
//					if (item.getType().getMaxDurability() == item.getDurability())
//						player.setItemInHand(null);
//					BaseBlock baseBlock = new BaseBlock(0, 0);
//					blocksCut++;
//					if (block.getX() == startBlock.getX() && block.getZ() == startBlock.getZ()
//							&& block.getY() <= startBlock.getY()) {
//						Block b = block.getRelative(BlockFace.DOWN);
//						if ((b.getType() == Material.DIRT || b.getType() == Material.GRASS) && blocks.size() > 5) {
//							block.setType(Material.SAPLING);
//							block.setData((byte) getSaplingType(block));
//						} else
//							block.setTypeIdAndData(baseBlock.getType(), (byte) baseBlock.getData(), true);
//					} else
//						block.setTypeIdAndData(baseBlock.getType(), (byte) baseBlock.getData(), true);
//
//				}
//
//				if (blocks.size() <= indexed || blocksCut >= 50)
//					this.cancel();
//			}
//
//			@Override
//			public void cancel() {
//				super.cancel();
//			}
//
//		}.runTaskTimer(ItemPlugin.getInstance(), 0L, speed);
//	}
//
//	private int getSaplingType(Block block) {
//		if (block.getType() == Material.LOG)
//			return block.getData() % 4;
//		return 4 + block.getData() % 2;
//	}
//
//	private boolean updateItemInHand() {
//		ItemStack item = player.getItemInHand();
//		if (item != null && item.getType() != Material.AIR)
//			return false;
//
//		for (int index = 0; index < 36; index++) {
//			ItemStack stack = player.getInventory().getItem(index);
//			if (stack != null && ItemType.fromItem(player.getItemInHand()).equals(ItemType.AXE)) {
//				player.setItemInHand(stack);
//				player.getInventory().setItem(index, null);
//				return true;
//			}
//		}
//		return false;
//	}
}