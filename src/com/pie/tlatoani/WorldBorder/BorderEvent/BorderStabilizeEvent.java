package com.pie.tlatoani.WorldBorder.BorderEvent;

import com.pie.tlatoani.Core.Static.Scheduling;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.WorldEvent;

public class BorderStabilizeEvent extends WorldEvent {
	private static final HandlerList handlers = new HandlerList();
	
	public BorderStabilizeEvent(World world) {
		super(world);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

	public static class Caller implements Runnable {
	    private boolean valid = true;
	    private final World world;
	    private final WorldBorderImpl border;

	    Caller(World world, WorldBorderImpl border) {
	        this.world = world;
	        this.border = border;
        }

        void schedule(int ticks) {
            Scheduling.syncDelay(ticks, this);
        }

        void invalidate() {
	        valid = false;
        }

        @Override
        public void run() {
            if (valid) {
                border.onStabilize();
                Bukkit.getServer().getPluginManager().callEvent(new BorderStabilizeEvent(world));
            }
        }
    }

}
