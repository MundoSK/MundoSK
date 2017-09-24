package com.pie.tlatoani.CustomEventNew;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Tlatoani on 7/15/17.
 */
public class SkriptCustomEvent extends Event {
    public static final HandlerList HANDLER_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
