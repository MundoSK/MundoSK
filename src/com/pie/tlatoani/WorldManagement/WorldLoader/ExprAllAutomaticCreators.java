package com.pie.tlatoani.WorldManagement.WorldLoader;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.WorldCreator.WorldCreatorData;
import org.bukkit.WorldCreator;
import org.bukkit.event.Event;

import java.util.Iterator;

/**
 * Created by Tlatoani on 8/16/16.
 */
public class ExprAllAutomaticCreators extends SimpleExpression<WorldCreatorData> {

    @Override
    protected WorldCreatorData[] get(Event event) {
        return WorldLoader.getAllCreators();
    }

    @Override
    public Iterator<WorldCreatorData> iterator(Event event) {
        return WorldLoader.getCreatorIterator();
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends WorldCreatorData> getReturnType() {
        return WorldCreatorData.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "all automatic creators";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        if (mode == Changer.ChangeMode.ADD) {
            WorldLoader.setCreator((WorldCreatorData) delta[0]);
        } else if (mode == Changer.ChangeMode.REMOVE) {
            WorldLoader.removeCreator(delta[0] instanceof String ? (String) delta[0] : ((WorldCreatorData) delta[0]).name);
        } else {
            WorldLoader.clearAllCreators();
        }
    }

    @SuppressWarnings("unchecked")
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.ADD) return CollectionUtils.array(WorldCreatorData.class);
        if (mode == Changer.ChangeMode.REMOVE) return CollectionUtils.array(String.class, WorldCreatorData.class);
        if (mode == Changer.ChangeMode.DELETE) return CollectionUtils.array();
        return null;
    }
}
