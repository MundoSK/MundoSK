package com.pie.tlatoani.CodeBlock;

import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.ScriptFunction;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;

import java.lang.reflect.Field;

/**
 * Created by Tlatoani on 8/14/16.
 */
public class FunctionCodeBlock implements CodeBlock {
    Function function;
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
        this.function = function;
        try {
            trigger = (Trigger) triggerField.get(function);
        } catch (IllegalAccessException | ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(Event event, boolean preserveOldValues) {
        trigger.execute(event);
    }

    @Override
    public void execute(Object[] args) {
        Object[][] funcArgs = new Object[args.length][];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Object[]) {
                funcArgs[i] = (Object[]) args[i];
            } else {
                funcArgs[i] = new Object[]{args[i]};
            }
        }
        function.execute(funcArgs);
    }
}
