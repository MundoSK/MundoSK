package com.pie.tlatoani.Miscellaneous.Matcher;

import ch.njol.skript.classes.Comparator;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Conditional;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.registrations.Comparators;
import com.pie.tlatoani.Miscellaneous.Matcher.ScopeMatcher;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Util.CustomScope;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/20/16.
 */
public class ScopeMatches extends CustomScope {
    private Expression<Object> object1Expression;
    private Expression<Object> object2Expression;

    @Override
    public boolean go(Event event) {
        return object2Expression != null && Comparators.compare(object1Expression.getSingle(event), object2Expression.getSingle(event)) == Comparator.Relation.EQUAL;
    }

    @Override
    public String getString() {
        return "matches " + object1Expression;
    }

    @Override
    public boolean init() {
        object1Expression = (Expression<Object>) exprs[0];
        return true;
    }

    @Override
    public void setScope() {
        if (scope.getParent() instanceof Conditional) {
            Condition condition = null;
            try {
                condition = (Condition) CustomScope.condition.get(scope.getParent());
            } catch (IllegalAccessException e) {
                Mundo.reportException(this, e);
            }
            if (condition instanceof ScopeMatcher) {
                object2Expression = ((ScopeMatcher) condition).objectExpression;
                last.setNext(scope.getParent().getNext());
            }
        }
    }
}
