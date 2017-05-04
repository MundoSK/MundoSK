package com.pie.tlatoani.Miscellaneous.MiscBukkit;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 12/22/16.
 */
public class ExprEntityCanCollide extends SimpleExpression<Boolean> {
    private Expression<LivingEntity> livingEntityExpression;

    @Override
    protected Boolean[] get(Event event) {
        return new Boolean[]{livingEntityExpression.getSingle(event).isCollidable()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return livingEntityExpression + " is collidable";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        livingEntityExpression = (Expression<LivingEntity>) expressions[0];
        return true;
    }

    public void change(Event arg0, Object[] delta, Changer.ChangeMode mode){
        if (mode == Changer.ChangeMode.SET){
            livingEntityExpression.getSingle(arg0).setCollidable((Boolean) delta[0]);
        }
    }

    @SuppressWarnings("unchecked")
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Boolean.class);
        }
        return null;
    }
}
