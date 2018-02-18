package com.pie.tlatoani.WorldBorder.BorderEvent;

import com.pie.tlatoani.Mundo;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.HashMap;
import java.util.Map;

public final class BorderManager implements Listener, Runnable {
	private static Map<World, Double> stablesizes = new HashMap<World, Double>();
	private static Map<World, Double> stabletimes = new HashMap<World, Double>();
	private static Map<World, Double> origsizes = new HashMap<World, Double>();
	private World borderevent;
	
	private BorderManager(World borderarg) {
		borderevent = borderarg;
	}
	
	public static void changeSize(World borderarg, Double sizearg, Double timearg) {
		if (borderarg.getWorldBorder().getSize() != sizearg) {
			stablesizes.put(borderarg, sizearg);
			stabletimes.put(borderarg, timearg);
			origsizes.put(borderarg, borderarg.getWorldBorder().getSize());
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Mundo.get(), new BorderManager(borderarg), timearg.longValue()*20);
		}
	}
	
	public static Double getStableSize(World borderarg) {
		if (stablesizes.containsKey(borderarg)) return stablesizes.get(borderarg);
		else return null;
	}
	
	public static Double getRemainingTime(World borderarg) {
		if (stablesizes.containsKey(borderarg)) {
			Double diff = stablesizes.get(borderarg) - origsizes.get(borderarg);
			Double left = stablesizes.get(borderarg) - borderarg.getWorldBorder().getSize();
			return (left * stabletimes.get(borderarg))/diff;
		} else return null;
	}
	
	public static Double getOriginalSize(World borderarg) {
		if (stablesizes.containsKey(borderarg)) return origsizes.get(borderarg);
		else return null;
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public static void onUnloadWorld(WorldUnloadEvent e) {
		if (stablesizes.containsKey(e.getWorld())) {
			stablesizes.remove(e.getWorld());
			stabletimes.remove(e.getWorld());
			origsizes.remove(e.getWorld());
		}
	}

	@Override
	public void run() {
		if (stablesizes.containsKey(borderevent)) {
			if (borderevent.getWorldBorder().getSize() - stablesizes.get(borderevent) <= 1 && borderevent.getWorldBorder().getSize() - stablesizes.get(borderevent) >= -1 ) {
				stablesizes.remove(borderevent);
				stabletimes.remove(borderevent);
				origsizes.remove(borderevent);
				BorderStabilizeEvent event = new BorderStabilizeEvent(borderevent);
				Bukkit.getServer().getPluginManager().callEvent(event);
			}
		}
	}

}
