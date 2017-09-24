package com.pie.tlatoani.CodeBlock;

import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.FunctionEvent;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.variables.Variables;
import com.pie.tlatoani.Util.Logging;
import org.bukkit.event.Event;

import java.lang.reflect.Field;
import java.util.WeakHashMap;

/**
 * Created by Tlatoani on 8/14/16.
 */
public class FunctionCodeBlock implements CodeBlock {
    Function function;

    private static WeakHashMap weakHashMap;

    static {
        try {
            Field localVariables = Variables.class.getDeclaredField("localVariables");
            localVariables.setAccessible(true);
            weakHashMap = (WeakHashMap) localVariables.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Logging.reportException(FunctionCodeBlock.class, e);
        }
    }

    public FunctionCodeBlock(Function function) {
        this.function = function;
    }

    @Override
    public Object execute(Event event, boolean preserveOldValues) {
        FunctionEvent functionEvent = new FunctionEvent();
        weakHashMap.put(functionEvent, weakHashMap.get(event));
        Parameter[] parameters = function.getParameters();
        Object[][] args = new Object[parameters.length][1];
        for (int i = 0; i < parameters.length; i++) {
            String paramToString = parameters[i].toString();
            args[i][0] = Variables.getVariable(paramToString.substring(0, paramToString.indexOf(':')), event, true);
        }
        return function.execute(functionEvent, args);
    }

    @Override
    public Object execute(Object[] args) {
        Logging.debug(this, "START:: " + args);
        Object[][] funcArgs = new Object[args.length][];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Object[]) {
                funcArgs[i] = (Object[]) args[i];
            } else {
                funcArgs[i] = new Object[]{args[i]};
            }
        }
        Logging.debug(this, "THEN:: " + funcArgs);
        Object result = function.execute(funcArgs);
        Logging.debug(this, "NOW:: " + result);
        return result;
    }
}
