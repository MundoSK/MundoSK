package com.pie.tlatoani.CodeBlock;

import ch.njol.skript.lang.TriggerItem;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Tlatoani on 8/14/16.
 */
public class ScopeCodeBlock implements CodeBlock {
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

    public ScopeCodeBlock(TriggerItem first) {
        this.first = first;
    }

    public void execute(Event event) {
        TriggerItem going = first;
        TriggerItem end = first.getParent().getNext();
        Mundo.debug(this, "First: " + first);
        Mundo.debug(this, "End: " + end);
        while (going != null && going != end) {
            try {
                run.invoke(going, event);
                going = going.getNext();
                Mundo.debug(this, "going: " + going);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                going = null;
            }
        }
    }
}
