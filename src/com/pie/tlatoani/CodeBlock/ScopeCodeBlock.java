package com.pie.tlatoani.CodeBlock;

import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.variables.Variables;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Util.EmptyEvent;
import org.bukkit.event.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.TreeMap;

/**
 * Created by Tlatoani on 8/14/16.
 */
public class ScopeCodeBlock implements CodeBlock {
    public static Method run;
    private TriggerItem first;
    private boolean hasConstant;
    private Object constantValue = null;
    private String[] argumentNames = null;

    public static final String constantVariableName = "constant";

    static {
        try {
            run = TriggerItem.class.getDeclaredMethod("run", Event.class);
            run.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public ScopeCodeBlock(TriggerItem first, boolean hasConstant) {
        this.first = first;
        this.hasConstant = hasConstant;
    }

    public ScopeCodeBlock(TriggerItem first, boolean hasConstant, String[] argumentNames) {
        this.first = first;
        this.hasConstant = hasConstant;
        this.argumentNames = argumentNames;
    }

    public void setConstantSingle(Object constantValue) {
        this.constantValue = constantValue;
    }

    public void setConstantArray(Object[] constantValue) {
        TreeMap<String, Object> treeMap = new TreeMap<String, Object>();
        for (int i = 1; i <= constantValue.length; i++) {
            treeMap.put("" + i, constantValue[i - 1]);
        }
        this.constantValue = treeMap;
    }

    public void setConstantListVariable(TreeMap<String, Object> constantValue) {
        this.constantValue = constantValue.clone();
    }

    public void execute(Event event, boolean preserveOldValues) {
        Object preservation = null;
        if (hasConstant) {
            if (preserveOldValues) {
                preservation = Variables.getVariable(constantVariableName, event, true);
            }
            Variables.setVariable(constantVariableName, constantValue, event, true);
        }
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
        if (hasConstant) {
            constantValue = Variables.getVariable(constantVariableName, event, true);
            if (preserveOldValues) {
                Variables.setVariable(constantVariableName, preservation, event, true);
            }
        }
    }

    @Override
    public void execute(Object[] args) {
        EmptyEvent event = new EmptyEvent();
        if (argumentNames != null) {
            for (int i = 0; i < Math.min(argumentNames.length, args.length); i++) {
                event.setLocalVariable(argumentNames[i], args[i]);
            }
        }
    }
}
