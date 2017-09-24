package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.StringMode;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import ch.njol.util.Pair;
import org.bukkit.event.Event;

import java.util.Iterator;
import java.util.TreeMap;

/**
 * Created by Tlatoani on 7/27/16.
 */
public class ExprIndexesOfListVariable extends SimpleExpression<String> {
    private Variable listVariable;
    private VariableString variableString;

    @Override
    protected String[] get(Event event) {
        TreeMap<String, Object> treeMap = (TreeMap) Variables.getVariable(variableString.toString(event), event, listVariable.isLocal());
        if (treeMap == null) {
            return new String[0];
        }
        return treeMap.keySet().toArray(new String[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "indexes of " + listVariable;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (exprs[0] instanceof Variable && ((Variable) exprs[0]).isList()) {
            listVariable = (Variable) exprs[0];
            String origstring = listVariable.isLocal() ? listVariable.toString().substring(2, listVariable.toString().length() - 1) : listVariable.toString().substring(1, listVariable.toString().length() - 1);
            variableString = VariableString.newInstance(origstring, StringMode.VARIABLE_NAME);
            return true;
        }
        Skript.error("'indexes of %listvariable%' must be used with a list variable!");
        return false;
    }
}
