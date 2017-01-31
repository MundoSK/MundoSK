package com.pie.tlatoani.Util;

import com.pie.tlatoani.Mundo;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Tlatoani on 7/6/16.
 */
public abstract class SyncGetter<T> implements Runnable {
    private T result;
    private CountDownLatch countDownLatch;

    @Override
    public void run() {
        result = getRaw();
        countDownLatch.countDown();
    }

    protected abstract T getRaw();

    public T getSync() {
        countDownLatch = new CountDownLatch(1);
        Mundo.sync(this);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Mundo.debug(this, e);
        }
        return result;
    }
}
