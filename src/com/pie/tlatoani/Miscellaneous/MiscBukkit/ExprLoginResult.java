package com.pie.tlatoani.Miscellaneous.MiscBukkit;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

/**
 * Created by Tlatoani on 8/16/16.
 */
public class ExprLoginResult extends SimpleExpression<Result> {

    @Override
    protected Result[] get(Event event) {
        return new Result[]{event instanceof PlayerLoginEvent ? ((PlayerLoginEvent) event).getResult() : null};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Result> getReturnType() {
        return Result.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "login result";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return ScriptLoader.isCurrentEvent(PlayerLoginEvent.class);
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        if (event instanceof PlayerLoginEvent) {
            ((PlayerLoginEvent) event).setResult((Result) delta[0]);
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(Result.class);
        return null;
    }
}
