package com.pie.tlatoani.Miscellaneous.TabCompletion;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.server.TabCompleteEvent;

/**
 * Created by Tlatoani on 6/16/16.
 */
public class ExprLastToken extends SimpleExpression<String> {
    @Override
    protected String[] get(Event event) {
        if (event instanceof PlayerChatTabCompleteEvent) {
            return new String[]{((PlayerChatTabCompleteEvent) event).getLastToken()};
        } else if (event instanceof TabCompleteEvent) {
            String buffer = ((TabCompleteEvent) event).getBuffer();
            if (buffer.endsWith(" ")) {
                return new String[]{""};
            }
            String[] tokens = buffer.split(" ");
            return new String[]{tokens[tokens.length - 1]};
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
        if (!ScriptLoader.isCurrentEvent(PlayerChatTabCompleteEvent.class) && !ScriptLoader.isCurrentEvent(TabCompleteEvent.class)) {
            Skript.error("The 'last token' expression can only be used in the 'on chat tab complete' event and the 'on tab complete' event!");
            return false;
        }
        return true;
    }
}
