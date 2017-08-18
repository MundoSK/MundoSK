package com.pie.tlatoani.Util;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/18/17.
 */
public abstract class EvolvingPropertyExpression<F, T> extends MundoPropertyExpression<F, T> {

    public abstract F evolve(F f, T t);

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            F[] fs = getExpr().getArray(event);
            for (F f : fs) {
                T t = (T) delta[0];
                getExpr().change(event, new Object[]{evolve(f, t)}, Changer.ChangeMode.SET);
            }
        } else {
            throw new IllegalArgumentException("Illegal mode: " + mode);
        }
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            Expression<? extends F> expression = getExpr();
            Class[] classes = expression.acceptChange(Changer.ChangeMode.SET);
            if (classes == null) {
                return null;
            }
            for (Class c : classes) {
                if (c.isAssignableFrom(expression.getReturnType())) {
                    return CollectionUtils.array(getReturnType());
                }
            }
        }
        return null;
    }
}
