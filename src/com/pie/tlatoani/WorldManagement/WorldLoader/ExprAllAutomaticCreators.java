package com.pie.tlatoani.WorldManagement.WorldLoader;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.WorldManagement.WorldLoader.UtilWorldLoader;
import org.bukkit.WorldCreator;
import org.bukkit.event.Event;

import java.util.Iterator;

/**
 * Created by Tlatoani on 8/16/16.
 */
public class ExprAllAutomaticCreators extends SimpleExpression<WorldCreator> {

    @Override
    protected WorldCreator[] get(Event event) {
        return UtilWorldLoader.getAllCreators().toArray(new WorldCreator[0]);
    }

    @Override
    public Iterator<WorldCreator> iterator(Event event) {
        return UtilWorldLoader.getAllCreators().iterator();
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends WorldCreator> getReturnType() {
        return WorldCreator.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "all automatic creators";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return true;
    }

    public void change(Event arg0, Object[] delta, Changer.ChangeMode mode){
        if (mode == Changer.ChangeMode.ADD) {
            UtilWorldLoader.setCreator((WorldCreator) delta[0]);
        } else if (mode == Changer.ChangeMode.REMOVE) {
            UtilWorldLoader.removeCreator(delta[0] instanceof String ? (String) delta[0] : ((WorldCreator) delta[0]).name());
        } else {
            UtilWorldLoader.clearAllCreators();
        }
    }

    @SuppressWarnings("unchecked")
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.ADD) return CollectionUtils.array(WorldCreator.class);
        if (mode == Changer.ChangeMode.REMOVE) return CollectionUtils.array(String.class, WorldCreator.class);
        if (mode == Changer.ChangeMode.DELETE) return CollectionUtils.array();
        return null;
    }
}
