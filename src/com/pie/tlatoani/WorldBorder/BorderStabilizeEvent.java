package com.pie.tlatoani.WorldBorder;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BorderStabilizeEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	private World border;
	
	public BorderStabilizeEvent(World borderarg) {
		border = borderarg;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public World getWorld() {
		return border;
	}

}
