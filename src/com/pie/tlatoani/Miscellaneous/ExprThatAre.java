package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Comparator;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Comparators;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tlatoani on 1/3/17.
 */
public class ExprThatAre extends SimpleExpression<Object> {
    private Expression<Object> objects;
    private Expression<Object> compareTarget;
    private Comparator comparator;
    private boolean negated;

    private List<Object> getList(Event event) {
        List<Object> list = new LinkedList<>(Arrays.asList(objects.getArray(event)));
        Object compareTarget = this.compareTarget.getSingle(event);
        if (compareTarget instanceof ClassInfo) {
            ClassInfo type = (ClassInfo) compareTarget;
            list.removeIf(object -> type.getC().isInstance(compareTarget) == negated);
        }
        if (comparator == null) {
            list.removeIf(object -> (Comparators.compare(object, compareTarget) == Comparator.Relation.EQUAL) == negated);
        } else {
            list.removeIf(object -> (comparator.compare(object, compareTarget) == Comparator.Relation.EQUAL) == negated);
        }
        return list;
    }

    @Override
    protected Object[] get(Event event) {
        return getList(event).toArray();
    }

    @Override
    public Iterator<Object> iterator(Event event) {
        return getList(event).iterator();
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<?> getReturnType() {
        return objects.getReturnType();
    }

    @Override
    public String toString(Event event, boolean b) {
        return objects + " that are " + compareTarget;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        objects = (Expression<Object>) expressions[0];
        compareTarget = (Expression<Object>) expressions[1];
        negated = parseResult.mark == 1;
        comparator = Comparators.getComparator(objects.getReturnType(), compareTarget.getReturnType());
        if (comparator == null && objects.getReturnType() != Object.class && compareTarget.getReturnType() != Object.class) {
            Skript.error("The elements of '" + objects + "' cannot be compared with '" + compareTarget + "'!");
            return false;
        }
        return true;
    }
}
