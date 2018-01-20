package com.pie.tlatoani.CustomEventNew;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tlatoani on 7/15/17.
 */
public class SkriptCustomEvent extends Event {
    private static final Map<Class<? extends SkriptCustomEvent>, HandlerList> handerListMap = new HashMap<>();

    public final CustomEventInfo info;
    public final Object[] eventValues;
    public final Object[] specificExpressions;

    public SkriptCustomEvent(CustomEventInfo info) {
        if (info.eventClass != getClass()) {
            throw new IllegalArgumentException("Invalid info being used to instantiate a SkriptCustomEvent");
        }
        this.info = info;
        this.eventValues = new Object[info.eventValues.size()];
        this.specificExpressions = new Object[info.specificExpressions.size()];
    }

    @Override
    public HandlerList getHandlers() {
        return handerListMap.computeIfAbsent(getClass(), __ -> new HandlerList());
    }
}
