package com.pie.tlatoani.Core.Static;

import com.pie.tlatoani.Mundo;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Tlatoani on 8/10/17.
 */
public class Scheduling {

    private static BukkitScheduler scheduler;

    public static void load() {
        scheduler = Bukkit.getScheduler();
    }

    public static void sync(Runnable runnable) {
        scheduler.runTask(Mundo.get(), runnable);
    }

    public static void async(Runnable runnable) {
        scheduler.runTaskAsynchronously(Mundo.get(), runnable);
    }

    public static void syncDelay(int ticks, Runnable runnable) {
        scheduler.runTaskLater(Mundo.get(), runnable, ticks);
    }

    public static void asyncDelay(int ticks, Runnable runnable) {
        scheduler.runTaskLaterAsynchronously(Mundo.get(), runnable, ticks);
    }

    public static void syncLock(Runnable runnable) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Scheduling.sync(() -> {
            runnable.run();
            countDownLatch.countDown();
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Logging.reportException(runnable, e);
        }
    }
}
