package me.crz.minecraft.itemsystem.utils;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class MetaValue extends Base implements MetadataValue {

	@Override
	public Object value() {
		return null;
	}

	public boolean asBoolean() {
		return false;
	}

	@Override
	public String asString() {
		return null;
	}

	@Override
	public Plugin getOwningPlugin() {
		return getPlugin();
	}

	@Override
	public void invalidate() {
	}

	@Override
	public int asInt() {
		return 0;
	}

	@Override
	public float asFloat() {
		return 0;
	}

	@Override
	public double asDouble() {
		return 0;
	}

	@Override
	public long asLong() {
		return 0;
	}

	@Override
	public short asShort() {
		return 0;
	}

	@Override
	public byte asByte() {
		return 0;
	}

}
