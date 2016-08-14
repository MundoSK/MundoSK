package com.pie.tlatoani.CodeBlock;

import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.function.ScriptFunction;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;

import java.lang.reflect.Field;

/**
 * Created by Tlatoani on 8/14/16.
 */
public class FunctionCodeBlock implements CodeBlock {
    Trigger trigger;

    public static Field triggerField;

    static {
        try {
            triggerField = ScriptFunction.class.getDeclaredField("trigger");
            triggerField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Mundo.reportException(FunctionCodeBlock.class, e);
        }
    }

    public FunctionCodeBlock(ScriptFunction function) {
        try {
            trigger = (Trigger) triggerField.get(function);
        } catch (IllegalAccessException | ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(Event event) {
        trigger.execute(event);
    }
}
