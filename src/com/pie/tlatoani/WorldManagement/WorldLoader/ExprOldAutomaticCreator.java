package com.pie.tlatoani.WorldManagement.WorldLoader;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.WorldCreator;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/16/16.
 */
public class ExprOldAutomaticCreator extends SimpleExpression<WorldCreator> {
    private Expression<String> stringExpression;

    @Override
    protected WorldCreator[] get(Event event) {
        //return new WorldCreator[]{WorldLoader.getCreator(stringExpression.getSingle(event))};
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends WorldCreator> getReturnType() {
        return WorldCreator.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "automatic creator " + stringExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        stringExpression = (Expression<String>) expressions[0];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        if (mode == Changer.ChangeMode.SET) {
            WorldCreator creator = (new WorldCreator(stringExpression.getSingle(event))).copy((WorldCreator) delta[0]);
            //WorldLoader.setCreator(creator);
        } else if (mode == Changer.ChangeMode.DELETE) {
            WorldLoader.removeCreator(stringExpression.getSingle(event));
        }
    }

    @SuppressWarnings("unchecked")
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(WorldCreator.class);
        if (mode == Changer.ChangeMode.DELETE) return CollectionUtils.array();
        return null;
    }
}
