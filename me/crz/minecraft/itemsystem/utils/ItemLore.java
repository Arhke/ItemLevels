package me.crz.minecraft.itemsystem.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.crz.minecraft.itemsystem.CustomItem;
import me.crz.minecraft.itemsystem.ItemPlugin;

public class ItemLore extends Base {

	public static enum ItemType {

		ARMOR {
			@Override
			public String getPassive(int level) {
				if (level < 10)
					return null;
				StringBuilder sb = new StringBuilder("&2&lPassive: &7(Always Active)\n");
				if (level >= 10) {
					sb.append("  &5" + ConfigData.dodgechance[level - 1]
							+ "% &fto &5avoid damage&f from a player attack.");
					sb.append("\n");
				}
				if (level >= 15) {
					sb.append("  &5" + ConfigData.firechance[level - 1] + "% &fto set &cenemy&f on &5fire&f.");
					sb.append("\n");
				}
				if (level >= 30) {
					sb.append("  &5" + ConfigData.blindchance[level - 1] + "% &fto &5blind&f &cenemies&f for &a10s&f.");
					sb.append("\n");
				}
				return sb.toString();
			}

			@Override
			public String getActive(int level) {
				return null;
			}
		},
		PICK {
			@Override
			public String getPassive(int level) {
				StringBuilder sb = new StringBuilder("&2&lPassive: &7(Always Active)\n");
				sb.append("  &fWhen mining chance to recieve at random:\n");
				sb.append("   &7Iron&8: &5" + ConfigData.ironchance[level - 1] + "%\n");
				sb.append("   &6Gold&8: &5" + ConfigData.goldchance[level-1] + "%\n");
				sb.append("   &3Diamond&8: &5" + ConfigData.diamondchance[level - 1] + "%\n");
				sb.append("   &aEmerald&8: &5" + ConfigData.emeraldchance[level - 1] + "%\n");
				sb.append("   &5" + ConfigData.spreadchance[level-1]
						+ "% &ffor mined ores to spread to surrounding stone.");
				sb.append("\n");
				return sb.toString();
			}

			@Override
			public String getActive(int level) {
				if (level < 5)
					return null;
				StringBuilder sb = new StringBuilder("&c&lActive: &7(Shift + Right click to cycle)\n");
				if (level < 30) {
					if (level >= 5) {
						sb.append("  &5Reserve&7:&f For &a" + ItemPlugin.fileconfig.getInt("actives.reserve.activetime")
								+ "s&f all broken ores will respawn.");
						sb.append("\n");
					}

					if (level >= 15) {
						sb.append("  &5Crush&7:&f For &a" + ItemPlugin.fileconfig.getInt("actives.crush.activetime")
								+ "s&f break all ores and stone instantly.");
						sb.append("\n");
					}

				}
				if (level >= 30) {
					sb.append("  &5Reserve&7:&f For &a" + ItemPlugin.fileconfig.getInt("actives.reserve.activetime30")
							+ "s&f all broken ores will respawn.");
					sb.append("\n");
					sb.append("  &5Crush&7:&f For &a" + ItemPlugin.fileconfig.getInt("actives.crush.activetime30")
							+ "s&f break all ores and stone instantly.");
					sb.append("\n");
					sb.append(
							"  &5VeinMiner&7:&f The next ore you break will break all ores of its type connected to it.");
					sb.append("\n");
				}

				return sb.toString();
			}
		},
		AXE {
			@Override
			public String getPassive(int level) {
				if (level < 5)
					return null;
				StringBuilder sb = new StringBuilder("&2&lPassive: &7(Always Active)\n");
				if (level >= 5) {
					sb.append("  &5" + ConfigData.axedoubledrop[level - 1]
							+ "% &fto recieve double drops from broken logs.");
					sb.append("\n");
				}
				if (level >= 15) {
					sb.append("  &5" + ConfigData.goldenapple[level - 1]
							+ "% &fto recieve 1x gold apple from broken logs.");
					sb.append("\n");
				}
				if (level >= 25) {
					sb.append("  &5" + ConfigData.doubledamage[level - 1] + "% &fto Deal double Damage to players.");
					sb.append("\n");
				}
				return sb.toString();
			}

			@Override
			public String getActive(int level) {
				if (level < 10)
					return null;
				StringBuilder sb = new StringBuilder("&c&lActive: &7(Shift + Right click to cycle)\n");
				if (level < 30) {
					if (level >= 10) {
						sb.append("  &5Timber&7:&f Activate the ability to chop down entire trees for &a"
								+ ItemPlugin.fileconfig.getInt("actives.timber.activetime") + "s&f.");
						sb.append("\n");
					}
					if (level >= 15) {
						sb.append(
								"  &5Toss&7:&f Throws your axe damaging any player in your path before returning to you &52.5s&f later.");
						sb.append("\n");
					}
					if (level >= 20) {
						sb.append("  &5 Bolt&7:&f Strikes all &cenemies&f within &510&f blocks with lighting. ");
						sb.append("\n");
					}
				}
				if (level >= 30) {
					sb.append("  &5Timber&7:&f Activate the ability to chop down entire trees for &a"
							+ ItemPlugin.fileconfig.getInt("actives.timber.activetime30") + "s&f.");
					sb.append("\n");
					sb.append(
							"  &5Toss&7:&f Throws your axe damaging any player in your path before returning to you &52.5s&f later.");
					sb.append("\n");
					sb.append("  &5 Bolt&7:&f Strikes all &cenemies&f within &510&f blocks with lighting. ");
					sb.append("\n");
					sb.append("  &5Shuffle&7:&f The next player you strike will have their hotbar scrambled.");
					sb.append("\n");
				}
				return sb.toString();
			}
		},
		SHOVEL {
			@Override
			public String getPassive(int level) {
				if (level < 5)
					return null;
				StringBuilder sb = new StringBuilder("&2&lPassive: &7(Always Active)\n");
				if (level >= 5) {
					sb.append("  &5" + ConfigData.shoveldoubledrop[level - 1]
							+ "% &fto recieve double drops from digging.");
					sb.append("\n");
				}
				if (level >= 20) {
					sb.append(
							"  &5" + ConfigData.expdrop[level - 1] + "% &fto get a small amount of exp from digging.");
					sb.append("\n");
				}
				return sb.toString();
			}

			@Override
			public String getActive(int level) {
				if (level < 15)
					return null;
				StringBuilder sb = new StringBuilder("&c&lActive: &7(Shift + Right click to cycle)\n");
				if (level >= 15 && level < 30) {
					sb.append("  &5Excavation&7:&f Allows a player to dig twice as fast for &a"
							+ ItemPlugin.fileconfig.getInt("actives.excavation.activetime") + "s&f.");
					sb.append("\n");
				}
				if (level >= 30) {
					sb.append("  &5Excavation&7:&f Allows a player to dig twice as fast for &a"
							+ ItemPlugin.fileconfig.getInt("actives.excavation.activetime30") + "s&f.");
					sb.append("\n");
					sb.append("  &5Treasure&7:&f For &5" + ItemPlugin.fileconfig.getInt("actives.treasure.activetime30")
							+ "s&f diggable drops have a chance to drop treasure.");
					sb.append("\n");
				}
				return sb.toString();
			}
		},
		HOE {
			@Override
			public String getPassive(int level) {
				StringBuilder sb = new StringBuilder("&2&lPassive: &7(Always Active)\n");
				sb.append("  &fGet more crops when When breaking crops with this item: \n");
				sb.append("   &a2x&8: &5" + "notHappeninganymore" + "%\n");
				sb.append("   &a3x&8: &5" + "notHappeninganymore" + "%\n");
				sb.append("   &a5x&8: &5" + "notHappeninganymore" + "%\n");
				return sb.toString();
			}

			@Override
			public String getActive(int level) {
				if (level < 5)
					return null;
				StringBuilder sb = new StringBuilder("no");
				return sb.toString();
			}
		},
		SWORD {
			@Override
			public String getPassive(int level) {
				if (level < 5)
					return null;
				StringBuilder sb = new StringBuilder("&2&lPassive: &7(Always Active)\n");
				if (level >= 5) {
					sb.append("  &5" + ConfigData.playerbleed[level - 1] + "% &fto inflict players with &5bleed&f.");
					sb.append("\n");
				}
				if (level >= 15) {
					sb.append("  &5" + ConfigData.mobbleed[level - 1] + "% &fto inflict mobs with &5bleed&f.");
					sb.append("\n");
				}
				return sb.toString();
			}

			@Override
			public String getActive(int level) {
				if (level < 10)
					return null;
				StringBuilder sb = new StringBuilder("&c&lActive: &7(Shift + Right click to cycle)\n");
				if (level < 30) {
					if (level >= 10) {
						sb.append("  &5Slay&7:&f &5Triple&f damage to mobs for &a"
								+ ItemPlugin.fileconfig.getInt("actives.slay.activetime") + "s&f.");
						sb.append("\n");
					}
					if (level >= 20) {
						sb.append(
								"  &5Slam&7:&f Damages surrounding &cenemies&f as well as your primary target and inflicts stun for &53s&f.");
						sb.append("\n");
					}
					if (level >= 25) {
						sb.append("  &5Rage&7:&f Grants swiftness &53&f and strength &52&f for &a"
								+ ItemPlugin.fileconfig.getInt("actives.slam.activetime") + "s&f.");
						sb.append("\n");
					}
				} else {
					sb.append("  &5Slay&7:&f &5Triple&f damage to mobs for &a"
							+ ItemPlugin.fileconfig.getInt("actives.slay.activetime30") + "s&f.");
					sb.append("\n");
					sb.append(
							"  &5Slam&7:&f Damages surrounding &cenemies&f as well as your primary target and inflicts stun for &53s&f.");
					sb.append("\n");
					sb.append("  &5Rage&7:&f Grants swiftness &53&f and strength &52&f for &a"
							+ ItemPlugin.fileconfig.getInt("actives.slam.activetime30") + "s&f.");
					sb.append("\n");
					sb.append("  &5Insight&7:&f Multiply experience drops from mobs by &55x&f for &a"
							+ ItemPlugin.fileconfig.getInt("actives.insight.activetime30") + "s&f.");
					sb.append("\n");
				}
				return sb.toString();
			}
		},
		BOW {
			@Override
			public String getPassive(int level) {
				if (level < 5)
					return null;
				StringBuilder sb = new StringBuilder("&2&lPassive: &7(Always Active)\n");
				if (level >= 5) {
					sb.append("  &5" + ConfigData.infinitychance[level - 1]
							+ "% &fto shoot an arrow without using any in your inventory.");
					sb.append("\n");
				}
				if (level >= 10) {
					sb.append("  &5" + ConfigData.lightningchance[level - 1]
							+ "% &fto strike your &cenemy&f with &5lightning&f.");
					sb.append("\n");
				}
				if (level >= 15) {
					sb.append("  &5" + ConfigData.slownesschance[level - 1]
							+ "% &fto give your &cenemy&f &5slowness&f. for &a10s&f.");
					sb.append("\n");
				}
				return sb.toString();
			}

			@Override
			public String getActive(int level) {
				if (level < 10)
					return null;
				StringBuilder sb = new StringBuilder("&c&lActive: &7(Shift + Right click to cycle)\n");

				if (level < 30) {
					if (level >= 10) {
						sb.append("  &5Multishot&7:&f Shoot &63&f-&65&f arrows for &a"
								+ ItemPlugin.fileconfig.getInt("actives.multishot.activetime") + "s&f.");
						sb.append("\n");
					}
					if (level >= 15) {
						sb.append("  &5Exsplosive&7:&f For &a"
								+ ItemPlugin.fileconfig.getInt("actives.explosive.activetime")
								+ "s&f When your arrows land they will create an explosion.");
						sb.append("\n");
					}
					if (level >= 20) {
						sb.append(
								"  &5Firework&7:&f For &a" + ItemPlugin.fileconfig.getInt("actives.firework.activetime")
										+ "s&f your arrows will explode in &5fireworks&f mid flight.");
						sb.append("\n");
					}
					if (level >= 25) {
						sb.append("  &5Poison arrow&7:&f Your next &5"
								+ ItemPlugin.fileconfig.getInt("actives.poison_arrow.activetime")
								+ "&f shots will have arrows that will give your &cenemy&f &5poison&f for &55s&f.");
						sb.append("\n");
					}
				} else {
					sb.append("  &5Multishot&7:&f Shoot &63&f-&65&f arrows for &a"
							+ ItemPlugin.fileconfig.getInt("actives.multishot.activetime30") + "s&f.");
					sb.append("\n");
					sb.append("  &5Exsplosive&7:&f For &a"
							+ ItemPlugin.fileconfig.getInt("actives.explosive.activetime30")
							+ "s&f When your arrows land they will create an explosion.");
					sb.append("\n");
					sb.append("  &5Firework&7:&f For &a" + ItemPlugin.fileconfig.getInt("actives.firework.activetime30")
							+ "s&f your arrows will explode in &5fireworks&f mid flight.");
					sb.append("\n");
					sb.append("  &5Poison arrow&7:&f Your next &5"
							+ ItemPlugin.fileconfig.getInt("actives.poison_arrow.activetime30")
							+ "&f shots will have arrows that will give your &cenemy&f &5poison&f for &55s&f.");
					sb.append("\n");
					sb.append(
							"  &5Auto Fire&7:&f Hold &5right click&fqq to auto fire every item in your inventory for &5"
									+ ItemPlugin.fileconfig.getInt("actives.auto_fire.activetime30") + "s&f.");
					sb.append("\n");
				}
				return sb.toString();
			}
		};

		public abstract String getPassive(int level);

		public abstract String getActive(int level);

		public static ItemType fromItem(ItemStack item) {
			if (item == null)
				return null;
			String name = item.getType().name().toLowerCase();
			if (name.contains("pickaxe"))
				return ItemType.PICK;
			if (name.contains("spade"))
				return ItemType.SHOVEL;
			if (name.contains("sword"))
				return ItemType.SWORD;
			if (name.contains("_axe"))
				return ItemType.AXE;
			if (name.contains("_hoe"))
				return ItemType.HOE;
			if (item.getType().equals(Material.BOW))
				return ItemType.BOW;
			if (arrayContains(name, Arrays.asList("helmet", "chestplate", "boots", "leggings")))
				return ItemType.ARMOR;
			return null;
		}

	}

	// public static void main(String[] args) {
	// System.out.println(percentage(17, 1, 15));
	// System.out.println(((float)5 / (30 - 30)) * (30-30));
	// }
	//
	// static DecimalFormat format = new DecimalFormat("#.##");
	// public static String percentage(int startLevel, int maxPercent, int level) {
	// if(level == 0)
	// return "0";
	// int i = level-startLevel, ii = 30 - startLevel;
	// return format.format(((float)maxPercent / (ii <= 0 ? 1 : ii)) * (i <= 0 ? 1 :
	// i));
	// }
	//
	// public static double percentage0(int startLevel, int maxPercent, int level) {
	// return Double.valueOf(percentage(startLevel, maxPercent, level));
	// }
	//
	private static boolean arrayContains(String contains, List<String> what) {
		return what.stream().filter((s) -> contains.contains(s)).count() > 0;
	}

	public CustomItem item;

	public ItemLore(CustomItem item) {
		this.item = item;
	}

	public List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(colour("&8Enchantment Slot Available: &5 "
				+ (item.getEnchantSlots() - item.getItem().getEnchantments().size())));
		lore.add("");
		if (item.getLevel() < 30)
			lore.add(colour("&7Level: &9" + item.getLevel() + " &f | &7XP: &9" + item.getXP() + " &7/&9 "
					+ item.levelxp));
		else
			lore.add(colour("&7Level: &930 &8- &aMax Level"));
		if (item.getLevel() < 30) {
			int percent = (int) ((((float) item.getXP())
					/ item.levelxp) * 100);
			StringBuilder sb = new StringBuilder("&7");
			for (int i = 0; i < ((int) percent / 5); i++)
				sb.append("&a█");
			for (int i = 0; i < (20 - ((int) percent / 5)); i++)
				sb.append("&c█");
			lore.add(colour(sb.toString()));
		} else {
			lore.add(colour("&a████████████████████"));
		}
		if (item.countSoulBound()>0)
			lore.add(colour("&bSoulBound: " + item.countSoulBound()));
		lore.add(" ");

		String passive = ItemType.fromItem(item.getItem()).getPassive(item.getLevel());
		String active = ItemType.fromItem(item.getItem()).getActive(item.getLevel());
		if (passive != null)
			for (String line : passive.split("\n"))
				lore.add(colour(line));
		if (active != null)
			for (String line : active.split("\n"))
				lore.add(colour(line));

		return lore;
	}

}
