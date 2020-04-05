package me.crz.minecraft.itemsystem.utils;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled;
	private String cancelReason;

	public boolean isCancelled() {
		return cancelled;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelled(boolean cancelled) {
		setCancelled(cancelled, null);
	}

	public void setCancelled(boolean cancelled, String cancelReason) {
		this.cancelled = cancelled;
		this.cancelReason = cancelReason;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
