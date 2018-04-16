package com.pie.tlatoani.Miscellaneous.Tree;

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
import ch.njol.util.coll.iterator.EmptyIterator;
import com.pie.tlatoani.Util.Collections.PairIterator;
import org.bukkit.event.Event;

import java.util.*;

/**
 * Created by Tlatoani on 5/7/16.
 */
public class ExprTreeOfListVariable extends SimpleExpression<Object> {
    private Variable listVariable;
    private VariableString variableString;
    private WeakHashMap<Event, PairIterator<String, Object>> iteratorWeakHashMap = new WeakHashMap<>();



    @Override
    protected Object[] get(Event event) {
        throw new UnsupportedOperationException("'tree of %objects%' should only be used in loops!!");
    }

    @Override
    public Iterator<?> iterator(Event event) {
        TreeMap<String, Object> treeMap = (TreeMap) Variables.getVariable(variableString.toString(event), event, listVariable.isLocal());
        if (treeMap != null) {
            //ChangePermissiveTreeIterator result = new ChangePermissiveTreeIterator(treeMap);
            PairIterator<String, Object> result = new PairIterator<>(addBranches(treeMap, new LinkedList<>(), "").iterator());
            iteratorWeakHashMap.put(event, result);
            return result;
        }
        return new EmptyIterator<>();
    }

    public static <L extends List<Pair<String, Object>>> L addBranches(TreeMap<String, Object> treeMap, L pairs, String prefix) {
        treeMap.forEach((key, value) -> {
            if (value instanceof TreeMap) {
                addBranches((TreeMap<String, Object>) value, pairs, prefix + key + Variable.SEPARATOR);
            } else {
                pairs.add(new Pair<>(prefix + key, value));
            }
        });
        return pairs;
    }

    @Override
    public boolean isLoopOf(String pattern) {
        return pattern.equals("value");
    }

    public String getBranch(Event event) {
        //return iteratorWeakHashMap.get(event).currentIndex();
        return iteratorWeakHashMap.get(event).key();
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
        return "tree of " + listVariable;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (exprs[0] instanceof Variable && ((Variable) exprs[0]).isList()) {
            listVariable = (Variable) exprs[0];
            String origstring = listVariable.isLocal() ? listVariable.toString().substring(2, listVariable.toString().length() - 1) : listVariable.toString().substring(1, listVariable.toString().length() - 1);
            variableString = VariableString.newInstance(origstring, StringMode.VARIABLE_NAME);
            return true;
        }
        Skript.error("'tree of %objects%' must be used with a list variable!");
        return false;
    }
}
