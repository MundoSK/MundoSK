package com.pie.tlatoani.CodeBlock;

import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.variables.Variables;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Util.CustomScope;
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
    private String[] argumentNames;
    private String returnName;

    public static final String constantVariableName = "constant";

    static {
        try {
            run = TriggerItem.class.getDeclaredMethod("run", Event.class);
            run.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public ScopeCodeBlock(TriggerItem first, boolean hasConstant, String[] argumentNames, String returnName) {
        this.first = first;
        this.hasConstant = hasConstant;
        this.argumentNames = argumentNames;
        this.returnName = returnName;
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

    public Object execute(Event event, boolean preserveOldValues) {
        Object preservation = null;
        if (hasConstant) {
            if (preserveOldValues) {
                preservation = Variables.getVariable(constantVariableName, event, true);
            }
            Variables.setVariable(constantVariableName, constantValue, event, true);
        }
        TriggerItem going = first;
        Mundo.debug(this, "FIRST:: " + first);
        Mundo.debug(this, "GETPARETN:: " + first.getParent());
        TriggerItem end = first.getParent().getNext();
        Mundo.debug(this, "First: " + first);
        Mundo.debug(this, "End: " + end);
        while (going != null && going != end) {
            try {
                going = (TriggerItem) CustomScope.walkmethod.invoke(going, event);
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
        if (returnName != null) {
            return Variables.getVariable(returnName, event, true);
        } else {
            return null;
        }
    }

    @Override
    public Object execute(Object[] args) {
        EmptyEvent event = new EmptyEvent();
        if (argumentNames != null) {
            for (int i = 0; i < Math.min(argumentNames.length, args.length); i++) {
                if (args[i] instanceof Object[]) {
                    Mundo.setListVariable(argumentNames[i], Mundo.listVariableFromArray((Object[]) args[i]), event, true);
                } else if (args[i] instanceof TreeMap) {
                    Mundo.setListVariable(argumentNames[i], (TreeMap<String, Object>) args[i], event, true);
                } else {
                    event.setLocalVariable(argumentNames[i], args[i]);
                }
            }
        }
        return execute(event, false);
    }

}
