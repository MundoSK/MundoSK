package com.pie.tlatoani.Core.Skript;

import ch.njol.skript.classes.Changer;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/16/17.
 */
public abstract class ChangeablePropertyExpression<F, T> extends MundoPropertyExpression<F, T> {

    public abstract void change(F f, T t, Changer.ChangeMode changeMode);

    public abstract Changer.ChangeMode[] getChangeModes();

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode changeMode) {
        F[] fs = getExpr().getArray(event);
        if (changeMode == Changer.ChangeMode.SET || changeMode == Changer.ChangeMode.ADD || changeMode == Changer.ChangeMode.REMOVE || changeMode == Changer.ChangeMode.REMOVE_ALL) {
            T t = (T) delta[0];
            for (F f : fs) {
                change(f, t, changeMode);
            }
        } else {
            for (F f : fs) {
                change(f, null, changeMode);
            }
        }

    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode changeMode) {
        if (CollectionUtils.contains(getChangeModes(), changeMode)) {
            return new Class[]{getReturnType()};
        }
        return null;
    }

}
