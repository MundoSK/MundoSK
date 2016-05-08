package com.pie.tlatoani.Util;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.WeakHashMap;

/**
 * Created by Tlatoani on 5/7/16.
 */
public class ExprTreeOfListVariable extends SimpleExpression<Object> {
    private Variable listVariable;
    private WeakHashMap<Event, TreeIterator> iteratorWeakHashMap = new WeakHashMap<Event, TreeIterator>();

    @Override
    protected Object[] get(Event event) {
        throw new UnsupportedOperationException("'tree of %objects%' should only be used in loops!!");
    }

    @Override
    public Iterator<?> iterator(Event event) {
        TreeMap<String, Object> treeMap = (TreeMap) Variables.getVariable(listVariable.isLocal() ? listVariable.toString().substring(2, listVariable.toString().length() - 1) : listVariable.toString().substring(1, listVariable.toString().length() - 1), event, listVariable.isLocal());
        if (treeMap != null) {
            TreeIterator result = new TreeIterator(treeMap);
            iteratorWeakHashMap.put(event, result);
            return result;
        }
        return new Iterator<Object>() {

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Object next() {
                return null;
            }
        };
    }

    public String getBranch(Event event) {
        return iteratorWeakHashMap.get(event).currentIndex();
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "tree of %objects%";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (exprs[0] instanceof Variable && ((Variable) exprs[0]).isList()) {
            listVariable = (Variable) exprs[0];
            return true;
        }
        Skript.error("'tree of %objects%' must be used with a list variable!");
        return false;
    }
}
