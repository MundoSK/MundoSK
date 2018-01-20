package com.pie.tlatoani.CustomEventNew;

import org.bukkit.event.Cancellable;

/**
 * Created by Tlatoani on 7/15/17.
 */
public class CancellableSkriptCustomEvent extends SkriptCustomEvent implements Cancellable {
    private boolean cancelled = false;

    public CancellableSkriptCustomEvent(CustomEventInfo info) {
        super(info);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
