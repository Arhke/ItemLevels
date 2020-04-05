package me.crz.minecraft.itemsystem;

import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import de.slikey.effectlib.Effect;

public class Bleed {
	public static void BleedEffect(LivingEntity le, int i) {
		Effect effect = new de.slikey.effectlib.effect.BleedEffect(ItemPlugin.EM);
		effect.setTargetEntity(le);
		effect.start();
		BukkitTask bt = new BukkitRunnable() {
			@Override
			public void run() {
				
				le.damage(1);
				le.addAttachment(ItemPlugin.getInstance(), 1);
			}

		}.runTaskTimer(ItemPlugin.getInstance(), 0L, 10L);
		new BukkitRunnable() {
			@Override
			public void run() {
				bt.cancel();
			}
		}.runTaskLater(ItemPlugin.getInstance(), 20*20);
		effect.period = 20;
		effect.iterations = i;
	}

}
