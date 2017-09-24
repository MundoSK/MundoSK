package com.pie.tlatoani.Miscellaneous.TabCompletion;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;

/**
 * Created by Tlatoani on 6/29/17.
 */
public class ExprLastTokenOld extends SimpleExpression<String> {
    @Override
    protected String[] get(Event event) {
        if (event instanceof PlayerChatTabCompleteEvent) {
            return new String[]{((PlayerChatTabCompleteEvent) event).getLastToken()};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "last token";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(PlayerChatTabCompleteEvent.class)) {
            Skript.error("The 'last token' expression can only be used in the 'on chat tab complete' event!");
            return false;
        }
        return true;
    }
}
