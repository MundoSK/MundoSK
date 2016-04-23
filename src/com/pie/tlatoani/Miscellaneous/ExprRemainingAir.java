package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.WorldBorder.UtilBorderManager;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

/**
 * Created by Tlatoani on 4/23/16.
 */
public class ExprRemainingAir extends SimpleExpression<Timespan> {
    private Expression<LivingEntity> entity;

    @Override
    public Class<? extends Timespan> getReturnType() {
        // TODO Auto-generated method stub
        return Timespan.class;
    }

    @Override
    public boolean isSingle() {
        // TODO Auto-generated method stub
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, SkriptParser.ParseResult arg3) {
        // TODO Auto-generated method stub
        entity = (Expression<LivingEntity>) expr[0];
        return true;
    }

    @Override
    public String toString(@Nullable Event arg0, boolean arg1) {
        // TODO Auto-generated method stub
        return "remaining air";
    }

    @Override
    @Nullable
    protected Timespan[] get(Event arg0) {
        return new Timespan[]{new Timespan(entity.getSingle(arg0).getRemainingAir() * 50)};
    }

    public void change(Event arg0, Object[] delta, Changer.ChangeMode mode){
        LivingEntity living = entity.getSingle(arg0);
        Integer time = (new Long(((Timespan)delta[0]).getMilliSeconds())).intValue() / 50;
        if (mode == Changer.ChangeMode.SET){
            living.setRemainingAir(time);
        }
        else if (mode == Changer.ChangeMode.ADD) {
            living.setRemainingAir(living.getRemainingAir() + time);
        }
        else if (mode == Changer.ChangeMode.REMOVE) {
            living.setRemainingAir(living.getRemainingAir() - time);
        }
    }

    @SuppressWarnings("unchecked")
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE) {
            return CollectionUtils.array(Timespan.class);
        }
        return null;
    }

}