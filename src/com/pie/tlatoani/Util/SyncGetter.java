package com.pie.tlatoani.Util;

import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.Core.Static.Scheduling;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Tlatoani on 7/6/16.
 */
public abstract class SyncGetter<T> implements Runnable {
    private T result;
    private CountDownLatch countDownLatch;
    private static BukkitScheduler scheduler = Bukkit.getScheduler();

    @Override
    public void run() {
        result = getRaw();
        countDownLatch.countDown();
    }

    protected abstract T getRaw();

    public T getSync() {
        countDownLatch = new CountDownLatch(1);
        Scheduling.sync(this);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Logging.debug(this, e);
        }
        return result;
    }
}
