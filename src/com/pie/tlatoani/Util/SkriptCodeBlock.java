package com.pie.tlatoani.Util;

import ch.njol.skript.lang.TriggerItem;
import org.bukkit.event.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Tlatoani on 6/5/16.
 */
public class SkriptCodeBlock {
    public static Method run;
    private TriggerItem first;

    static {
        try {
            run = TriggerItem.class.getDeclaredMethod("run", Event.class);
            run.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public SkriptCodeBlock(TriggerItem first) {
        this.first = first;
    }

    public void execute(Event event) {
        TriggerItem going = first;
        while (going != null) {
            try {
                going = (Boolean) run.invoke(going, event) ? going.getNext() : null;
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                going = null;
            }
        }
    }
}
