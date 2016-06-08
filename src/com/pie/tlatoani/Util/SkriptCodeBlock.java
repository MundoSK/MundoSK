package com.pie.tlatoani.Util;

import ch.njol.skript.lang.TriggerItem;
import com.pie.tlatoani.Mundo;
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
        Mundo.debug(this, "First: " + first);
        while (going != null) {
            try {
                Boolean whetherToContinue = (Boolean) run.invoke(going, event);
                Mundo.debug(this, "whtertContiue: " + whetherToContinue);
                going = whetherToContinue ? going.getNext() : null;
                Mundo.debug(this, "going: " + going);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                going = null;
            }
        }
    }
}
