package com.pie.tlatoani.Miscellaneous.MiscBukkit;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Core.Static.Utilities;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 12/22/16.
 */
public class CondCollidable extends SimpleExpression<Boolean> {
    private Expression<LivingEntity> livingEntityExpression;
    private boolean positive;

    @Override
    protected Boolean[] get(Event event) {
        return new Boolean[]{Utilities.check(livingEntityExpression, event, LivingEntity::isCollidable, positive)};
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
        return livingEntityExpression + " " + (positive ? "is" : "isn't") + " collidable";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        livingEntityExpression = (Expression<LivingEntity>) expressions[0];
        positive = parseResult.mark == 0;
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        if (mode == Changer.ChangeMode.SET){
            Boolean collidable = positive == (Boolean) delta[0];
            for (LivingEntity livingEntity : livingEntityExpression.getArray(event)) {
                livingEntity.setCollidable(collidable);
            }
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
