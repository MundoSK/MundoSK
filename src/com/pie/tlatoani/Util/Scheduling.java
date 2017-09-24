package com.pie.tlatoani.Util;

import com.pie.tlatoani.Mundo;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Created by Tlatoani on 8/10/17.
 */
public class Scheduling {

    private static BukkitScheduler scheduler;

    public static void load() {
        scheduler = Bukkit.getScheduler();
    }

    public static void sync(Runnable runnable) {
        scheduler.runTask(Mundo.INSTANCE, runnable);
    }

    public static void async(Runnable runnable) {
        scheduler.runTaskAsynchronously(Mundo.INSTANCE, runnable);
    }

    public static void syncDelay(int ticks, Runnable runnable) {
        scheduler.runTaskLater(Mundo.INSTANCE, runnable, ticks);
    }

    public static void asyncDelay(int ticks, Runnable runnable) {
        scheduler.runTaskLaterAsynchronously(Mundo.INSTANCE, runnable, ticks);
    }
}
