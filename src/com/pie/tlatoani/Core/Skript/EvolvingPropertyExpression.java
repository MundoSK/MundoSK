package com.pie.tlatoani.Core.Skript;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;

import java.lang.reflect.Method;

/**
 * Created by Tlatoani on 8/18/17.
 */
public abstract class EvolvingPropertyExpression<F, T> extends MundoPropertyExpression<F, T> {

    public abstract F set(F f, T t);

    public F reset(F f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        F[] fs = getExpr().getArray(event);
        if (mode == Changer.ChangeMode.SET) {
            for (F f : fs) {
                T t = (T) delta[0];
                getExpr().change(event, new Object[]{set(f, t)}, Changer.ChangeMode.SET);
            }
        } else if (mode == Changer.ChangeMode.RESET || mode == Changer.ChangeMode.DELETE) {
            for (F f : fs) {
                getExpr().change(event, new Object[]{reset(f)}, Changer.ChangeMode.SET);
            }
        } else {
            throw new IllegalArgumentException("Illegal mode: " + mode);
        }
    }

    public boolean exprIsSettable() {
        Expression<? extends F> expression = getExpr();
        Class[] classes = expression.acceptChange(Changer.ChangeMode.SET);
        if (classes == null) {
            return false;
        }
        for (Class c : classes) {
            if (c.isAssignableFrom(expression.getReturnType())) {
                return true;
            }
        }
        return false;
    }

    public boolean isChangeable(Changer.ChangeMode mode) {
        if (!exprIsSettable()) {
            return false;
        } else if (mode == Changer.ChangeMode.SET) {
            return true;
        } else if (mode == Changer.ChangeMode.RESET || mode == Changer.ChangeMode.DELETE) {
            for (Method method : this.getClass().getMethods()) {
                if (method.getName().equals("reset") && method.getParameterCount() == 1) {
                    return method.getDeclaringClass() != EvolvingPropertyExpression.class;
                }
            }
            throw new IllegalStateException("The method reset(F f) exists, and should have been found");
        }
        return false;
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        return isChangeable(mode) ? CollectionUtils.array(getReturnType()) : null;
    }
}
