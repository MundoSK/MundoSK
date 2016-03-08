package com.pie.tlatoani.WorldBorder;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UtilBorderStabilize extends Event{
	private static final HandlerList handlers = new HandlerList();
	private World border;
	
	public UtilBorderStabilize(World borderarg) {
		border = borderarg;
	}

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		// TODO Auto-generated method stub
		return handlers;
	}
	
	public World getWorld() {
		return border;
	}

}
